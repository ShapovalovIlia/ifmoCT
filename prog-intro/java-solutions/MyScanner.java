import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.*;
import java.util.InputMismatchException;


public class MyScanner implements AutoCloseable {
    private final Reader reader;
    private final char[] buffer = new char[1024];
    private int bufferPos = 0;
    private int readLength = -1;
    private String nextLineCache = null;
    private Integer nextIntCache = null;
    private String nextOctCache = null;

    private final int crutch = System.lineSeparator().length();


    public MyScanner(InputStream source, Charset ch) throws IOException { // конструктор для ридера от файла или консоли
        reader = new InputStreamReader(source, ch);
    }

    public MyScanner(String str) { // конструктор для ридера от строки
        reader = new StringReader(str);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public void fillBufferIf() throws IOException{ // проверка на то, есть ли что-то непрочитанное в буффере + заполнение
        if (bufferPos >= readLength) {
            readLength = reader.read(buffer);
            bufferPos = 0;
        }
    }

    public boolean hasNextLine() throws IOException {
        nextLineCache = nextLine();
        return nextLineCache != null;
    }

    public String nextLine() throws IOException {
        if (nextLineCache != null) {
            String temp = nextLineCache;
            nextLineCache = null;
            return temp;
        }
        fillBufferIf();
        int bufferStart = bufferPos;
        StringBuilder line = new StringBuilder();
        if (readLength == -1) {
            return null;
        }
        if (readLength == 0) {
            return "";
        }
        while (true) { // :NOTE: нужно больше вариантов для перевода строк под разными ОС
            if (crutch == 1 && buffer[bufferPos] == '\n') {
                break;
            }
            if (buffer[bufferPos] == '\r') {
                if (crutch == 2) {
                    bufferPos++;
                }
                break;
            }
            bufferPos++;
            if (bufferPos >= readLength) {
                line.append(buffer, bufferStart, bufferPos - bufferStart);
                readLength = reader.read(buffer);
                bufferPos = 0;
                fillBufferIf();
                bufferStart = 0;
            }
            if (readLength == -1) {
                return line.toString();
            }
        }
        line.append(buffer, bufferStart, bufferPos - bufferStart);
        bufferPos++;
        return line.toString();
    }

    public int nextInt() throws IOException, InputMismatchException{
        if (nextIntCache != null) {
            int temp = nextIntCache;
            nextIntCache = null;
            return temp;
        }
        int num = 0;
        fillBufferIf();
        if (readLength == -1) {
            throw new InputMismatchException("Empty String");
        }
        while (Character.isWhitespace(buffer[bufferPos])) {
            bufferPos++;
            fillBufferIf();
        }
        int sign = 1;
        if (buffer[bufferPos] == '-') {
            sign = -1;
            bufferPos++;
        } else if (buffer[bufferPos] == '+') {
            bufferPos++;
        }
        fillBufferIf();
        while (Character.isDigit(buffer[bufferPos])) {
            num = num * 10 + Character.getNumericValue(buffer[bufferPos]); // :NOTE: нужно делать Integer.parse
            bufferPos++;
            fillBufferIf();
            if (readLength == -1) {
                return num * sign;
            }
        }
        if (!Character.isWhitespace(buffer[bufferPos])) {
            throw new InputMismatchException("Last character isn't a digit");
        }
        bufferPos++;
        return num * sign;
    }

    public boolean hasNextInt() throws IOException {
        try {
            nextIntCache = nextInt();
            return true;
        } catch (InputMismatchException ex){
            return false;
        }
    }

    public String nextOct() throws IOException, InputMismatchException{
        if (nextOctCache != null) {
            String temp = nextOctCache;
            nextOctCache = null;
            return temp;
        }
        fillBufferIf();
        StringBuilder oct = new StringBuilder();
        while (Character.isWhitespace(buffer[bufferPos])) {
            bufferPos++;
            fillBufferIf();
        }
        if (buffer[bufferPos] == '-') {
            oct.append('-');
            bufferPos++;
        }
        fillBufferIf();
        while (Character.isDigit(buffer[bufferPos])) {
            oct.append(buffer[bufferPos]);
            bufferPos++;
            fillBufferIf();
            if (readLength == -1) {
                return oct.toString();
            }
        }
        bufferPos++;
        return oct.toString();
    }

    public boolean hasNextOct() throws IOException, InputMismatchException{
        try {
            nextOctCache = nextOct();
            return true;
        } catch (InputMismatchException ex) {
            return false;
        }
    }
}
