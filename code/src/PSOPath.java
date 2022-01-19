public class PSOPath {
	public static int n;
	public double[] angles;
	public Point[] points;
	
	public PSOPath(int no) {
		n = no;
		angles = new double[n];
		points = new Point[n];
	}
	// Hàm chuyển đổi điểm
	public static Point convertAtoP(double angle, double R, Point start, Point end) {
		Point p;
		double cosp, sinp, x, y;
		double temp1, temp2, AB;
		temp1 = end.x - start.x;
		temp2 = end.y - start.y;
		AB = Math.sqrt(Math.pow(temp1, 2) + Math.pow(temp2, 2));
		cosp = (temp1/AB)*Math.cos(Math.toRadians(angle)) - (temp2/AB)*Math.sin(Math.toRadians(angle));
		sinp = (temp1/AB)*Math.sin(Math.toRadians(angle)) + (temp2/AB)*Math.cos(Math.toRadians(angle));
		x = start.x + R*cosp;
		y = start.y + R*sinp;
		p = new Point(x, y);
		return p;
	}
	
	
	// Hàm chuyển đổi góc
	public void convertAngles() {
		for (int i=0; i < n; i++) {
			points[i] = PSOPath.convertAtoP(angles[i], PSO.R, PSO.startPoint, PSO.endPoint);
		}
	}
	

	public double pathDistance(double R) {
		double dis = R;
		double temp1;
		double a2, b2, distance;
		a2 = Math.pow(Math.abs(PSO.startPoint.x - PSO.endPoint.x), 2);
	    b2 = Math.pow(Math.abs(PSO.startPoint.y - PSO.endPoint.y), 2);
	    distance = Math.sqrt(a2 + b2);
		for (int i = 1; i < n; i++) {
			dis += R*Math.sqrt(Math.pow(i, 2) + Math.pow(i+1, 2) - 2*i*(i+1)*Math.cos(Math.toRadians(Math.abs(angles[i]-angles[i-1]))));
		}
		temp1 = Math.sqrt(Math.pow(n*R, 2) + Math.pow(distance, 2) - 2*n*R*distance*Math.cos(Math.toRadians(Math.abs(angles[n-1]))));
		dis += temp1;
		return dis;
	}
	
	// Hàm tính khoảng cách
	public double pathDistancePoint() {
		double dis = 0;
		dis += Math.sqrt(Math.pow(points[0].x-0, 2) + Math.pow(points[0].y-0, 2));
		for (int i = 0; i < n-1; i++) {
			dis += Math.sqrt(Math.pow(points[i+1].x-points[i].x, 2) + Math.pow(points[i+1].y-points[i].y, 2));
		}
		dis += Math.sqrt(Math.pow(points[n-1].x-PSO.endPoint.x, 2) + Math.pow(points[n-1].y-PSO.endPoint.y, 2));
		return dis;
	}
	
	static public double disPointSeg(Point S0, Point S1, Point A) {
		double px = S1.x - S0.x;
        double py = S1.y - S0.y;
        double temp = (px*px) + (py*py);
        double u = ((A.x - S0.x) * px + (A.y - S0.y) * py) / (temp);
        if(u > 1){
            u = 1;
        }
        else if(u < 0){
            u = 0;
        }
        double x = S0.x + u * px;
        double y = S0.y + u * py;
        double dx = x - A.x;
        double dy = y - A.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        return dist;
	}
}
