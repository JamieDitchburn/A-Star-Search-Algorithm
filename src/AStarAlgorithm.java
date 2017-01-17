import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

public class AStarAlgorithm implements ActionListener {

	public static AStarAlgorithm aStarAlgorithm;
	
	// Window render
	public final static int WIDTH = 800, HEIGHT = 800;
	public Renderer renderer;
	
	// Algorithm
	private static int cols = 50; 
	private static int rows = 50;
	private static int[][] grid = new int[cols][rows];
	private static Spot[][] spots = new Spot[cols][rows];
	private static List<Spot> openSet = new LinkedList<>();
	private static List<Spot> closedSet = new LinkedList<>();
	private static List<Spot> neighbours;
	private static List<Spot> path;
	static Timer timer;
	
	// Start and end points
	private static Spot start = new Spot(0, 0);
	private static Spot end = new Spot(cols - 1, rows - 1);
	
	
	public AStarAlgorithm() {
		// Window rendering
		JFrame jframe = new JFrame();
		timer = new Timer(20, this);				// Timer to refresh window eery 20ms.
		
		renderer = new Renderer();
		jframe.add(renderer);
		jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH+6, HEIGHT+29);			// Account for window cutting edges
		jframe.setResizable(false);
		jframe.setTitle("A* algorithm pathfinding test");
		jframe.setVisible(true);
		
		// Algorithm
		for (int x = 0; x < cols; x++ ) {
			for (int y = 0; y < rows; y++ ) {
				spots[x][y] = new Spot(x, y);
			}
		}
		
		spots[start.getI()][start.getJ()] = start;
		spots[end.getI()][end.getJ()] = end;
		start.setWall(false);
		end.setWall(false);
		openSet.add(start);
		addNeighbours();
		timer.start();
	}
	
	private static double heuristic(Spot a, Spot b) {
		int x = b.getI() - a.getI();
		int y = b.getJ() - a.getJ();
		return Math.sqrt(x*x + y*y);
	}
	
	// Add neighbours to each spot in spots
	private void addNeighbours() {
		for (Spot[] spotArr: spots) {
			for (Spot spot: spotArr) {
				neighbours = new ArrayList<>();
				int i = spot.getI();
				int j = spot.getJ();
				if (i < cols - 1) neighbours.add(spots[i+1][j]);
				if (i > 0) neighbours.add(spots[i-1][j]);
				if (j < rows - 1) neighbours.add(spots[i][j+1]);
				if (j > 0) neighbours.add(spots[i][j-1]);
				spot.setNeighbours(neighbours);
			}
		}
	}
	
	public static void repaint(Graphics g) {
		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// Still searching?
		if(openSet.size() > 0) {
			
			// Find lowest fScore in openSet
			Spot winner = openSet.get(0);
			for (Spot spot: openSet) {
				if(spot.getF() < winner.getF()) {
					winner = spot;
				}
			}
			
			// If completed path
			if (winner == end) {
				System.out.println("Complete!");
				timer.stop();
			}
			
			// Find path
			path = new LinkedList<>();
			Spot temp = winner;
			path.add(temp);
			while(temp.getPrevious() != null) {
				path.add(temp.getPrevious());
				temp = temp.getPrevious();
			}
			
			// Update openSet and closedSet
			openSet.remove(winner);
			closedSet.add(winner);
			
			// Check neighbours
			neighbours = winner.getNeighbours();
			for (Spot neighbour: neighbours) {
				
				if (!closedSet.contains(neighbour) && !neighbour.isWall()) {
					int tempG = winner.getG() + 1;
					
					if (openSet.contains(neighbour)) {
						if (tempG < neighbour.getG()) {
							neighbour.setG(tempG);
						}
					} else {
						neighbour.setG(tempG);
						openSet.add(neighbour);
					}
					
					neighbour.setH(heuristic(neighbour, end));
					neighbour.setF(neighbour.getG() + neighbour.getH());
					neighbour.setPrevious(winner);
				}
				
			}
		} else {
			// No solution
			System.out.println("Error: No possible solution");
		}
		
		
		// Draw and colour grid squares
		for (Spot[] spotArr: spots) {
			for (Spot spot: spotArr) {
				int x = spot.getI();
				int y = spot.getJ();
				if (spot.isWall()) g.setColor(Color.BLACK);
				else g.setColor(Color.WHITE);
				g.fillRect((x * (WIDTH / cols)) + 1, (y * (HEIGHT / rows)) + 1, (WIDTH / cols) - 2, (HEIGHT / rows) - 2);
			}
		}
		
		for (Spot spot: closedSet) {
			g.setColor(Color.RED);
			int x = spot.getI();
			int y = spot.getJ();
			g.fillRect((x * (WIDTH / cols)) + 1, (y * (HEIGHT / rows)) + 1, (WIDTH / cols) - 2, (HEIGHT / rows) - 2);
		}
		
		for (Spot spot: openSet) {
			g.setColor(Color.GREEN);
			int x = spot.getI();
			int y = spot.getJ();
			g.fillRect((x * (WIDTH / cols)) + 1, (y * (HEIGHT / rows)) + 1, (WIDTH / cols) - 2, (HEIGHT / rows) - 2);
		}
		
		for (Spot spot: path) {
			g.setColor(Color.BLUE);
			int x = spot.getI();
			int y = spot.getJ();
			g.fillRect((x * (WIDTH / cols)) + 1, (y * (HEIGHT / rows)) + 1, (WIDTH / cols) - 2, (HEIGHT / rows) - 2);
		}

	}
	
	public static void main(String[] args) {
		aStarAlgorithm = new AStarAlgorithm();
	}

	public void actionPerformed(ActionEvent arg0) {
		renderer.repaint();
	}

}
