import java.awt.BasicStroke;
import java.awt.Color;
import java.io.*;
// import java.util.ArrayList;
// import java.util.LinkedList;
// import java.util.Scanner;

// import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) throws IOException {   	
		GUIRobotics gui = new GUIRobotics(600, 150, 15);
//		gui.generateEnvironment("obstacles9.txt");
//		Graph test_graph = new Graph("obstacles9.txt");
		gui.generateEnvironment("room3.txt");
		Graph test_graph = new Graph("room3.txt");
		try {
			Point start = new Point(30, 40);
			Point end = new Point(120, 120);
			int R = 25;// Bán kính cung
			
			long startTimePSODE = System.currentTimeMillis();
			PSODE.startPoint = start;
			PSODE.endPoint = end;
			PSODE.R = R;
			PSODE.g = test_graph;
			PSODE myPSODE = new PSODE();
			gui.canvas.drawPoint(PSODE.startPoint, Color.BLUE);
			gui.canvas.drawPoint(PSODE.endPoint, Color.BLACK);
	        Thread.sleep(1000);

			// Vòng lặp t
			for (int i = 0; i < 1; i++) {
				myPSODE.PSO("results.txt");
			}
			PSODE.getNumR(PSODE.R, PSODE.startPoint, PSODE.endPoint);
			gui.canvas.drawLine(PSODE.startPoint,PSODE.gBestParticle);
			gui.canvas.drawLine(PSODE.gBestParticle,PSODE.endPoint);
			gui.canvas.drawPaths(PSODE.pBestParticles);
			
			// Đường đi HMOPSO
			gui.canvas.drawPoint(PSODE.gBestParticle.points, Color.RED);
			gui.canvas.drawPath(PSODE.gBestParticle, new BasicStroke(2), Color.MAGENTA);
			long timePSODE = System.currentTimeMillis() - startTimePSODE;
			
			
			long startTimePSO = System.currentTimeMillis();
			PSO.startPoint = start;
			PSO.endPoint = end;
			PSO.R = R;
			PSO.g = test_graph;
			PSO myPSO = new PSO();
			gui.canvas.drawPoint(PSO.startPoint, Color.BLUE);
			gui.canvas.drawPoint(PSO.endPoint, Color.BLACK);
	        Thread.sleep(1000);
			for (int i = 0; i < 1; i++) {
				myPSO.PSO("results.txt");
			}
			PSO.getNumR(PSO.R, PSO.startPoint, PSO.endPoint);
			gui.canvas.drawLine(PSO.startPoint,PSO.gBestParticle);
			gui.canvas.drawLine(PSO.gBestParticle,PSO.endPoint);
			gui.canvas.drawPaths(PSO.pBestParticles);
			
			// Đường đi PSO
			gui.canvas.drawPoint(PSO.gBestParticle.points, Color.blue);
			gui.canvas.drawPath(PSO.gBestParticle, new BasicStroke(2), Color.blue);
			long timePSO = System.currentTimeMillis() - startTimePSO; 
			
			
			System.out.println("Thời gian chạy của thuật toán PSODE : " + timePSODE + "ms");
			System.out.println("Thời gian chạy của thuật toán PSO: " + timePSO + "ms");
		}
		catch(Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("End!");
	}
}