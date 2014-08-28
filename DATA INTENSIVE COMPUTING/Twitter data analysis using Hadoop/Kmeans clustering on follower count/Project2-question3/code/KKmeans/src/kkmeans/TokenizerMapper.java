package kkmeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EmptyStackException;
import java.util.StringTokenizer;

//import kkmeans.Kkmeans.CKmean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TokenizerMapper extends Mapper<Object, Text, Text, LongWritable> {
	
	public TokenizerMapper() throws IOException
	
	{
		

	Configuration conf = new Configuration();
	FileSystem fsi = FileSystem.get(conf);
	
	

	FSDataInputStream in = fsi.open(new Path("/kmeans/centriod1.txt"));
	 BufferedReader bin=new BufferedReader(new InputStreamReader(in));
	String line = bin.readLine();
	String cl = "";
	while (line != null) {
		cl = line;
		line = bin.readLine();
	}
	low = Integer.parseInt(cl.trim());
	
	

	in = fsi.open(new Path("/kmeans/centriod2.txt"));
	  bin=new BufferedReader(new InputStreamReader(in));

	cl = "";
	line = bin.readLine();
	while (line != null) {
		cl = line;
		line = bin.readLine();
	}
	mid = Integer.parseInt(cl.trim());
	
	
	

	in = fsi.open(new Path("/kmeans/centriod3.txt"));
	  bin=new BufferedReader(new InputStreamReader(in));
	cl = "";
	line = bin.readLine();
	while (line != null) {
		cl = line;
		line = bin.readLine();
	}
	high = Integer.parseInt(cl.trim());
	
	}
	
	
	
	
	long low ;
	long mid ;
	long high ;

	static final String l = "LOW";
	static final String m = "MED";
	static final String h = "HIGH";
	
	

	private Text word = new Text();
	long node = -1;
	String clusterID = "";

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {


		StringTokenizer itr = new StringTokenizer(value.toString());
		int counter = 0;
		String type = "";
		long cluster = -1;
		while (itr.hasMoreTokens()) {
			String next = itr.nextToken();

			if (counter == 0)
				node = Integer.parseInt(next.trim());

			if (counter == 1)
				type = next.trim();

			if (counter == 2)
				cluster = Integer.parseInt(next.trim());

			counter++;

		}



		if (node != -1) {

			long diffL = Math.abs(node - low);
			//diffL = (long) Math.pow(diffL, 2);

			long diffM = Math.abs(node - mid);
			//diffM = (long) Math.pow(diffM, 2);

			long diffH = Math.abs(node - high);
			//diffH = (long) Math.pow(diffH, 2);

			if ((diffL < diffH) && (diffL < diffM)) {
				clusterID = l;
			} else if ((diffH < diffL) && (diffH < diffM)) {
				clusterID = h;

			} else if ((diffM < diffL) && (diffM < diffH)) {
				clusterID = m;

			}
			/*
			 * if(clusterID.equalsIgnoreCase("")){ throw new IOException(); }
			 */
			word.set(clusterID);
			context.write(word, new LongWritable(node));
			word.clear();
		}

	}
}