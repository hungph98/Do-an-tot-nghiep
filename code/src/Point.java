import java.util.LinkedList;

// Điểm

public class Point {
	public double x;
	public double y;
	public double distance;
	public double[] angles;
	public Point[] points;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public boolean isEquals(Point p) {
		return p.x == x && p.y == y;
	}
	
	public double distanceFrom(Point p) {
		return Math.hypot(p.x - x, p.y - y);
	}
	
	public void printPoint() {
		System.out.println("(" + x + ", " + y + ")");
	}
	public boolean greaterThan180(Point b, Point c, Polygons poly) {
		Point p1 = new Point((x + b.x)/2, (y + b.y)/2);
		Point p2 = new Point((x + c.x)/2, (y + c.y)/2);
		
		return CollisionDetection.polyLine2(poly, p1, p2) == 2;
	}
	
    public boolean isDuplicate(LinkedList<Point> lst) {
    	for(int i = 0; i < lst.size(); i++) {
    		if(lst.get(i).x == x && lst.get(i).y == y) 
    			return true;
    	}
    	return false;
    }
    
    public int indexInSet(LinkedList<Point> list) {
    	int id = 0;
    	for(int i = 0; i < list.size(); i++) {
    		if(list.get(i).x == x && list.get(i).y == y) 
    			return id;
    		else id++;
    	}
    	return -1;
    }
    
	public boolean isOnSegment(LineNode line) {
		return distanceFrom(line.pt1) + distanceFrom(line.pt2) == line.getLength() && distanceFrom(line.pt1) > 0 && distanceFrom(line.pt2) > 0;
	}
	
}
