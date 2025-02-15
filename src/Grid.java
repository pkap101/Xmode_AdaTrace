import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Grid {
	
	Cell[][][] topLevelCells; // NxN matrix of cells

	private static final String[] LABELS = {"residence", "unspecified", "retail", "other", "school", "college", "worship"};
	private static final int num_labels = 7;
	private double minX, maxX, minY, maxY;
    private double xIncrement, yIncrement;

	private HashMap<Cell, Integer> posInListForm;  // for efficiency
	
	public Grid (int cellCount, double minX, double maxX, double minY, double maxY) {
		// initialize 
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;

		topLevelCells = new Cell[cellCount][cellCount][num_labels];
		
		// fill with cells
		double xIncrement = (maxX-minX)/((double) cellCount);
		double yIncrement = (maxY-minY)/((double) cellCount);
		for (int i = 0; i < cellCount; i++) {
			for (int j = 0; j < cellCount; j++) {
				for (int k = 0; k<num_labels; k++) {
					topLevelCells[i][j][k] = new Cell(minX+xIncrement*i, minY+yIncrement*j,
							xIncrement, yIncrement, ""+i+","+j, LABELS[k]);
				}
			}
		}
		
		// for efficiency
		posInListForm = new HashMap<Cell, Integer>();
		for (int i = 0; i < this.getCells().size(); i++) {
			posInListForm.put(this.getCells().get(i), i);
		}
	}
	
	public int getN() {
		return this.topLevelCells.length;
	}

	public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }
    public double getXIncrement() { return (maxX - minX) / getN(); }
    public double getYIncrement() { return (maxY - minY) / getN(); }
    public int getNumLabels() { return num_labels; }
	
	public double getXofCell(Cell c1) {
		for (int i = 0; i < topLevelCells.length; i++) {
			for (int j = 0; j < topLevelCells.length; j++) {
				for (int k = 0; k<num_labels; k++) {
					if (topLevelCells[i][j][k] == c1) {
						return i + 0.0;
					}
				}
			}
		}
		return -1;
	}
	
	public double getYofCell(Cell c1) {
		for (int i = 0; i < topLevelCells.length; i++) {
			for (int j = 0; j < topLevelCells.length; j++) {
				for (int k = 0; k<num_labels; k++) {
					if (topLevelCells[i][j][k] == c1) {
						return j + 0.0;
					}
				}
			}
		}
		return -1;
	}

	public double getKofCell(Cell c1) {
		for (int i = 0; i < topLevelCells.length; i++) {
			for (int j = 0; j < topLevelCells.length; j++) {
				for (int k = 0; k<num_labels; k++) {
					if (topLevelCells[i][j][k] == c1) {
						return k + 0.0;
					}
				}
			}
		}
		return -1;
	}
	
	public List<Cell> getCells() {
		List<Cell> tbR = new ArrayList<Cell>();
		for (int i = 0; i < this.getN(); i++) {
			for (int j = 0; j < this.getN(); j++) {
				for (int k = 0; k<num_labels; k++) {
					tbR.add(topLevelCells[i][j][k]);
				}
			}
		}
		return tbR;
	}
	
	public int getPosInListForm(Cell c1) {
		return this.posInListForm.get(c1);
	}
	
	public Cell[][][] getCellMatrix() {
		return this.topLevelCells;
	}
	
	public boolean areAdjacent (Cell c1, Cell c2) {
		int c1x = -1, c1y = -1, c2x = -1, c2y = -1, c1z = -1, c2z = -1;
		boolean c1found = false, c2found = false;
		// locate cells
		for (int i = 0; i < this.getN(); i++) {
			for (int j = 0; j < this.getN(); j++) {
				for (int k=0; k<num_labels; k++) {
					if (c1 == topLevelCells[i][j][k]) {
						c1x = i;
						c1y = j;
						c1z = k;
						c1found = true;
					}
					if (c2 == topLevelCells[i][j][k]) {
						c2x = i;
						c2y = j;
						c2z = k;
						c2found = true;
					}
				}
			}
		}
		if (c1found == false || c2found == false) {
			System.out.println("cells not found, cannot compute adjacency");
			System.out.println("c1: " + c1.getName() + ", c2: " + c2.getName());
			return false;
		}
		// check adjacency conditions: c1 to c2
		if (c2x == c1x+1 && c2y == c1y) 
			return true; // c2 is east of c1
		if (c2x == c1x+1 && c2y == c1y+1)
			return true; // c2 is northeast of c1
		if (c2x == c1x && c2y == c1y+1)
			return true; // c2 is north of c1
		if (c2x == c1x-1 && c2y == c1y+1)
			return true; // c2 is northwest of c1
		if (c2x == c1x-1 && c2y == c1y) 
			return true; // c2 is west of c1
		if (c2x == c1x-1 && c2y == c1y-1)
			return true; // c2 is southwest of c1
		if (c2x == c1x && c2y == c1y-1)
			return true; // c2 is south of c1
		if (c2x == c1x+1 && c2y == c1y-1)
			return true; // c2 is southeast of c1
		// check adjacency conditions: c2 to c1 - symmetric to above
		if (c1x == c2x+1 && c1y == c2y) 
			return true; 
		if (c1x == c2x+1 && c1y == c2y+1)
			return true; 
		if (c1x == c2x && c1y == c2y+1)
			return true; 
		if (c1x == c2x-1 && c1y == c2y+1)
			return true; 
		if (c1x == c2x-1 && c1y == c2y) 
			return true; 
		if (c1x == c2x-1 && c1y == c2y-1)
			return true; 
		if (c1x == c2x && c1y == c2y-1)
			return true; 
		if (c1x == c2x+1 && c1y == c2y-1)
			return true; 
		// no adjacency found?
		return false;		
	}
	
	public List<Cell> getAdjacentCells (Cell c1) {
		List<Cell> cells = this.getCells();
		List<Cell> tbr = new ArrayList<Cell>();
		for (int i = 0; i < cells.size(); i++) {
			if (this.areAdjacent(cells.get(i), c1))
				tbr.add(cells.get(i));
		}
		return tbr;
	}
	
	// return the list of cells on the shortest path between start cell and end cell
	public List<Cell> giveInterpolatedRoute (Cell start, Cell end) {
		// locate cells
		int startx = -1, starty = -1, endx = -1, endy = -1, startz=-1, endz=-1;
		boolean startfound = false, endfound = false;
		for (int i = 0; i < this.getN(); i++) {
			for (int j = 0; j < this.getN(); j++) {
				for (int k=0; k<num_labels; k++) {
					if (start == topLevelCells[i][j][k]) {
						startx = i;
						starty = j;
						startz = k;
						startfound = true;
					}
					if (end == topLevelCells[i][j][k]) {
						endx = i;
						endy = j;
						endz = k;
						endfound = true;
					}
				}
			}
		}
		if (startfound == false || endfound == false) {
			System.out.println("cells not found, cannot interpolate");
			return null;
		}
		// compute shortest path between them 
		List<Cell> tbr = new ArrayList<Cell>();
		int currx = startx;
		int curry = starty;
		int currz = startz;
		while (true) {
			tbr.add(this.topLevelCells[currx][curry][currz]); // adds startcell, never the endcell
			if (endx > currx) {
				currx = currx+1;
			} else if (endx < currx) {
				currx = currx-1;
			}
			if (endy > curry) {
				curry = curry + 1;
			} else if (endy < curry) {
				curry = curry - 1;
			}
			//gradually transition label type
			if (new Random().nextDouble() < 0.7) {  // 70% chance to change label
				if (currz < endz) {
					currz++;
				} else if (currz > endz) {
					currz--;
				}				
			}
			
			if (currx == endx && curry == endy) {
				currz = endz; 
				break;
			}
		}
		return tbr;
	}

	public int findShortestLengthBetween (Cell start, Cell end) {
		List<Cell> shortestPath = this.giveInterpolatedRoute(start, end);
		shortestPath.add(end); // because giveInterpolatedRoute doesnt add end cell
		return shortestPath.size();
	}

}
