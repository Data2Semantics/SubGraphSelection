#!/bin/bash
function hadoopLs {
	hadoopLs=()
	echo "hadoop fs -ls $1";
	dirListing=`hadoop fs -ls $1`;
	for word in ${dirListing} ; do
 		if [[ $word =~ ^/ ]];then 
	    	hadoopLs+=(${word})
	    fi
	done
}  
[ -z "$PIG_SCRIPTS" ] && echo "PIG_SCRIPTS variable not set. Exiting" && exit 1;
if [ -z "$1" ];then
	echo "at least 1 argument required (dataset). (second arg enables validation check)"
	exit;
fi
dataset=$1

#create dict
hadoopLs "$dataset/rewrite";
for rewrite in "${hadoopLs[@]}"; do
	if [[ "$rewrite" != "_dict" ]]; then
		if [[ "$rewrite" != "_long" ]]; then
			pig $PIG_SCRIPTS/utils/edgeListCreateDictHash.py $rewrite;
		fi
	fi
done
