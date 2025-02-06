import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;


public class Cell {
	
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private String name = "";
	private List<Cell> level2cells; // for adaptive grid
	private List<Double> level2densities; // for adaptive grid

	private String label;
	private Map<String, Integer> labelCounts;
    private static final String[] LABELS = {"residence", "unspecified", "retail", "other", "school", "college", "worship"};
	
	public Cell (double minx, double miny, double xIncrement, double yIncrement, String nm, String label_type) {
		this.minX = minx;
		this.minY = miny;
		this.maxX = minx + xIncrement;
		this.maxY = miny + yIncrement;
		this.name = nm;
		this.label  = label_type;
		this.level2cells = new ArrayList<Cell>();
		this.level2densities = new ArrayList<Double>();

		this.labelCounts = new HashMap<>();
        for (String label : LABELS) {
            labelCounts.put(label, 0);
	}
}
	
	//adds 1 to count for specified label
	public void addLabel(String label) {
        labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
    }
	//samples label weighted by its likelihood
	public String sampleLabel() {
        int total = labelCounts.values().stream().mapToInt(i -> i).sum();
        if (total == 0) return "unspecified"; // Default if no labels have been added
        
        Random random = new Random();
        int r = random.nextInt(total);
        int countSoFar = 0;
        for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
            countSoFar += entry.getValue();
            if (r < countSoFar) {
                return entry.getKey();
            }
        }
        return "unspecified"; // This should never happen, but just in case
    }
	

	public boolean inCell(double xCoord, double yCoord) {
		if (xCoord >= this.minX && xCoord <= this.maxX && yCoord >= this.minY && yCoord <= this.maxY)
			return true;
		else
			return false;
	}
	
	public boolean inCell (Point p) {
		double xcoord = p.getX();
		double ycoord = p.getY();
		return inCell(xcoord, ycoord);
	}

	public boolean inCellLabeled(double xCoord, double yCoord, String clabel) {
		if (xCoord >= this.minX && xCoord <= this.maxX && yCoord >= this.minY && yCoord <= this.maxY && clabel==this.label)
			return true;
		else
			return false;
	}
	
	public boolean inCellLabeled(Point p) {
		double xcoord = p.getX();
		double ycoord = p.getY();
		String label = p.getLabel();
		return inCellLabeled(xcoord, ycoord, label);
	}
	
	public String toString() {
		return this.name;
	}
	
	public String getName() {
		return this.name;
	}
	public String getLabel() { 
        return label; 
    }
	public int getNumCells() {
		return this.level2cells.size();
	}
	/*
	// samples and returns a random point in this cell 
	public Point sampleRandomPoint() {
		double xcoord = this.minX + (new Random().nextDouble())*(this.maxX-this.minX);
		double ycoord = this.minY + (new Random().nextDouble())*(this.maxY-this.minY);
		// rounding to 2 digits after decimal place for producing more readable output
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		xcoord = Double.parseDouble(df.format(xcoord));
		ycoord = Double.parseDouble(df.format(ycoord));
		return new Point(xcoord, ycoord);
	} 
	*/
	
	
	// samples and returns a random point in this cell 
	public Point sampleRandomPoint() {
		// getting label as well
		// String sampledLabel = this.sampleLabel();
		String sampledLabel = this.label;

		if (level2cells.size() == 0 || level2cells.size() == 1) {
			double xcoord = this.minX + (new Random().nextDouble())*(this.maxX-this.minX);
			double ycoord = this.minY + (new Random().nextDouble())*(this.maxY-this.minY);
			// rounding to 2 digits after decimal place for producing more readable output
			//switched to 3 digits
			DecimalFormat df = new DecimalFormat("#.###");
			df.setRoundingMode(RoundingMode.HALF_UP);
			xcoord = Double.parseDouble(df.format(xcoord));
			ycoord = Double.parseDouble(df.format(ycoord));
			return new Point(xcoord, ycoord, sampledLabel);
		} else {
			Random r = new Random();
			double random = r.nextDouble();
			double seenSoFar = 0.0;
			for (int i = 0; i < this.level2densities.size(); i++) {
				seenSoFar = seenSoFar + this.level2densities.get(i);
				if (seenSoFar >= random) {
					// found it !
					Cell myAdaptiveCell = level2cells.get(i);
					double xcoord = myAdaptiveCell.minX + (new Random().nextDouble())*(this.maxX-this.minX);
					double ycoord = myAdaptiveCell.minY + (new Random().nextDouble())*(this.maxY-this.minY);
					DecimalFormat df = new DecimalFormat("#.##");
					df.setRoundingMode(RoundingMode.HALF_UP);
					xcoord = Double.parseDouble(df.format(xcoord));
					ycoord = Double.parseDouble(df.format(ycoord));
					return new Point(xcoord, ycoord, sampledLabel);
				}
			}
			// !?!?
			double xcoord = this.minX + (new Random().nextDouble())*(this.maxX-this.minX);
			double ycoord = this.minY + (new Random().nextDouble())*(this.maxY-this.minY);
			// rounding to 2 digits after decimal place for producing more readable output
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
			xcoord = Double.parseDouble(df.format(xcoord));
			ycoord = Double.parseDouble(df.format(ycoord));
			
			return new Point(xcoord, ycoord, sampledLabel);
		}
	}
	
	
	public String getBoundaries() {
		String n = "minX: " + this.minX + ", maxX: " + this.maxX + ", minY: " + this.minY +
				", maxY: " + this.maxY;
		return n;
	}
	
	public void divideFurther (double noisydensity, double EpsLeft, List<Trajectory> db) {
				
		//int lvl2cell = (int) Math.ceil(Math.sqrt(noisydensity*EpsLeft/200.0));
		//int lvl2cell = (int) Math.ceil( (5*noisydensity/(db.size()*EpsLeft)) );	
		int minSubdivisions = 1;  // Minimum 2x2 subdivision
		int lvl2cell = Math.max(minSubdivisions, (int)Math.ceil(Math.sqrt(noisydensity*EpsLeft/60.0)));
		if (this.label == "residence") {
			lvl2cell = Math.max(minSubdivisions, (int)Math.ceil(Math.sqrt(noisydensity*EpsLeft/75.0)));
		} else {
			lvl2cell = Math.max(minSubdivisions, (int)Math.ceil(Math.sqrt(noisydensity*EpsLeft/7.0)));
		}


		System.out.println("Subdividing cell " + this.name + 
                      " with density " + noisydensity + 
                      " into " + lvl2cell + "x" + lvl2cell + " grid");

		if (lvl2cell < 0)
			lvl2cell = 1;
		double xIncrement = (this.maxX-this.minX)/((double)lvl2cell);
		double yIncrement = (this.maxY-this.minY)/((double)lvl2cell);
		/*
		List<Double> densitiess = new ArrayList<Double>();
		for (int i = 0; i < lvl2cell; i++) {
			for (int j = 0; j < lvl2cell; j++) {
				for (int k=0; k<7; k++) {
					Cell lvl2cellSelf = new Cell(this.minX+xIncrement*i, minY+yIncrement*j, xIncrement,
							yIncrement, ""+i+","+j, this.label);
					double lvl2cellDensity = 0.0;
					for (Trajectory t : db) {
						for (int q = 0; q < t.getSize(); q++) {
							if (lvl2cellSelf.inCell(t.getPoint(q))) {
								lvl2cellDensity = lvl2cellDensity + 1.0;
								break;
							}
						}
					}
					level2cells.add(lvl2cellSelf);
					densitiess.add(lvl2cellDensity);
				}
			}
		}
		// normalize densities
		double totaldensity = 0.0;
		for (Double d : densitiess)
			totaldensity = totaldensity + d;
		for (int i = 0; i < densitiess.size(); i++) {
			level2densities.add(densitiess.get(i)/totaldensity);
		}
		*/

		//optimized version
		// Pre-create all subcells
		Cell[][] subcells = new Cell[lvl2cell][lvl2cell];
		double[][] densities = new double[lvl2cell][lvl2cell];
		
		for (int i = 0; i < lvl2cell; i++) {
			for (int j = 0; j < lvl2cell; j++) {
				subcells[i][j] = new Cell(
					this.minX + xIncrement*i,
					this.minY + yIncrement*j,
					xIncrement,
					yIncrement,
					""+i+","+j,
					this.label
				);
			}
		}
		
		// For each trajectory, only process it if it could intersect this cell
		for (Trajectory t : db) {
			// Quick bounds check
			if (t.getMaxXCoord() < this.minX || t.getMinXCoord() > this.maxX ||
				t.getMaxYCoord() < this.minY || t.getMinYCoord() > this.maxY) {
				continue;
			}
			
			// For each point in trajectory
			for (int q = 0; q < t.getSize(); q++) {
				Point p = t.getPoint(q);
				// Skip if point isn't in this cell
				if (!this.inCell(p)) continue;
				
				// Calculate which subcell this point belongs to
				int i = (int)((p.getX() - this.minX) / xIncrement);
				int j = (int)((p.getY() - this.minY) / yIncrement);
				
				// Bounds checking
				if (i >= 0 && i < lvl2cell && j >= 0 && j < lvl2cell) {
					densities[i][j] += 1.0;
				}
			}
		}
		
		// Convert to lists for compatibility with existing code
		List<Double> densitiess = new ArrayList<>();
		double totaldensity = 0.0;
		
		// Add cells and densities to lists
		for (int i = 0; i < lvl2cell; i++) {
			for (int j = 0; j < lvl2cell; j++) {
				for (int k = 0; k < 7; k++) {  // 7 copies for activity types
					level2cells.add(subcells[i][j]);
					double density = densities[i][j];
					densitiess.add(density);
					totaldensity += density;
				}
			}
		}
		
		// Normalize densities
		for (int i = 0; i < densitiess.size(); i++) {
			level2densities.add(densitiess.get(i)/totaldensity);
		}
	}


}
