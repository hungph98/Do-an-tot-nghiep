
// Phát hiện va chạm 

public class CollisionDetection {
	static int INF = 10000;
    static boolean onSegment(Point p, Point q, Point r)  
    { 
        if (q.getX() <= Math.max(p.getX(), r.getX()) && 
            q.getX() >= Math.min(p.getX(), r.getX()) && 
            q.getY() <= Math.max(p.getY(), r.getY()) && 
            q.getY() >= Math.min(p.getY(), r.getY())) 
        { 
            return true; 
        } 
        return false; 
    } 
  
    static int orientation(Point p, Point q, Point r)  
    { 
    	double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) 
                - (q.getX() - p.getX()) * (r.getY() - q.getY()); 
    	
        if (val == 0)  
        { 
            return 0; 
        } 
        return (val > 0) ? 1 : 2; 
    } 
    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)  
    { 
        int o1 = orientation(p1, q1, p2); 
        int o2 = orientation(p1, q1, q2); 
        int o3 = orientation(p2, q2, p1); 
        int o4 = orientation(p2, q2, q1); 
        if (o1 != o2 && o3 != o4) { return true; } 
        if (o1 == 0 && onSegment(p1, p2, q1)) { return true; } 
        if (o2 == 0 && onSegment(p1, q2, q1)) { return true; } 
        if (o3 == 0 && onSegment(p2, p1, q2)) { return true; } 
        if (o4 == 0 && onSegment(p2, q1, q2)) { return true; } 
        return false;  
    } 
    static boolean polyPoint(Polygons poly, Point p) 
    { 
        if (poly.cornum < 3) { return false; } 
        Point extreme = new Point(INF, p.getY()); 
        int count = 0, i = 0; 
        do 
        { 
            int next = (i + 1) % poly.cornum; 
            if (doIntersect(poly.point[i], poly.point[next], p, extreme))  
            { 
                if (orientation(poly.point[i], p, poly.point[next]) == 0) 
                { 
                    return onSegment(poly.point[i], p, poly.point[next]); 
                } 
  
                count++; 
            } 
            i = next; 
        } while (i != 0); 
        return (count % 2 == 1); 
    } 
    
    static boolean polyLine(Polygons poly, Point p, Point q) {
    	int cornum = poly.cornum;
    	int i = 0;
    	if ((polyPoint(poly, p) == true) || (polyPoint(poly, q) == true)) {
    		return true;
    	}
    	i = 0;
    	do {
    		int next = (i + 1) % cornum; 
    		if (doIntersect(poly.point[i], poly.point[next], p, q)) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	return false;
    }
    
    static boolean polyPoly(Polygons poly1, Polygons poly2) {
    	int num1 = poly1.cornum;
    	int num2 = poly2.cornum;
    	int i = 0;
    	i = 0;
    	do {
    		int next = (i + 1) % num1; 
    		if (polyLine(poly2, poly1.point[i], poly1.point[next]) == true) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	i = 0;
    	do {
    		int next = (i + 1) % num2; 
    		if (polyLine(poly1, poly2.point[i], poly2.point[next]) == true) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	return false;
    }
    
    static boolean graphPoint (Graph g, Point p) {
    	for (int i = 0; i < g.num; i++) {
    		if (polyPoint(g.obstacles[i], p) == true) {
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean graphLine(Graph g, Point p, Point q) {
    	for (int i = 0; i < g.num; i++) {
    		if (polyLine(g.obstacles[i], p, q) == true) {
    			return true;
    		}
    	}
    	return false;
    }
    static double numGraphLine(Graph g, Point p, Point q) {
    	double S =0;
    	for(int i=0; i<g.num; i++) {
    		if(polyLine(g.obstacles[i], p,q)==true) {
    			S = S+1.0;
    		}
    }
    return S;
    }
    static int polyLine2(Polygons poly, Point p, Point q) {
    	int cornum = poly.cornum;
    	int i = 0, j = 0;
    	do {
    		int next = (i + 1) % cornum; 
    		if (doIntersect(poly.point[i], poly.point[next], p, q)) {
    			j++;
    		}
    		i = next;
    	} while (i != 0);
    	return j;
    }
    
    static int graphLine2(Graph g, Point p, Point q) {
    	int result = 0;
    	for (int i = 0; i < g.num; i++) {
    		result += polyLine2(g.obstacles[i], p, q); 
    		}
    	return result;
    }
    
}