package dalgo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import dalgo.Dijikstra.Cdk;

public class IntSumReducer extends
		Reducer<LongWritable, Text, LongWritable, Text> {
	// private IntWritable result = new IntWritable();
	private Text word = new Text();

	public void reduce(LongWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		//ArrayList<Integer> alldist = new ArrayList<Integer>();
		String nodelist = "";
		int low = 1000000;
		int counter = 0;
		String dist = "0";
		String spkey="";
		for (Text tx : values) {

			if (tx.toString().equalsIgnoreCase(""))
				continue;
			StringTokenizer itr = new StringTokenizer(tx.toString());
			while (itr.hasMoreTokens()) {
				String next = itr.nextToken();

				if (counter == 0)
					dist = next;
				if (counter == 1)
					nodelist = next;

				counter++;
			}
			
			if(counter==2)
				spkey=dist;

			counter = 0;
			low = Math.min(Integer.parseInt(dist.trim()), low);
		//	alldist.add(Integer.parseInt(dist.trim()));
		}
		
		if(Integer.parseInt(spkey.trim())!=low)
			context.getCounter(Cdk.ISREADY).increment(1);


		word.set(low + " " + nodelist);
		
		context.write(key, word);
	}
}
