import java.util.Queue;

public class DijkstraMain {
	
	public static void main(String[] args) {
		
		Vertex vertexA = new Vertex("A"); //SO1
		Vertex vertexB = new Vertex("B");//SO2
		Vertex vertexC = new Vertex("C"); //SO3
		Vertex vertexD = new Vertex("D"); //SO4
		Vertex vertexE = new Vertex("E"); //SO5
		Vertex vertexF = new Vertex("F"); //SO6
		
		vertexA.addNeighbour(new Edge(15,vertexA,vertexB)); //SO1 conection to SO2/SO2 to SO1d
		
		vertexB.addNeighbour(new Edge(15,vertexB,vertexC)); //SO2 connection to SO3/SO3 to SO2
		vertexB.addNeighbour(new Edge(11,vertexB,vertexE)); //SO2 connection to SO5/S05 to SO2
		
		vertexC.addNeighbour(new Edge(15,vertexC,vertexD)); //SO3 connection to SO4/SO4 to SO3
		
		vertexE.addNeighbour(new Edge(15,vertexE,vertexF)); //SO5 connection to SO6/SO6 to SO5
		
		
		
	
		DijkstraShortestPath shortestPath = new DijkstraShortestPath();
//		shortestPath.computeShortestPaths(vertexA);
//		
//		System.out.println("======================================");
//		System.out.println("Calculating minimum distance");
//		System.out.println("======================================");
//		
//		System.out.println("Minimum distance from A to B: "+vertexB.getDistance());
//		System.out.println("Minimum distance from A to C: "+vertexC.getDistance());
//		System.out.println("Minimum distance from A to D: "+vertexD.getDistance());
//		System.out.println("Minimum distance from A to E: "+vertexE.getDistance());
//		
//		System.out.println("=====================	=================");
//		System.out.println("Calculating Paths");
//		System.out.println("======================================");
//		
//		System.out.println("Shortest Path from A to B: "+shortestPath.getShortestPathTo(vertexB));
//		System.out.println("Shortest Path from A to C: "+shortestPath.getShortestPathTo(vertexC));
//		System.out.println("Shortest Path from A to D: "+shortestPath.getShortestPathTo(vertexD));
//		System.out.println("Shortest Path from A to E: "+shortestPath.getShortestPathTo(vertexE));		
		
		shortestPath.computeShortestPaths(vertexA);
		System.out.println("Shortest Path from A to F: "+shortestPath.getShortestPathTo(vertexF));
		
		shortestPath.computeShortestPaths(vertexF);
		
		System.out.println("Shortest Path from F to D: "+shortestPath.getShortestPathTo(vertexD));
		//Reorient the Graph, doing it really basichere but it would be helpful to know how to do this at any point without the following
		
		
		shortestPath.computeShortestPaths(vertexA);
		System.out.println("Shortest Path from D to A: "+shortestPath.getShortestPathTo(vertexD));  
		//Will have to flip order when heading back, mybe another branch of dikstra with inputs reveresed,
		//So if node 1 is larger than node 2 well be able to start at 2 and work our wa
		
	}
	
}