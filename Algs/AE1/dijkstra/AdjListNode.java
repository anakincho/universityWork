
/**
 class to represent an entry in the adjacency list of a vertex
 in a graph
*/
public class AdjListNode {

	private int vertexNumber;
	private int weight;
	
    /* creates a new instance */
	public AdjListNode(int n, int w){
		vertexNumber = n;
		weight = w;
	}
	
	public int getVertexNumber(){
		return vertexNumber;
	}
	
	public void setVertexNumber(int n){
		vertexNumber = n;
	}

	public void setWeight(int w){
		weight = w;
	}

	public int getWeight(){
		return weight;
	}
	
}
