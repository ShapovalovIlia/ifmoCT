import java.io.*;
import java.util.LinkedHashMap;
import java.util.Set;


public class WordStatInputOld {


    private static boolean isWord(char ch) {
        return Character.isLetter(ch) || Character.getType(ch) == Character.DASH_PUNCTUATION || ch == '\'';
    }   


    public static void main(String[] args) { 
        LinkedHashMap<String, Integer> wordsStat = new LinkedHashMap<>();
        int k;

        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), "utf8"));
            try {
                String str; 
                while ((str = reader.readLine()) != null) { 
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
                BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(args[1]), "utf8"));
                try {
                    Set<String> keys = wordsStat.keySet();
                    for (String key: keys) {
                        writer.write(key + " " + wordsStat.get(key) + System.lineSeparator());
                    }
                } finally {
                    writer.close();
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Reached error while reading" + args[0] + ":" + e.getMessage());
        }
    }
}