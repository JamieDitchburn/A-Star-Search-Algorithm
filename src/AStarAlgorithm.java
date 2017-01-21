import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.Timer;

public class AStarAlgorithm implements ActionListener, Runnable {

	public static AStarAlgorithm aStarAlgorithm;
	
	// Window render
	public final static int WIDTH = 800, HEIGHT = 800;
	public Renderer renderer;
	
	// Algorithm
	private static int cols = 200; 
	private static int rows = 200;
	private static Spot[][] spots = new Spot[cols][rows];
	private static Set<Spot> openSet = new HashSet<>(2 * rows + 2 * cols);
	private static Set<Spot> closedSet = new HashSet<>(cols * rows);
	private static Set<Spot> neighbours;
	private static Set<Spot> path = new HashSet<>(rows + 2 * cols);
	private static Timer timer;
	private static long startTime = System.currentTimeMillis();
	
	// Start and end points
	private static Spot start = new Spot(0, 0);
	private static Spot end = new Spot(cols - 1, rows - 1);
	
	
	public AStarAlgorithm() {
		// Window rendering
		JFrame jframe = new JFrame();
		timer = new Timer(20, this);				// Timer to refresh window eery 1ms.
		renderer = new Renderer();
		jframe.add(renderer);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		start.setOpen(true);
		addNeighbours();
		timer.start();
		
	}
	
	// Add neighbours to each spot in spots
	private void addNeighbours() {
		for (Spot[] spotArr: spots) {
			for (Spot spot: spotArr) {
				neighbours = new HashSet<>(4);
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
	
	private static synchronized double heuristic(Spot a, Spot b) {
		int x = b.getI() - a.getI();
		int y = b.getJ() - a.getJ();
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		return x + y;
	}
	
	// Draw method
	public void repaint(Graphics g) {
		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
			
		// Draw and colour grid squares
		for (Spot[] spotArr: spots) {
			for (Spot spot: spotArr) {
				int x = spot.getI();
				int y = spot.getJ();
				// Set colour of spot to be drawn
				g.setColor(Color.WHITE);
				if (spot.isWall()) g.setColor(Color.BLACK);
				else if (spot.isPath()) g.setColor(Color.BLUE);
				else if (spot.isOpen()) g.setColor(Color.GREEN);
				else if (spot.isClosed()) g.setColor(Color.RED);
				g.fillRect((x * (WIDTH / cols)) + 1, (y * (HEIGHT / rows)) + 1, (WIDTH / cols) - 1, (HEIGHT / rows) - 1);
			}
		}
		
	}
	
	public static void main(String[] args) {
		aStarAlgorithm = new AStarAlgorithm();
		
		// Multithreading
		Thread thread = new Thread(aStarAlgorithm);
		thread.start();
	}

	public void actionPerformed(ActionEvent arg0) {
		renderer.repaint();
	}

	
	public void run() {
		// Still searching?
		while(openSet.size() > 0) {
			
			// Find lowest fScore in openSet and set open			
			Iterator<Spot> iterator = openSet.iterator();
			Spot winner = new Spot(0,0);
			winner.setF(Integer.MAX_VALUE);
			while(iterator.hasNext()) {
				Spot spot = iterator.next();
				if (!spot.isOpen()) spot.setOpen(true);
				if(spot.getF() < winner.getF()) {
					winner = spot;
				}
			}

			// Clear old path
			for (Spot spot: path) {
				spot.setPath(false);
			}
			// Find new path
			path.clear();
			Spot temp = winner;
			path.add(temp);
			temp.setPath(true);
			while(temp.getPrevious() != null) {
				path.add(temp.getPrevious());
				temp.getPrevious().setPath(true);
				temp = temp.getPrevious();
			}

			// If completed path
			if (winner == end) {
				System.out.println("Complete!");
				System.out.println(System.currentTimeMillis() - startTime + " ms elapsed.");
				System.out.println(path.size());
				break;
				
			}

			// Update openSet and closedSet
			openSet.remove(winner);
			winner.setOpen(false);
			closedSet.add(winner);
			winner.setClosed(true);

			// Check neighbours
			neighbours = winner.getNeighbours();
			for (Spot neighbour: neighbours) {

				if (!closedSet.contains(neighbour) && !neighbour.isWall()) {
					int tempG = winner.getG() + 1;

					boolean newPath = false;		// Only update spot from closed set if g is lower.
					if (openSet.contains(neighbour)) {
						if (tempG < neighbour.getG()) {
							neighbour.setG(tempG);
							newPath = true;
						}
					} else {
						neighbour.setG(tempG);
						newPath = true;
						openSet.add(neighbour);
					}

					if (newPath) {
						neighbour.setH(heuristic(neighbour, end));
						neighbour.setF(1 * neighbour.getG() + neighbour.getH());			// A weight can be added to this calculation that has a very large effect on the time taken at the expense of reliability that the path is the shortest.
						neighbour.setPrevious(winner);
					}
				}

			}
		}
	}

}
