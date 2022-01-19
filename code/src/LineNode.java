import java.util.LinkedList;

// Node

public class LineNode {
	public Point pt1;
	public Point pt2;
	
	public LineNode(Point pt1, Point pt2) {
		this.pt1 = pt1;
		this.pt2 = pt2;
	}
	
	public void printLine() {
		System.out.print("(" + pt1.x + ", " + pt1.y + ") (" + pt2.x + ", " + pt2.y + ") ");
	}
	
	public double getLength() {
		return Math.sqrt(Math.pow(this.pt1.x - this.pt2.x, 2) + Math.pow(this.pt1.y - this.pt2.y, 2));
	}
	
	public boolean doIntersect(LineNode line) {
		double a = pt1.y - pt2.y;
		double b = pt2.x - pt1.x;
		double c = -a*pt1.x - b*pt1.y;
		
		double a1 = line.pt1.y - line.pt2.y;
		double b1 = line.pt2.x - line.pt1.x;
		double c1 = -a1*line.pt1.x - b1*line.pt1.y;
		if(a*line.pt1.x + b*line.pt1.y + c == 0 && a*line.pt2.x + b*line.pt2.y + c == 0) {
			if((pt1.distanceFrom(line.pt1) >= this.getLength() && pt1.distanceFrom(line.pt2) >= this.getLength()) || (pt2.distanceFrom(line.pt1) >= this.getLength() && pt2.distanceFrom(line.pt2) >= this.getLength()))
				return false;
			else
				return true;
		}
		
		if(((a*line.pt1.x + b*line.pt1.y + c)*(a*line.pt2.x + b*line.pt2.y + c) < 0) && ((a1*pt1.x + b1*pt1.y + c1)*(a1*pt2.x + b1*pt2.y + c1) < 0)) 
			return true;
		
		if(((a*line.pt1.x + b*line.pt1.y + c)*(a*line.pt2.x + b*line.pt2.y + c) > 0) || ((a1*pt1.x + b1*pt1.y + c1)*(a1*pt2.x + b1*pt2.y + c1) > 0))
			return false;	
		return false;		
	}
	
	public boolean doIntersectSet(LinkedList<LineNode> lines) {
		for(LineNode line: lines) 
			if(line.doIntersect(this)) 
				return true;		
		
		return false;
	}
	
	public boolean doInSet(LinkedList<LineNode> lines) {
		for(LineNode line: lines) 
			if((pt1.isEquals(line.pt1) && pt2.isEquals(line.pt2)) || (pt2.isEquals(line.pt1) && pt1.isEquals(line.pt2)))
				return true;
				
		return false;
	}
	
	public int indexInSet(LinkedList<LineNode> lines) {
		int i = 0;
		for(LineNode line: lines) {
			if((pt1.isEquals(line.pt1) && pt2.isEquals(line.pt2)) || (pt2.isEquals(line.pt1) && pt1.isEquals(line.pt2)))
				return i;
			else
				i++;
		}
				
		return -1;
	}
		
}
