package com.d2s.subgraph.eval.experiments;

import java.io.IOException;
import org.data2semantics.query.filters.ConstructFilter;
import org.data2semantics.query.filters.DescribeFilter;
import org.data2semantics.query.filters.GraphClauseFilter;
import com.d2s.subgraph.eval.EvaluateGraph;
import com.d2s.subgraph.queries.GetQueries;
import com.d2s.subgraph.queries.LgdQueries;
import com.d2s.subgraph.queries.filters.SimpleBgpFilter;



public class LgdExperimentSetup implements ExperimentSetup {
	public static String GOLDEN_STANDARD_GRAPH = "http://lgd";
	private static String GRAPH_PREFIX = "lgd_";
	private static String QUERY_TRIPLES_DIR = "lgdQueryTriples";
	private static String QUERY_RESULTS_DIR = "lgdQueryResults";
	private static String EVAL_RESULTS_DIR = "lgdResults";
	private static boolean PRIVATE_QUERIES = true;
	private static int MAX_NUM_QUERIES = 100;
	private GetQueries queries;
	
	public LgdExperimentSetup() throws IOException {
		queries = new LgdQueries(true, MAX_NUM_QUERIES, new DescribeFilter(), new SimpleBgpFilter(), new GraphClauseFilter(), new ConstructFilter());
	}
	
	public String getGoldenStandardGraph() {
		return GOLDEN_STANDARD_GRAPH;
	}
	public String getGraphPrefix() {
		return GRAPH_PREFIX;
	}
	public GetQueries getQueries() {
		return queries;
	}
	

	public String getEvalResultsDir() {
		return EVAL_RESULTS_DIR;
	}
	public int getMaxNumQueries() {
		return MAX_NUM_QUERIES;
	}
	public String getEndpoint() {
		return EvaluateGraph.OPS_VIRTUOSO;
	}
	public String getQueryTriplesDir() {
		return QUERY_TRIPLES_DIR;
	}
	public String getQueryResultsDir() {
		return QUERY_RESULTS_DIR;
	}
	public boolean privateQueries() {
		return PRIVATE_QUERIES;
	}
}
