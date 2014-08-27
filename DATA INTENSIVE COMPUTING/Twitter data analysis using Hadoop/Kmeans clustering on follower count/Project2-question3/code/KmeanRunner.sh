#!/bin/bash
cd ~
hdfs dfs -rm /input/*
hdfs dfs -copyFromLocal $1 /input/input.txt
hadoop jar Kkmeans.jar kkmeans.Kkmeans


