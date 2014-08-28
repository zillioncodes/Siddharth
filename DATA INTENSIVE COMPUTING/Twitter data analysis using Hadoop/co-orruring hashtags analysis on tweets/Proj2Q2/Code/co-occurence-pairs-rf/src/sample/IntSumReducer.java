package sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IntSumReducer 
extends Reducer<Text,IntWritable,Text,DoubleWritable> {
	private DoubleWritable result = new DoubleWritable();
	private Map<String, Double> map = new HashMap<String, Double>();
	
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		double sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		
		if(key.toString().startsWith("!")){
			map.put(key.toString(), sum);
		} else {
			String[] hashTag = key.toString().split("#");
			result.set(sum/map.get("!" + hashTag[1]));
			//result.set(sum);
			context.write(key, result);
		}
		
	}
}
