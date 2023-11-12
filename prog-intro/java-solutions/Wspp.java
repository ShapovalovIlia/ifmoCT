import java.io.*;
import java.util.LinkedHashMap;
import java.util.Set;
import java.nio.charset.StandardCharsets;


public class Wspp {


	private static boolean isWord(char ch) {
        return Character.isLetter(ch) || Character.getType(ch) == Character.DASH_PUNCTUATION || ch == '\'';
    }	

	
    private static int spaceCount(String str) {
    	int spaceCounter = 0;
    	for (int i = 0; i < str.length(); i++) {
    		if (Character.isWhitespace(str.charAt(i))) {
    			spaceCounter++;
    		}
    	}
    	return spaceCounter;
    }


	public static void main(String[] args) { 
		LinkedHashMap<String, String> wordsStat = new LinkedHashMap<>();

		try (MyScanner scan = new MyScanner(new FileInputStream(args[0]), StandardCharsets.UTF_8)) {
			int counter = 0;
			while (scan.hasNextLine()) {	
				String str = ' ' + scan.nextLine() + ' ';
				for (int i = 0; i < str.length(); i++) {
					int k = i;
					while (isWord(str.charAt(i))) {
						i++;
					}  
					if (k != i) {
						counter += 1;
						String word = str.substring(k, i).toLowerCase();
						wordsStat.merge(word, Integer.toString(counter), (oldVal, newVal) -> oldVal + ' ' + newVal);
					}
				}
			}	
			try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
				Set<String> keys = wordsStat.keySet();
				for (String key: keys) {
					writer.write(key + " " + Integer.toString(spaceCount(wordsStat.get(key)) + 1) + " " + wordsStat.get(key) + System.lineSeparator());
				}
			} 
		} catch (IOException e) {
			System.out.println("Reached error while reading" + args[0] + ":" + e.getMessage());
		}
	}
}
