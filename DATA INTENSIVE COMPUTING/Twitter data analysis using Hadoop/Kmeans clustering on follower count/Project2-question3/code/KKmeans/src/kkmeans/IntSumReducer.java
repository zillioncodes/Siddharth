package kkmeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import kkmeans.Kkmeans.CKmean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IntSumReducer extends
		Reducer<Text, LongWritable, LongWritable, Text> {
	private Text word = new Text();
	
	static final String l="LOW";
	static final String m="MED";
	static final String h="HIGH";

	public void reduce(Text key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		
		Configuration conf = new Configuration();
		FileSystem fsi = FileSystem.get(conf);
		
		
		
		long sum = 0;
		String clus = key.toString();
		int counter = 0;

		Iterator<LongWritable> it2 = values.iterator();
		ArrayList<String> t = new ArrayList<String>();

		while (it2.hasNext()) {
			LongWritable tx3 = it2.next();
			t.add(tx3.toString().trim());
			long val = Integer.parseInt(tx3.toString());

			counter++;

			sum = sum + val;

		}
		

		long avg = (long) sum / counter;

		counter = 0;
		
		////////////////////////////////////Switch///////////////////////////
		switch(clus){
		case l:
			
			FSDataInputStream inr = fsi.open(new Path("/kmeans/centriod1.txt"));
			 BufferedReader bin=new BufferedReader(new InputStreamReader(inr));
			String line = bin.readLine();
			String cl = "";
			while (line != null) {
				cl = line;
				line = bin.readLine();
			}
			long i = Integer.parseInt(cl.trim());
			bin.close();
			
			if(i!=avg)
			context.getCounter(CKmean.ISREADY).increment(1);
			

		

			FSDataOutputStream in = fsi.append(new Path("/kmeans/centriod1.txt"));
			PrintWriter pr=new PrintWriter(in) ;
			pr.println(""+avg);
			pr.close();
			break;
			
			
		case m:
			
			FSDataInputStream inr1 = fsi.open(new Path("/kmeans/centriod2.txt"));
			 BufferedReader bin1=new BufferedReader(new InputStreamReader(inr1));
			String line1 = bin1.readLine();
			String cl1 = "";
			while (line1 != null) {
				cl1 = line1;
				line1 = bin1.readLine();
			}
			long i1 = Integer.parseInt(cl1.trim());
			bin1.close();
			if(i1!=avg)
			context.getCounter(CKmean.ISREADY).increment(1);
			
			
		FSDataOutputStream in1 = fsi.append(new Path("/kmeans/centriod2.txt"));
		PrintWriter pr1=new PrintWriter(in1) ;
		pr1.println(""+avg);
		pr1.close();
			break;
			
			
		case h:
			FSDataInputStream inr2 = fsi.open(new Path("/kmeans/centriod3.txt"));
			 BufferedReader bin2=new BufferedReader(new InputStreamReader(inr2));
			String line2 = bin2.readLine();
			String cl2 = "";
			while (line2 != null) {
				cl2 = line2;
				line2 = bin2.readLine();
			}
			long i2 = Integer.parseInt(cl2.trim());
			bin2.close();
			if(i2!=avg)
			context.getCounter(CKmean.ISREADY).increment(1);
			
			
			
			
			FSDataOutputStream in2 = fsi.append(new Path("/kmeans/centriod3.txt"));
			PrintWriter pr2=new PrintWriter(in2) ;
			pr2.println(""+avg);
			pr2.close();
			break;
		
		}
		
		
		
		
		
		
		
		
		
		
		////////////////////////////////////////////////////////////////
		


		for (String text : t) {
			long val = Integer.parseInt(text);

			word.set(clus + " " + avg);

			context.write(new LongWritable(val), word);
			word.clear();

		}

		
		sum = 0;

	}
}
