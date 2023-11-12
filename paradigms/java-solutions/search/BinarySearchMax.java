package search;

public class BinarySearchMax {
    // l' are left border of a
    // r' are right border of a

    // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
    // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]
    static int findMaxIter(int[] a) {
        final int n = a.length;
        int l = 0;
        // l' = l = 0
        int r = n - 1;
        // r' = r = n - 1

        // n >= 0 -> 0 <= l' <= r <= n
        // l' <= r'
        while (l + 1 < r) {
            // l' + 1 < r' -> l' + 2 <= r'
            int m = (l + r) / 2;
            /* I: 0 <= l' < m < r' <= n && max ∈ a[l'...r']  ->
            (1) m < r'
            (2) m > l'

            Proof:
            (1) cond -> r' - 2 >= l'
                r' + r' > r' + r' - 2 >= r' + l'
                r' + r' > r' + l'
                (r' + r') / 2 > (r' + l') / 2
                r' > m

            (2) cond -> l' + 2 <= r'
                l' + l' < l' + l' + 2 <= l' + r'
                l' + l' < l' + r'
                (l' + l') / 2 < (l' + r') / 2
                l' < m
                m > l'

             */


            if (a[m] < a[l]) {
                // cond && a[m] < a[l'] ->
                // max ∈ a[l'...m']
                r = m;
                // r' = m > l'
                // max ∈ a[l'...r'] -> I
            } else {
                // cond && a[m] >= a[l'] ->
                // max ∈ a[m'...r']
                l = m;
                // l' = m < r'
                // max ∈ a[l'...r'] -> I
            }
            // I && cond
        }
        // l' + 1 >= r'
        // (a[r'] >= a[l']) || (a[r'] < a[l'])

        if (a[r] >= a[l]) {
            // a[r] >= a[l] -> max == a[r]
            return a[r];
        } else {
            // a[r] < a[l] -> max == a[l]
            return a[l];
        }

    }

    public static int findMaxRecursive(int[] a) {
        // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;

        return recursive(a,0, a.length);
        // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]

    }
    // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
    public static int recursive(int[] a, int l, int r) {
        // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]

        if (r - l == 1) {
            // l' == r' - 1
            // a[l'] > a[r']
            return a[l];
        }

        // l' < r'
        int m = (l + r) / 2;
        /* I: 0 <= l' < m < r' <= n && max ∈ a[l'...r']  ->

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
        if (a[m] > a[l]) {
            // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
            // 0 <= l' < r' <= n
            // cond && a[m] >=a[l'] ->
            // max ∈ a[m'...r']
            return recursive(a, m, r);
            // l' = m
            // r' = r
            // max ∈ a[l'...r']
            // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]

        }
        else {
            // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
            // 0 <= l' < r' <= n
            // cond && a[m] <= a[l'] ->
            // max ∈ a[l'...m]
            return recursive(a, l, m);
            // l' = l
            // r' = m
            // max ∈ a[l'...r']
            // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]
        }
    }



    public static void main(String[] args) {
        // Pred: args[n]
        // n > 0
        // for all (0 <= i < n) args[i] is integer and not null
        //
        final int n = args.length;
        int[] a = new int[n];
        int sum = 0;
        int result;
        for (int i = 0; i < n; i++) {
            // for all (0 <= i < n) args[i] is integer and not null
            a[i] = Integer.parseInt(args[i]);
            sum += Integer.parseInt(args[i]);
        }

        // Oddity Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
        // Oddity Post: sum % 2 ? use recursive : use iterative,
        // result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]
        if (sum % 2 == 0) {
            // sum % 2 == 0
            // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
            result = findMaxRecursive(a);
            // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]
        }
        else {
            // sum % 2 == 1
            // Pred for all i, j: 0 <= i < j < arr.length, except of k: arr[k] > arr[k + 1]: arr[i] >= arr[j]; arr.length >= 0;
            result = findMaxIter(a);
            // Post: result = max = a[k]: for all (0 <= i < n) \ {max} a[k] > a[i]
        }
        System.out.println(result);
    }
}
