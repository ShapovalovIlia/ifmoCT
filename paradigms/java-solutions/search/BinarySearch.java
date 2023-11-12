package search;

public class BinarySearch {

    // l' are left border of a
    // r' are right border of a

    // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
    // Post: result = min(i: a[i] <= x && 0 <= i <= n - 1)
    public static int iterative(int[] a, int x) {
        final int n = a.length;

        int l = 0;
        // l' = l = 0
        int r = n;
        // r' = r = n


        while (l < r) {
            int m = (l + r) / 2;
            /* I: 0 <= l' < m < r' <= n && x ∈ a[l'...r']  ->

            (1) m < r'
            (2) m > l'

            Proof:
            (1) cond -> r' > l'
                r' + r' > l' + r'
                (r' + r') / 2 > (l' + r') / 2
                r' > m'

            (2) cond -> r' > l'
                r' + l' > l' + l'
                (r' + l') / 2 > (l' + l') / 2
                m > l'

             */

            // I && cond
            if (a[m] > x) {
                // I && a[m] > x && cond
                // x < a[m] -> x ∈ a[(m+1)...r']
                l = m + 1;
                // I && l' = m + 1 && cond
                // x ∈ a[(m+1)=l'...r']

            }
            else {
                // I && a[m] <= x && cond
                // x >= a[m] -> x ∈ a[l'...m]
                r = m;
                // I && r' = m > l' && cond
                // x ∈ (a[l'...r'=m])
            }
            // I && cond
        }
        // r == l
        // r = min(i: a[i] <= x && 0 <= i <= n - 1)
        return r;
    }

    public static int recursive(int[] a, int x) {
        // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j] && l' = 0 && r' = a.length
        return recursive(a, x, 0, a.length);
        // Post: result = min(i: a[i] <= x && 0 <= i <= n - 1)

    }
    // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]

    public static int recursive(int[] a, int x, int l, int r) {
        // Post: result = min(i: a[i] <= x && 0 <= i <= n - 1)

        if (l >= r) {
            // l' >= r'
            // a[l'] <= a[r']

            return r;
        }

        // l' < r'
        int m = (l + r) / 2;
        /* I: 0 <= l' < m < r' <= n && x ∈ a[l'...r']  ->

            (1) m < r'
            (2) m > l'

            Proof:
            (1) l' < r' -> r' > l'
                r' + r' > l' + r'
                (r' + r') / 2 > (l' + r') / 2
                r' > m'

            (2) l' < r' -> r' > l'
                r' + l' > l' + l'
                (r' + l') / 2 > (l' + l') / 2
                m > l'

             */
        if (a[m] > x) {
            // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
            // 0 <= l' < r' <= n
            return recursive(a, x, m + 1, r);
            // l' = m + 1
            // r' = r
            // Post: result = min i: a[i] <= x && l' = (m + 1) <= i <= r'
        }
        else {
            // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
            // 0 <= l' < r' <= n
            return recursive(a, x, l, m);
            // l' = l
            // r' = m
            // Post: result = min i: a[i] <= x && l' <= i <= r' = m
        }
    }

    public static void main(String[] args) {
        // Pred: args[n]
        // n > 0
        // for all (0 <= i < n) args[i] is integer and not null
        // for all (1 <= i < j < n) args[j] <= args[i]
        final int x = Integer.parseInt(args[0]);
        // args[0] is integer and not null
        int[] a = new int[args.length - 1];
        int sum = x;
        for (int i = 0; i < a.length; i++) {
//             for all (1 <= i < n) args[i] is integer and not null
            a[i] = Integer.parseInt(args[i + 1]);
            sum += Integer.parseInt(args[i + 1]);
        }
        int result;
        // Oddity Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
        // Oddity Post: sum % 2 ? use recursive : use iterative,
        // result = min(i: a[i] <= x && 0 <= i <= n - 1)
        if (sum % 2 == 0) {
            // sum % 2 == 0
            // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
            result = recursive(a, x);
            // Post: result = min(i: a[i] <= x && 0 <= i <= n - 1)
        }
        else {
            // sum % 2 == 1
            // Pred: for all (i, j): 0 <= i < j < a.length: a[i] >= a[j]
            result = iterative(a, x);
            //  Post: result = min(i: a[i] <= x && 0 <= i <= n - 1)
        }
        System.out.println(result);
    }
}
