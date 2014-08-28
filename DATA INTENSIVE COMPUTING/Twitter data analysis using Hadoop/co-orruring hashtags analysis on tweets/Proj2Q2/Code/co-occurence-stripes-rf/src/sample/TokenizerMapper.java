package sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TokenizerMapper extends Mapper<Object, Text, Text, MapWritable>{

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString());
		String hashTag = null;
		List<String> hashTags = new ArrayList<String>();
		while (itr.hasMoreTokens()) {
			
			hashTag = itr.nextToken();
			//hashTags = new ArrayList<String>();
			
			if(hashTag.startsWith("#")){
				hashTags.add(hashTag);
			}
			
			/*word.set(itr.nextToken());
			context.write(word, one);*/
		}
		
		if (hashTags.size() > 1) {
			Collections.sort(hashTags);

			for (int j = 0; j < hashTags.size(); j++) {
				word.set(hashTags.get(j));
				MapWritable map = new MapWritable();
				for (int i = j + 1; i < hashTags.size(); i++) {
					if (map.containsKey(hashTags.get(i))) {
						int val = Integer.parseInt(map.get(hashTags.get(i))
								.toString());
						val++;
						map.put(new Text(hashTags.get(i)), new IntWritable(val));
					} else {
						map.put(new Text(hashTags.get(i)), one);
					}

				}
				context.write(new Text(hashTags.get(j)), map);
			}
		}
	}
}