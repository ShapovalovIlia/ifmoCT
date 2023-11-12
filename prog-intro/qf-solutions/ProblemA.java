import java.util.Scanner;
import java.lang.Math;

public class ProblemA {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        double a = scan.nextInt();
        double b = scan.nextInt();
        double  n = scan.nextInt();
        scan.close();
        if (a != b) {
            System.out.println((int) (2 * Math.ceil((n-b)/(b-a)) + 1));
        }
    }
}
