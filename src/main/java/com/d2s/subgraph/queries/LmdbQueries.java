package com.d2s.subgraph.queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.data2semantics.query.QueryCollection;
import org.data2semantics.query.filters.DescribeFilter;
import org.data2semantics.query.filters.GraphClauseFilter;
import org.data2semantics.query.filters.QueryFilter;

import com.d2s.subgraph.queries.filters.SimpleBgpFilter;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class LmdbQueries extends GetQueries {
	public static String QUERY_FILE = "src/main/resources/lmdbQueries.txt";
	public static String CSV_COPY = "src/main/resources/lmdb_queries.csv";
	public static String PARSE_QUERIES_FILE = "src/main/resources/lmdb_queries.arraylist";
	private static boolean ONLY_UNIQUE = true;

	public LmdbQueries(QueryFilter... filters) throws IOException {
		this(true, filters);
	}

	public LmdbQueries(boolean useCacheFile, QueryFilter... filters) throws IOException {
		File cacheFile = new File(PARSE_QUERIES_FILE);
		if (useCacheFile && cacheFile.exists()) {
			System.out.println("WATCH OUT! getting queries from cache file. might be outdated!");
			readQueriesFromCacheFile(cacheFile);
		}
		if (queries == null || queries.size() == 0 || (maxNumQueries > 0 && maxNumQueries != queries.size())) {
			System.out.println("parsing lmdb query logs");
			this.filters = new ArrayList<QueryFilter>(Arrays.asList(filters));
			parseLogFile(new File(QUERY_FILE));
			if (ONLY_UNIQUE) {
				// we have stored stuff in hashmap to keep queries unique. now get them as regular queries
				queries = new ArrayList<Query>(queriesHm.values());
				queriesHm.clear();
			}
			saveCsvCopy(new File(CSV_COPY));
			saveQueriesToCacheFile();
		}
	}

	private void saveQueriesToCacheFile() throws IOException {
		FileWriter writer = new FileWriter(PARSE_QUERIES_FILE);
		for (Query query : queries) {
			writer.write(URLEncoder.encode(query.toString(), "UTF-8") + "\n");
		}
		writer.close();
	}
	
	private void readQueriesFromCacheFile(File cacheFile) throws QueryParseException, IOException {
		Scanner sc = new Scanner(cacheFile);
		int queryIndex = 0;
		while(sc.hasNext()) {
			String line = sc.next();
			String queryString = line.trim();
			if (queryString.length() > 0) {
				Query query = Query.create(URLDecoder.decode(queryString, "UTF-8"), new QueryCollection());
				query.setQueryId(queryIndex);
				queries.add(query);
				queryIndex++;
			}
		}
		sc.close();
	}

	private void parseLogFile(File textFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(textFile));
		String query;
		while ((query = br.readLine()) != null) {
			if (query.length() > 0) {
				addQueryToList(query);
			}
			if (queries.size() > maxNumQueries) {
				break;
			}
		}
		br.close();
	}

	private void addQueryToList(String queryString) {
		try {
			Query query = new Query(QueryFactory.create(queryString));
			if (checkFilters(query)) {
//				System.out.println(query.getQueryString("http://lmdb"));
				if (ONLY_UNIQUE) {
					if (queriesHm.containsKey(query)) {
						duplicateQueries++;
					} else {
						if (hasResults(query)) {
							query.setQueryId(validQueries);
							queriesHm.put(query, query);
							validQueries++;
						} else {
							noResultsQueries++;
						}
					}
				} else {
					queries.add(query);
					validQueries++;
				}
				query.generateQueryStats();
			} else {
				filteredQueries++;
			}
		} catch (QueryParseException e) {
			// could not parse query, probably a faulty one. ignore!
			invalidQueries++;
		} catch (QueryBuildException e) {
			// could not parse query, probably a faulty one. ignore!
			invalidQueries++;
		} catch (QueryExceptionHTTP e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			//query wrong or something. ignore
	
		}
	}

	public static void main(String[] args) {

		try {

			LmdbQueries lmdbQueries = new LmdbQueries(false, new GraphClauseFilter(), new SimpleBgpFilter(), new DescribeFilter());
			System.out.println(lmdbQueries.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
