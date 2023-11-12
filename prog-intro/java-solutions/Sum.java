public class Sum {
    public static void main(String[] args) {
        Integer sum = 0;
        for (int i = 0; i < args.length; i++) {
            int start = 0;
            String nums = " " + args[i] + " ";
            for (int k = 0; k < nums.length() - 1; k++) {
                if (Character.isWhitespace(nums.charAt(k)) && !Character.isWhitespace(nums.charAt(k + 1))) {
                    start = k + 1;
                }
                if (!Character.isWhitespace(nums.charAt(k)) && Character.isWhitespace(nums.charAt(k + 1))) {
                    sum += Integer.parseInt(nums.substring(start, k + 1));
                }
            }
        }
        System.out.print(sum);
    }
}
