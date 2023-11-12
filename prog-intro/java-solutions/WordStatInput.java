import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;


public class WordStatInput {


    private static boolean isWord(char ch) {
        return Character.isLetter(ch) || Character.getType(ch) == Character.DASH_PUNCTUATION || ch == '\'';
    }


    public static void main(String[] args) throws IOException{ 
        LinkedHashMap<String, Integer> wordsStat = new LinkedHashMap<>();
        int k;

        try {
            try (MyScanner scan = new MyScanner(new FileInputStream(args[0]), StandardCharsets.UTF_8)) {
                String str;
                while ((str = scan.nextLine()) != null) {
                    str = " " + str + " ";
                    for (int i = 0; i < str.length(); i++) {
                        k = i;
                        while (isWord(str.charAt(i))) {
                            i++;
                        }
                        if (k != i) {
                            String word = str.substring(k, i).toLowerCase();
                            if (wordsStat.containsKey(word)) {
                                wordsStat.compute(word, (a, b) -> b + 1);
                            } else {
                                wordsStat.put(word, 1);
                            }
                        }
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                    Set<String> keys = wordsStat.keySet();
                    for (String key : keys) {
                        writer.write(key + " " + wordsStat.get(key) + System.lineSeparator());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Reached error while reading" + args[0] + ":" + e.getMessage());
        }
    }
}