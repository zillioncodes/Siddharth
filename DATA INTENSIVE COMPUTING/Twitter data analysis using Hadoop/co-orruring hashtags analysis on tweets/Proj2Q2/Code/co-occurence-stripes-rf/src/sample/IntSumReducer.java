package sample;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IntSumReducer 
extends Reducer<Text,MapWritable,Text,DoubleWritable> {
	private DoubleWritable result = new DoubleWritable();
	private Text finalKey = new Text();
	
	public void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
		MapWritable map = new MapWritable();
		for(MapWritable m : values){
			for(Entry entry:m.entrySet()){
				if (map.containsKey(entry.getKey())) {
					int val = ((IntWritable) entry.getValue()).get();
					val += ((IntWritable) map.get(entry.getKey())).get();
					map.put(new Text(entry.getKey().toString()), new IntWritable(val));
				} else {
					map.put(new Text(entry.getKey().toString()), ((IntWritable) entry.getValue()));
				}
			}
		}
		double sum = 0;
		for(Entry entry : map.entrySet()){
			sum += ((IntWritable) entry.getValue()).get();
		}
		
		for(Entry entry : map.entrySet()){
			finalKey.set(key.toString() + entry.getKey().toString());
			result.set(((IntWritable) entry.getValue()).get()/sum);
			context.write(finalKey, result);
		}
		
		
	}
}
