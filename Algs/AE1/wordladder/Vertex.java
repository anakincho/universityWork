import java.util.LinkedList;


/**
 class to represent a  vertex in a graph
*/
public class Vertex {
   
    private LinkedList<AdjListNode> adjList ; // the adjacency list 
    private int index; // the index of this vertex in the graph
    
    private String word; //contains the word from the dictionary 
    
    //possibly other fields, for example representing data
    // stored at the node, whether the vertex has been visited
    // in a traversal, its predecessor in such a traversal, etc.
    
	boolean visited; // whether vertex has been visited in a traversal

     
    /**
	 creates a new instance of Vertex
	 */
    public Vertex(int n, String s) {
    	adjList = new LinkedList<AdjListNode>();
    	index = n;
    	word = s;
    	visited = false;
    }
    
    /**
	 copy constructor
	*/
    public Vertex(Vertex v){
    	adjList = v.getAdjList();
    	index = v.getIndex();
    	visited = v.getVisited();
    }
     
    
    public LinkedList<AdjListNode> getAdjList(){
        return adjList;
    }
    
    public int getIndex(){
    	return index;
    }
    
    public void setIndex(int n){
    	index = n;
    }
    
    public boolean getVisited(){
    	return visited;
    }
    
    public void setVisited(boolean b){
    	visited = b;
    }
    
    public void addToAdjList(int n){
        adjList.addLast(new AdjListNode(n));
    }
    
    //adding getters and setters for the words which the vertices will contain
    public String getWord(){
    	return word; 
    }
    
    public void setWord(String s){
    	word = s; 
    }
}
