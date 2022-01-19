import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

// Đồ thị

public class Graph {
	public Polygons[] obstacles;
	public int num;
	
	public Graph() {
		num = 0;
		obstacles = new Polygons[0];
	}
	
	public Graph(int n, String fileName) throws IOException {
		Random rd = new Random();
		final double maxArea = 200;
		double x, y;
		Polygons[] obs = new Polygons[n];
		for (int i=0; i < n; i++) {
			int cornum = rd.nextInt(9);
			while (cornum < 3) {
				cornum = rd.nextInt(9);
			}
			obs[i] = new Polygons(cornum);
			obs[i] = obs[i].convexHull(obs[i].point, obs[i].cornum);
			while (obs[i].polygonArea() > maxArea) {
				obs[i].resizeArea(2);
			}
			System.out.println("Area: " + obs[i].polygonArea());
			x = rd.nextInt(100);
			y = rd.nextInt(100);
			obs[i].movePolygon(x, y);
			for (int j=0; j < i; j++) {
				while (CollisionDetection.polyPoly(obs[i], obs[j])) {
					x = rd.nextInt(100);
					y = rd.nextInt(100);
					obs[i].movePolygon(x, y);
					j = 0;
				}
			}
			System.out.println("Obstacles " + i + ": ");
			for (int g = 0; g < obs[i].cornum; g++) {
				System.out.print("(" + obs[i].point[g].x + ", " + obs[i].point[g].y + "), ");
			}
			System.out.println("");
			System.out.println("");
			num = n;
			obstacles = obs;
		}
		
		@SuppressWarnings("resource")
		FileWriter obtaclesFile = new FileWriter(fileName); 
		for (int i=0; i < n; i++) {
			obtaclesFile.write("-1");
			for (int g = 0; g < obs[i].cornum; g++) {
				obtaclesFile.write("\n");
				obtaclesFile.write(String.valueOf(obs[i].point[g].x));
				obtaclesFile.write(" ");
				obtaclesFile.write(String.valueOf(obs[i].point[g].y));
			}
			obtaclesFile.write("\n");
		}
		obtaclesFile.write("-1");
		obtaclesFile.close();
	}
	
	public static Polygons[] addPoly(int n, Polygons arr[], Polygons x) { 
        int i;
        Polygons[] newarr = new Polygons[n + 1]; 
        for (i = 0; i < n; i++) 
            newarr[i] = arr[i];  
        newarr[n] = x; 
        return newarr; 
    } 
	
	public Graph(String inputFile) throws FileNotFoundException {
		File file = new File(inputFile);
		Scanner scan;
		scan = new Scanner(file);
		scan.useLocale(Locale.US);
		int count_obs = 0;
		Polygons[] obs = new Polygons[0];
		int count_cor = 0;
		scan.nextDouble();
		Point[] point = new Point[30];
		while(scan.hasNextDouble()) {
			Double x = scan.nextDouble();
			if(x == -1) {
				for(int i = count_cor; i < 10; i++) 
					point[i] = null;
				Point[] point1 = new Point[count_cor];
				for(int i = 0; i < count_cor; i++) 
					point1[i] = point[i];
				Polygons poly = new Polygons();
				poly.cornum = count_cor;
				poly.point = point1;
				obs = addPoly(count_obs, obs, poly);
				count_obs++;
				count_cor = 0;				
			}	
			else {
				double y = scan.nextDouble();
				Point pt = new Point(x,y);
				point[count_cor] = pt;
				count_cor++;
			}
		}
		scan.close();	
		num = count_obs;
		obstacles = obs;
	}
}
