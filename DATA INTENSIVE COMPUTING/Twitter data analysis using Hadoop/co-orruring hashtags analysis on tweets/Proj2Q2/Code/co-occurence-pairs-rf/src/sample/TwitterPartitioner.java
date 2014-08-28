package sample;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
 public class TwitterPartitioner extends Partitioner<Text, IntWritable> {
 
        @Override
        public int getPartition(Text key, IntWritable value, int numReduceTasks) {
 
            String tweet = key.toString();
            
            //this is done to avoid performing mod with 0
            if(numReduceTasks == 0)
                return 0;
 
            if(Character.toString(tweet.charAt(1)).matches("[A-Ga-g]")){               
                return 1 % numReduceTasks;
            }
            if(Character.toString(tweet.charAt(1)).matches("[H-Oh-o]")){
               
                return 2 % numReduceTasks;
            }
            //otherwise assign partition 0
            else
                return 0;
           
        }
    }
