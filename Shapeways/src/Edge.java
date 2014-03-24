
public class Edge implements Comparable<Edge> {
	AuthorIndex left;
	AuthorIndex right;
	int count;

	public Edge(AuthorIndex a, AuthorIndex b){
	count=1;
	left=a;
	right=b;
	}
public void increment(){
	count++;
	
}
@Override
public int compareTo(Edge b){
	return (this.count-b.count);
}

}
