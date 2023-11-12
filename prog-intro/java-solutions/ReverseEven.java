import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.copyOf;

public class ReverseEven {

    public static boolean check(char ch) { return ch >= '0' && ch <= '9' || ch == '-'; }


    public static void main(String[] args) {
        try (MyScanner in = new MyScanner(System.in, StandardCharsets.UTF_8)) {
            int[][] ans = new int[1][];
            int countLines = 0;
            while (in.hasNextLine()) {
                countLines++;
                String str = in.nextLine();
                int numsAmountInStr = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (check(str.charAt(i))) {
                        numsAmountInStr++;
                        while (i < str.length() && check(str.charAt(i))) {
                            i++;
                        }
                    }
                }
                int[] nums = new  int[numsAmountInStr];
                MyScanner scanStr = new MyScanner(str);
                for (int i = 0; i < numsAmountInStr; i++) {
                    nums[i] = scanStr.nextInt();
                }
                if (ans.length - 1 < countLines) {
                    ans = copyOf(ans, ans.length * 2);
                }
                ans[countLines - 1] = nums;
            }
            for (int i = countLines - 1; i >= 0; i--) {
                for (int j = ans[i].length - 1; j >= 0; j--) {
                    if (ans[i][j] % 2 == 0) {
                        System.out.print(ans[i][j] + " ");
                    }
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
