package dalgo;



import java.io.IOException;
import java.util.EmptyStackException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TokenizerMapper extends Mapper<Object, Text,LongWritable, Text >{

	//private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException  {
		StringTokenizer itr = new StringTokenizer(value.toString());
		int counter=0;
		int node=0;
		int dist=0;
		String nodelist="0:0";
		
		while (itr.hasMoreTokens()) {
			String next=itr.nextToken();
			
			if(counter==0)
				node=Integer.parseInt(next.trim());
			
			if(counter==1)
				dist=Integer.parseInt(next.trim());
			
				if(counter==2)
					nodelist=next;
				
					counter++;
				

			
				
		}
	//	String valll=value.toString();
		
	if(node!=0){

			
			String[] nodes= nodelist.split("\\:");
			word.set(String.valueOf(dist)+"  "+nodelist);
			context.write(new LongWritable(node),word);
			word.clear();
			
			for(String nod : nodes){
			
				
				word.set(String.valueOf(dist+1));
			context.write(new LongWritable(Integer.parseInt(nod)),word);
				word.clear();
			}
			//context.write(word, one);
			}
		
	}
}