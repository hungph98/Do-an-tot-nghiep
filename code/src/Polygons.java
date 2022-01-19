import java.util.*;

// Đa giác

public class Polygons {
	public int cornum;
	public Point[] point;
	
	public Polygons() {
        cornum = 0;
        point = new Point[3];
    }
	
	public Polygons(int n) {
		cornum = n;
		point = new Point[n];
		Random rd = new Random();
		double x, y;		
		for (int i = 0; i < cornum; i++) {
			x = rd.nextInt(100);
			y = rd.nextInt(100);
			Point p = new Point(x, y);
			point[i] = p;
		}
	}
	
	public double polygonArea() {
		double area = 0.0; 	      
		int n = cornum;
        int j = n - 1; 
        for (int i = 0; i < n; i++) 
        { 
            area += (point[j].getX() + point[i].getX()) * 
            		(point[j].getY() - point[i].getY()); 
            j = i;  
        } 
        return Math.abs(area / 2.0); 
	}
	public void resizeArea(int n) {
		for (int i = 0; i < cornum; i++) {
			point[i].x = point[i].x/2;
			point[i].y = point[i].y/2;
		}
	}
	
	public void movePolygon(double x, double y) {
		Random rd = new Random();
		double movex = x - point[0].x;
		double movey = y - point[0].y;
		for (int i = 0; i < cornum; i++) {
			point[i].x = point[i].x + movex;
			point[i].y = point[i].y + movey;
		}
		for (int i = 0; i < cornum; i++) {
			if(point[i].x < 0 || point[i].x > 100 || point[i].y < 0 || point[i].y > 100) {
				double x1 = rd.nextInt(100);
				double y1 = rd.nextInt(100);
				movePolygon(x1, y1);
			}
		}
	}
	
	private void resize() {
        Point[] temp = new Point[2*cornum+1];
        for (int i = 0; i <= cornum; i++) temp[i] = point[i];
        point = temp;
    }
	
	public void addPoint(Point p) {
        if (cornum >= point.length - 1) {
        	resize();
        }
        point[cornum++] = p;
        point[cornum] = point[0];
	}
	
	public Point[] delPoint() {
		point[cornum-1] = point[0];
		Point[] anotherArray = new Point[point.length - 1]; 
		for (int i = 0, k = 0; i < point.length-1; i++) { 
            anotherArray[k++] = point[i]; 
        } 
		cornum--;
        return anotherArray; 
	}
	
	public int size() { return cornum; }
	
	public static float orientation(Point p, Point q, Point r) 
    { 
		double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - 
                  (q.getX() - p.getX()) * (r.getY() - q.getY()); 
       
        if (val == 0) return 0;  
        return (val > 0)? 1: 2;  
    } 
	
	public Polygons convexHull(Point point[], int cornum) {
		if (cornum < 3) return null;
		Vector<Point> hull = new Vector<Point>(); 
		Polygons tempPoints = new Polygons();
	
        int l = 0; 
        for (int i = 1; i < cornum; i++) 
            if (point[i].getX() < point[l].getX()) 
                l = i; 
        int p = l, q; 
        do
        { 
            hull.add(point[p]);            
            q = (p + 1) % cornum;   
            for (int i = 0; i < cornum; i++) 
            { 
               if (orientation(point[p], point[i], point[q]) == 2) 
                   q = i; 
            } 
            p = q; 
        } while (p != l); 
        int j = 0;
        for (Point temp : hull) {
            tempPoints.addPoint(temp);
            j++;
        }
        tempPoints.cornum = j;
        return tempPoints;
	}	
}
