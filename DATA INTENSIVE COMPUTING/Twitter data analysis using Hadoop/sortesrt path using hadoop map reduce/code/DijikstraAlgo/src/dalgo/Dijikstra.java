package dalgo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dijikstra {
	static enum Cdk { ISREADY }


	private static final transient Logger LOG = LoggerFactory.getLogger(Dijikstra.class);

	public static void main(String[] args) throws Exception {
		String home = //"/home/hduser";

				System.getProperty("user.home");
		
		
		Configuration conf = new Configuration();		

		LOG.info("HDFS Root Path: {}", conf.get("fs.defaultFS"));
		LOG.info("MR Framework: {}", conf.get("mapreduce.framework.name"));
		/* Set the Input/Output Paths on HDFS */
		String inputPath = "/input";
		String outputPath = "/output";

		/* FileOutputFormat wants to create the output directory itself.
		 * If it exists, delete it:
		 */

		
		boolean sucess=true;
		int counter=0;
		while((sucess)){
			deleteFolder(conf,outputPath);
			
			Job job = Job.getInstance(conf);

			job.setJarByClass(Dijikstra.class);
			job.setMapperClass(TokenizerMapper.class);
			job.setCombinerClass(IntSumReducer.class);
			job.setReducerClass(IntSumReducer.class);
			job.setOutputValueClass(Text.class);
			
			job.setOutputKeyClass(LongWritable.class);
			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));
			boolean s= job.waitForCompletion(true);
			
			Counters cns=	job.getCounters();
			long k=cns.findCounter(Cdk.ISREADY).getValue();
			
			if(k==0)break;
			
			cns.findCounter(Cdk.ISREADY).setValue(0);
			
			
		counter++;
		FileSystem fs =  FileSystem.get(conf);

          
		fs.delete(new Path(inputPath+"/input.txt"), true);
		fs.copyToLocalFile(new Path(outputPath+"/part-r-00000"),new Path(home+"/input.txt"));
		fs.copyFromLocalFile(new Path(home+"/input.txt"),new Path(inputPath+"/"));
		
		FileSystem fsl =  FileSystem.getLocal(conf);
		fsl.delete(new Path(home+"/input.txt"));

           

		}
			System.exit(0);


	}
	
	/**
	 * Delete a folder on the HDFS. This is an example of how to interact
	 * with the HDFS using the Java API. You can also interact with it
	 * on the command line, using: hdfs dfs -rm -r /path/to/delete
	 * 
	 * @param conf a Hadoop Configuration object
	 * @param folderPath folder to delete
	 * @throws IOException
	 */
	private static void deleteFolder(Configuration conf, String folderPath ) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(folderPath);
		if(fs.exists(path)) {
			fs.delete(path,true);
		}
	}
}