package sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString());
		String hashTag = null;
		List<String> hashTags = new ArrayList<String>();
		while (itr.hasMoreTokens()) {
			
			hashTag = itr.nextToken();
			//hashTags = new ArrayList<String>();
			
			if(hashTag.startsWith("#") && hashTag.length() > 1){
				hashTags.add(hashTag);
			}
			
			/*word.set(itr.nextToken());
			context.write(word, one);*/
		}
		
		if (hashTags.size() > 1) {
			Collections.sort(hashTags);

			for (int i = 0; i < hashTags.size(); i++) {
				for (int j = i + 1; j < hashTags.size(); j++) {
					word.set(hashTags.get(i).replace("#", "!"));
					context.write(word, one);
					word.set(hashTags.get(i) + hashTags.get(j));
					context.write(word, one);
					
				}
			}
		}
	}
}