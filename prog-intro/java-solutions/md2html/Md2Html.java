package md2html;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Md2Html {
    private static StringBuilder markHeader = new StringBuilder(); // чтобы не делать присвоений в функциях
    private static int pos; // чтобы можно было вынести замену в функцию(иначе не знаем откуда продолжать)
    private static int[][] symbolsData = new int[7][3]; // [0]: `,[1]: *, [2]: **, [3]: _, [4]: __, [5]: --; [0][0] - количество, [0][1] - четность, [0][2] - флаги
    private static Map<Character, String> specialSymblos = Map.of('&', "&amp;", '<', "&lt;", '>', "&gt;");

    private static void getAmount(StringBuilder sb) {
        symbolsData = new int[7][3];
        sb.append(" ");
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '`') {
                symbolsData[0][0] += 1;
            }
        }
        int ignoreAmountTemp = symbolsData[0][0];
        symbolsData[0][1] = symbolsData[0][0] % 2;
        for (int i = 1; i < sb.length(); i++) {
            if (sb.charAt(i - 1) == '\\') {
                i++;
            } else if (sb.charAt(i - 1) == '`') {
                if (symbolsData[0][1] == 0) {
                    i++;
                    while (sb.charAt(i - 1) != '`') {
                        i++;
                    }
                } else {
                    if (ignoreAmountTemp > 1) {
                        i++;
                        ignoreAmountTemp -= 1;
                        while (sb.charAt(i - 1) != '`') {
                            i++;
                        }
                        ignoreAmountTemp -= 1;
                    }
                }
            } else if (sb.charAt(i - 1) == '*') {
                if (sb.charAt(i) == '*') {
                    symbolsData[2][0] += 1;
                    i++;
                } else {
                    symbolsData[1][0] += 1;
                }
            } else if (sb.charAt(i - 1) == '_') {
                if (sb.charAt(i) == '_') {
                    symbolsData[4][0] += 1;
                    ;
                    i++;
                } else {
                    symbolsData[3][0] += 1;
                }
            } else if (sb.charAt(i - 1) == '-' && sb.charAt(i) == '-') {
                symbolsData[5][0] += 1;
            } else if (sb.charAt(i - 1) == '~') {
                symbolsData[6][0] += 1;
            }
        }
        for (int i = 0; i < symbolsData.length; i++) {
            symbolsData[i][1] = symbolsData[i][0] % 2;
        }
    }

    private static void markHead(StringBuilder markHeader) {
        int headerSmblCnt = 0;
        markHeader.delete(markHeader.length() - 2, markHeader.length() - 1);
        while (markHeader.charAt(headerSmblCnt) == '#' && headerSmblCnt < markHeader.length() - 1) {
            headerSmblCnt += 1;
        }
        if (headerSmblCnt != 0 && Character.isWhitespace(markHeader.charAt(headerSmblCnt))) {
            markHeader.delete(markHeader.length() - System.lineSeparator().length(), markHeader.length());
            markHeader.replace(0, headerSmblCnt + 1, "<h" + headerSmblCnt + ">");
            markHeader.append("</h" + headerSmblCnt + ">");
        } else {
            markHeader.delete(markHeader.length() - System.lineSeparator().length(), markHeader.length());
            markHeader.insert(0, "<p>");
            markHeader.append("</p>");
        }
        markHeader.append(System.lineSeparator());
    }

    private static void markHtml(int dataNum, String htmlOpen, String htmlClose, int symbolsDeleteAmount) {
        if (symbolsDeleteAmount == 2) {
            pos++;
        }
        if (symbolsData[dataNum][1] == 0 || symbolsData[dataNum][0] > 1) {
            if (symbolsData[dataNum][2] == 0) {
                markHeader.replace(pos - symbolsDeleteAmount, pos, htmlOpen);
                symbolsData[dataNum][2] = 1;
                pos += htmlOpen.length() - symbolsDeleteAmount;
            } else {
                markHeader.replace(pos - symbolsDeleteAmount, pos, htmlClose);
                symbolsData[dataNum][2] = 0;
                pos += htmlClose.length() - symbolsDeleteAmount;
            }
        }
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                String temp = reader.readLine();
                boolean checker = true;
                while (checker) { //чтобы обработало последний абзац
                    if (temp == null) {
                        checker = false;
                    }
                    if (temp != null && !temp.isEmpty()) { // обязательно в этом порядке иначе выдаст ошибку, null нельзя проверять на isEmpty
                        markHeader.append(temp + System.lineSeparator());
                    } else if (!markHeader.isEmpty()){ // чтобы не обрабатывать пустые строки
                        getAmount(markHeader);
                        for (pos = 1; pos < markHeader.length(); pos++) {
                            if (specialSymblos.containsKey(markHeader.charAt(pos - 1))) {
                                markHeader.replace(pos - 1, pos, specialSymblos.get(markHeader.charAt(pos - 1)));
                                pos += specialSymblos.get(markHeader.charAt(pos - 1)).length() - 2;
                            } else if (markHeader.charAt(pos - 1) == '\\') {
                                markHeader.delete(pos - 1, pos);
                            } else if (markHeader.charAt(pos - 1) == '`') {
                                markHtml(0, "<code>", "</code>", 1);
                            } else if (markHeader.charAt(pos - 1) == '*') {
                                if (markHeader.charAt(pos) == '*') {
                                    markHtml(2, "<strong>", "</strong>", 2);
                                } else {
                                    markHtml(1, "<em>", "</em>", 1);
                                }
                            } else if (markHeader.charAt(pos - 1) == '_') {
                                if (markHeader.charAt(pos) == '_') {
                                    markHtml(4, "<strong>", "</strong>", 2);
                                } else {
                                    markHtml(3, "<em>", "</em>", 1);
                                }
                            } else if (markHeader.charAt(pos - 1) == '-' && markHeader.charAt(pos) == '-' ) {
                                markHtml(5,  "<s>", "</s>", 2);
                            } else if (markHeader.charAt(pos - 1) == '~') {
                                markHtml(6, "<mark>", "</mark>", 1);
                            }
                        }
                        markHead(markHeader);
                        writer.write(markHeader.toString());
                        markHeader.setLength(0);
                    }
                    temp = reader.readLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("3rr0r" + ex.getMessage());
        }
    }
}

