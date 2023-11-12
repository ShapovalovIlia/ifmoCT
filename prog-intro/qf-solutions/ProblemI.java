import java.util.Scanner;
import java.lang.Math;

public class ProblemI { 
    public static int minX = (int) 1e8;
    public static int maxX = - (int) 1e8;
    public static int minY = (int) 1e8;
    public static int maxY = - (int) 1e8;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        for (int i = 0; i < n; i++) {
            int x = scan.nextInt();
            int y = scan.nextInt();
            int h = scan.nextInt();
            minX = Math.min(minX, x - h);
            maxX = Math.max(maxX, x + h);
            minY = Math.min(minY, y - h);
            maxY = Math.max(maxY, y + h);
        }
        scan.close();
        System.out.println((maxX + minX) / 2 + " " + (maxY + minY) / 2 + " " + (Math.max(maxY - minY, maxX - minX) + 1) / 2);
    }
}
