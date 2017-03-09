import java.io.*;
import java.util.*;

/**
 program to find word ladder with shortest distance for two words in a dictionary
 distance between elements of the word ladder is the absolute difference in the
 positions of the alphabet of the non-matching letter
 */
public class Main {

	public static void main(String[] args) throws IOException {

		long start = System.currentTimeMillis();

		String inputFileName = args[0]; // dictionary
		String word1 = args[1]; // first word
		String word2 = args[2]; // second word
  
		FileReader reader = new FileReader(inputFileName);
		Scanner in = new Scanner(reader);
		
		// read in the data here
		List<String> words = new ArrayList<String>();

        // add the words to the list
		while(in.hasNext()){
			words.add(in.nextLine());
		}

		String str1, str2;
		int startIndex = 0; 
		int endIndex = 0;
		int numVertex = 0;
		
		reader.close();

        // create graph here
       	Graph g = new Graph(words.size());

		for(int i = 0; i < words.size(); i++){
			str1 = words.get(i);
			if(str1.equals(word1)) startIndex = i;
			if(str1.equals(word2)) endIndex = i;
			g.getVertex(i).setWord(str1); // add the word to the vertex in the graph
			for(int j=0; j< words.size(); j++){
				str2 = words.get(j);
				if(DifferByOne(str1, str2) != 0){
					g.getVertex(i).addToAdjList(j, DifferByOne(str1, str2)); // create the "edge"
				}
			}
		}

		Dijkstra(g.getVertex(startIndex), g.getVertex(endIndex), g); // call the dijkstra alg

        // end timer and print total time
		long end = System.currentTimeMillis();
		System.out.println("\nElapsed time: " + (end - start) + " milliseconds");
	}

    /**
    * Method public static void Dijkstra(Vertex arg1, Vertex arg2, Graph arg3)
    * applies the dijkstra algorithm to the graph using the start vertex as a source
    * sets the predecessors for each vertex in the set
    * finds the shortest distance between the start and end vertices
    * finds the shortest path between the start and end vertices
    * prints out the shortest distance, path
    */
	public static void Dijkstra(Vertex start, Vertex end, Graph g){
        Set<Vertex> S = new HashSet<Vertex>();
        Set<Vertex> notS = new HashSet<Vertex>();
        Map<Vertex, Integer> distance = new HashMap<Vertex, Integer>();

        distance.put(start, 0); // append the starting vertex with obvious distance(weight) 0
        notS.add(start);
        while (notS.size() > 0) {
            Vertex node = getMinimum(notS, distance);
            S.add(node);
            notS.remove(node);
            findMinimalDistances(node, g, distance, notS);
        }

        LinkedList<Vertex> path = getPath(end, g);
        if(path != null){
        	String result = "";
	        for (Vertex vertex : path) {
	        	result += vertex.getWord()+"->";
	        }
	        if (result.endsWith("->")) {
	  			result = result.substring(0, result.length() - 2);
			}
			System.out.println("Minimum Distance: "+distance.get(end));
			System.out.println("Words in path: "+(path.size()-1));
			System.out.println("Path: "+result);

    	} else System.out.println("No Ladder");

		//return null;
	}

    /**
    * Method public static LinkedList<Vertex> getPath(Vertex arg1, Graph arg2)
    * returns the path from the end vertex to the start vertex
    */
    public static LinkedList<Vertex> getPath(Vertex target, Graph g) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        if (step.getPredecessor() == -1) { // if the 2 vertices are not connected (no path)
            return null;
        }
        path.add(step);
        // get each predecessor and append to the list
        while (step.getPredecessor() != -1) {
            step = g.getVertex(step.getPredecessor()); 
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    /**
    * Method public static Vertex getMinimum(Set<Vertex> arg1, Map<Vertex, Integer> distance)
    * returns the vertex with minimum distance
    */
	public static Vertex getMinimum(Set<Vertex> vertexes, Map<Vertex, Integer> distance) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
	        if (minimum == null) {
				minimum = vertex;
	        } else {
	            if (getShortestDistance(vertex, distance) < getShortestDistance(minimum, distance)) {
                    minimum = vertex;
	            }
	        }
        }
        return minimum;
    }

    /**
    * Method public static int getDistance(Vertex arg1, Vertex arg2, Graph arg3)
    * returns the distance(weight) of the edge between the two vertices
    */
    public static int getDistance(Vertex node, Vertex target, Graph g) {
    	LinkedList<AdjListNode> adjList = node.getAdjList();
    	for(AdjListNode i : adjList){
    		int index = i.getVertexNumber();
    		if(index == target.getIndex()) return i.getWeight();
    	}
        //should not come here
    	return 0;
    }

    /**
    * Method public static void findMinimalDistances(Vertex arg1, Graph arg2, Map<Vertex, Integer> arg3, Set<Vertex> arg4)
    * gets the adjacent list for the given node, finds the shortest distance and updates the
    * distances map, and then sets the predecessor of the adjacent vertex
    */
    public static void findMinimalDistances(Vertex node, Graph g, Map<Vertex, Integer> distance, Set<Vertex> notS) {
    	LinkedList<AdjListNode> adjList = node.getAdjList();
    	List<Vertex> adjacentNodes = new ArrayList<Vertex>();
    	for(AdjListNode i : adjList){
    		int index = i.getVertexNumber();
			adjacentNodes.add(g.getVertex(index));
    	}
    	for (Vertex target : adjacentNodes) {
            // if shorter distance update distance map and set predecessor
            if (getShortestDistance(target, distance) > getShortestDistance(node, distance) + getDistance(node, target, g)) {
                distance.put(target, getShortestDistance(node, distance) + getDistance(node, target, g));
                g.getVertex(target.getIndex()).setPredecessor(node.getIndex());
                notS.add(target);
            }
        }
    }

    /**
    * Method public static int getShortestDistance(Vertex arg1, Map<Vertex, Integer> arg2)
    * returns the shortest distance from the distance map,
    * if its not set returns max_value of int
    */
    public static int getShortestDistance(Vertex destination, Map<Vertex, Integer> distance) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /**
    * Method public static int DifferByOne(String arg1, String arg2)
    * Checks if the two strings differ by only 1 letter, if so returns 
    * the difference between the two letters
    */
	public static int DifferByOne(String str, String str2){
		int letterDifference = 0;
		int res = 0;
		char[] firstStr = str.toCharArray();
		char[] secondStr = str2.toCharArray();
		for(int i = 0; i < str.length(); i++){
			if(firstStr[i] != secondStr[i]){
				letterDifference ++;
				res = Math.abs(firstStr[i]-secondStr[i]);	
			} 
		}

		if(letterDifference == 1) return res;

		return 0;

	}

}