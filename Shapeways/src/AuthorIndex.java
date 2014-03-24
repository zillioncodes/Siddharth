import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;


public class AuthorIndex {

	String author;
	ArrayList<Integer> users=new ArrayList<Integer>();
	TreeMap<Edge,String> edges=new TreeMap<Edge,String>();
	public AuthorIndex(){
	}
	public AuthorIndex(String author){
		this.author=author;
	}
	
	
}
