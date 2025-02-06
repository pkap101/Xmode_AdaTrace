
public class Point {
	
	private double xCoord; // longitude
	private double yCoord; // latitude
	private String activity_lbl; //activity type label
	
	public Point(double xc, double yc, String label) {
		this.xCoord = xc;
		this.yCoord = yc;
		this.activity_lbl = label;
	}
	
	public double getX() {
		return this.xCoord;
	}
	
	public double getY() {
		return this.yCoord;
	}

	public String getLabel() {
		return this.activity_lbl;
	}
	
	public String toString() {
		return "(" + this.xCoord + "," + this.yCoord + "," + this.activity_lbl + ")";
	}
	
	public boolean isEqualTo(Point p2) {
		if (this.getX() != p2.getX())
			return false;
		if (this.getY() != p2.getY())
			return false;
		if (this.getLabel() != p2.getLabel())
			return false;
		return true;
	}
	
	public void setX (double xc) {
		this.xCoord = xc;
	}
	
	public void setY (double yc) {
		this.yCoord = yc;
	}

	public void setLabel (String label) {
		this.activity_lbl = label;
	}
	
	public double euclideanDistTo(Point p2) {
		double sum = (this.getY()-p2.getY())*(this.getY()-p2.getY()) +
				(this.getX()-p2.getX())*(this.getX()-p2.getX());
		return Math.sqrt(sum);
	}



}
