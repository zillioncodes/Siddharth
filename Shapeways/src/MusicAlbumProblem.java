import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class MusicAlbumProblem {

	public static void main(String[] args) {
		// least count decided
		int LEAST_COUNT = 50;
		// Input file path-- should be changed according to the location of file
		String Inputfilepath = "G:/workspace/Shapeways/Artist_lists_small.txt";
		// Output file path--- Should be changed according to where you want to
		// generate your result
		String OutputfilePath = "G:/workspace/Shapeways/Result.txt";

		// can pass Input and output path as first argument and second arguments
		if (args.length == 2) {
			Inputfilepath = args[0];
			OutputfilePath = args[1];
		} if (args.length > 2) {
			System.out.println("Wrong number of args");
			return;
		}

		// Using scanner to read the file
		Scanner scan;
		try {
			scan = new Scanner(new File(Inputfilepath));
		} catch (FileNotFoundException e) {

			e.printStackTrace();

			return;
		}

		// for holding all the nodes- Artists
		ConcurrentHashMap<String, AuthorIndex> athr = new ConcurrentHashMap<String, AuthorIndex>();

		// for holding edges --- all the edges from one Artist to another
		ConcurrentHashMap<String, Edge> edges = new ConcurrentHashMap<String, Edge>();

		// Graph Formation started

		while (scan.hasNextLine()) {

			String[] author = scan.nextLine().split("\\,");

			for (int i = 0; i < author.length; i++) {
				for (int j = i + 1; j < author.length; j++) {
					if (!athr.containsKey(author[i])) {
						AuthorIndex a1 = new AuthorIndex(author[i]);
						athr.put(author[i], a1);
					} else {
					}
					if (!athr.containsKey(author[j])) {
						AuthorIndex a1 = new AuthorIndex(author[j]);
						athr.put(author[j], a1);
					} else {
					}

					String token = author[i] + "," + author[j];
					String tokenop = author[j] + "," + author[i];
					Edge n;
					if ((n = edges.get(tokenop)) != null
							|| (n = edges.get(token)) != null) {
						n.increment();
						athr.get(author[i]).edges.put(n, "");
						athr.get(author[j]).edges.put(n, "");
					} else {
						Edge eg = new Edge(athr.get(author[i]),
								athr.get(author[j]));
						edges.put(token, eg);
						athr.get(author[i]).edges.put(eg, "");
						athr.get(author[j]).edges.put(eg, "");
					}

				}

			}

		}// Graph formation is done

		// file for holding result
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OutputfilePath));

			// DFS (Depth first search) on the graph with allowing edges greater than LEAST_COUNT only.

			Set<String> set = athr.keySet();
			AuthorIndex enrtypoint;
			for (String s : set) {
				// entry point in the graph or entry point for all disconnected
				// graphs
				enrtypoint = athr.get(s);
				// deleting the entry point from Map of authors so it can't be
				// traced again

				athr.remove(enrtypoint);
				Stack<Edge> edg = new Stack<Edge>();

				// Adding all edges to stack with value greater than LEAST_COUNT
				while (enrtypoint.edges.lastKey().count > LEAST_COUNT) {
					edg.add(enrtypoint.edges.pollLastEntry().getKey());
				}
				AuthorIndex presentNode = enrtypoint;
				while (!edg.isEmpty()) {

					Edge e = edg.pop();
					/*
					 * System.out.print(e.left.author + "," + e.right.author +
					 * "\n no of co-occurance:" + e.count+"\n");
					 */
					out.println(e.left.author + "," + e.right.author
							+ "\n :        no of co-occurance:" + e.count + "\n");

					// removing edges so can't be traced again
					e.left.edges.remove(e);
					e.right.edges.remove(e);
					// Updating present node value
					if (e.left.author == presentNode.author) {
						presentNode = e.right;
						athr.remove(presentNode.author);
					}
					if (e.right.author == presentNode.author) {
						presentNode = e.left;
						athr.remove(presentNode.author);
					}

					// Adding edges of the present node into the stack with
					// value greater than LEAST_COUNT
					while (presentNode.edges.lastKey().count > LEAST_COUNT) {
						edg.add(presentNode.edges.pollLastEntry().getKey());
					}

				}

			}
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

	}

}
