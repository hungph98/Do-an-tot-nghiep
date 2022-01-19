import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// Chướng ngại vật

public class Obstacle {

    static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    ArrayList<Point> points;

    public Obstacle(ArrayList<Point> points) {
        this.points = points;
    }


    public static ArrayList<Obstacle> getObtacles(String path_file) {
        ArrayList<Obstacle> obtacles = new ArrayList<>();

        try {
            System.setIn(new FileInputStream(path_file));
        } catch (FileNotFoundException e) {
            System.err.println( "File " + path_file + " Not Found");
            return null;
        }

        try (Scanner sc = new Scanner(System.in)) {
            ArrayList<Point> points = null;

            while (sc.hasNext()) {
                String s = sc.nextLine();
                if (s.equals("-1")) {
                    if (points != null && points.size() != 0)
                        obtacles.add(new Obstacle(points));
                    points = new ArrayList<>();
                    continue;
                }

                String temp[] = s.split("\\s+");
                points.add(new Point(Double.parseDouble(temp[0]), Double.parseDouble(temp[1])));
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return obtacles;
    }

    public static void main(String[] args) {
        ArrayList<Obstacle> obtacles = Obstacle.getObtacles("obtacles3.txt");
        for (Obstacle obtacle : obtacles){
            for(Point p : obtacle.points){
                System.out.print("(" + p.x + ", " + p.y + ") , ");
            }
            System.out.println();
        }
    }
}

