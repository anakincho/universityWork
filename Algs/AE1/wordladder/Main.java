import java.io.*;
import java.util.*;

public class Main{
	
	public static void main(String[] args) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();

		String inputFileName = args[0];
		String word1= args[1];

		String word2 = args[2];
		FileReader reader = new FileReader(inputFileName);
		Scanner in = new Scanner(reader);

		List<String> words = new ArrayList<String>();

		// add the words to the list
		while(in.hasNext()){
			words.add(in.nextLine()); 
		}

		Graph g = new Graph(words.size());
		String str1, str2;
		int startIndex = 0, endIndex = 0;

		for(int i = 0; i < words.size(); i++){
			str1 = words.get(i);
			if(str1.equals(word1)) startIndex = i;
			if(str1.equals(word2)) endIndex = i;
			g.getVertex(i).setWord(str1); // add the word to the vertex in the graph
			for(int j=0; j< words.size(); j++){ // get all the edges for vertex with str1
				str2 = words.get(j);
				if(DifferByOne(str1, str2)){
					g.getVertex(i).addToAdjList(j); // create the "edge"
				}
			}
		}

		reader.close();

		// do the BFS
		g.bfs(g.getVertex(startIndex), g.getVertex(endIndex));

		long end= System.currentTimeMillis();
		System.out.println("\nElapsed Time: " + (end-start) + " milliseconds");	
	}

	/**
	* Method public static boolean DifferByOne(String arg1, String arg2)
	* Checks if the two strings differ by only 1 letter, if so return true
	*/
	public static boolean DifferByOne(String str, String str2){
		int letterDifference = 0;
		char[] firstStr = str.toCharArray();
		char[] secondStr = str2.toCharArray();
		for(int i = 0; i < str.length(); i++){
			if(firstStr[i] != secondStr[i]) letterDifference ++;
		}

		if(letterDifference == 1) return true;

		return false;

	}
}