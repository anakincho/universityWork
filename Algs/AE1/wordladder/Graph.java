import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 class to represent an undirected graph using adjacency lists
 */
public class Graph {

	private Vertex[] vertices; // the (array of) vertices
	private int numVertices = 0; // number of vertices


	/**
	 creates a new instance of Graph with n vertices
	*/
	public Graph(int n) {
		numVertices = n;
		vertices = new Vertex[n];
		for (int i = 0; i < n; i++)
			vertices[i] = new Vertex(i,"");
	}

	public int size() {
		return numVertices;
	}

	public Vertex getVertex(int i) {
		return vertices[i];
	}

	public void setVertex(int i) {
		vertices[i] = new Vertex(i,"");
	}
	

	/**
	* Method public void bfs(Vertex arg1, Vertex arg2)
	* Implementation on the bfs with start and end vertices
	* if ladder exists, it is printed, otherwise a No ladder
	* message is given
	*/
	public void bfs(Vertex start, Vertex end){
		
		List<Vertex> ladder = new ArrayList<>();
		List<Vertex> ladderTemp;
		Queue<List<Vertex>> laddersContainer = new LinkedList<>();
		
		Vertex v = null;
		boolean isLadder = false;

		ladder.add(start);
		laddersContainer.add(ladder);
		start.setVisited(true);
		
		while (!laddersContainer.isEmpty()){
			ladder = laddersContainer.remove();
			if (ladder.get(ladder.size()-1).getWord().equals(end.getWord())){
				String resultPrint = "";
				for(Vertex w : ladder){
					if(w != null) resultPrint += w.getWord()+"->";
				}
				if (resultPrint.endsWith("->")) {
		  			resultPrint = resultPrint.substring(0, resultPrint.length() - 2);
				}

				System.out.println("Steps in ladder: "+(ladder.size()-1));
				System.out.println("Ladder: "+resultPrint);
				isLadder = true;
				break;			
			}
			for (AdjListNode adjNode : ladder.get(ladder.size()-1).getAdjList()){	
				v = getVertex(adjNode.getVertexNumber());
				if (!v.getVisited()){
					ladderTemp = new ArrayList<Vertex>();
					ladderTemp.addAll(ladder);
					ladderTemp.add(v);
					v.setVisited(true);
					laddersContainer.add(ladderTemp);
				}
			}
		}
		if(!isLadder) System.out.println("No ladder");
	}
}
