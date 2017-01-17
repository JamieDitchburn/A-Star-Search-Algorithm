import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spot {

	// Properties of cell
	private int i, j;
	private int g = 0;
	private double f = 0;
	private double h = 0;
	private Spot previous;
	private List<Spot> neighbours = new ArrayList<>();
	private boolean wall = false;
	
	public Spot(int i, int j) {
		this.i = i;
		this.j = j;
		Random rand = new Random();
		if(rand.nextFloat() < 0.3) wall = true;
	}
	
	public boolean isWall() {
		return wall;
	}
	
	public void setWall(boolean b) {
		wall = b;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}
	
	public void setNeighbours(List neighbours) {
		this.neighbours = neighbours;
	}
	
	public List<Spot> getNeighbours() {
		return neighbours;
	}

	public Spot getPrevious() {
		return previous;
	}

	public void setPrevious(Spot previous) {
		this.previous = previous;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setF(double f) {
		this.f = f;
	}

	public void setH(double h) {
		this.h = h;
	}

	public int getG() {
		return g;
	}

	public double getF() {
		return f;
	}

	public double getH() {
		return h;
	}
	
}
