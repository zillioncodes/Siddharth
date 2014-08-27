#!/bin/bash
cd ~
hdfs dfs -rm /input/*
hdfs dfs -copyFromLocal $1 /input/input.txt
hadoop jar Dalgo.jar dalgo.Dijikstra
#wait
#rm /home/hduser/Res.txt
#hdfs dfs -copyToLocal  /output/part-r-00000 /home/hduser/Res.txt

