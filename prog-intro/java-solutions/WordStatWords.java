import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class WordStatWords {


    private static boolean isWord(char ch) {
        return Character.isLetter(ch) || Character.getType(ch) == Character.DASH_PUNCTUATION || ch == '\'';
    }

    
    public static void main(String[] args) {
        Map<String, Integer> wordsStat = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            String str;
            while ((str = reader.readLine()) != null) {
                str = " " + str + " ";
                for (int i = 0; i < str.length(); i++) {
                    int k = i;
                    while (isWord(str.charAt(i))) {
                        i++;
                    }
                    if (k != i) {
                        String word = str.substring(k, i).toLowerCase();
                        wordsStat.merge(word, 1, Integer::sum);
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
        } catch (IOException e) {
            System.out.println("Reached error while reading" + args[0] + ":" + e.getMessage());
        }
    }
}