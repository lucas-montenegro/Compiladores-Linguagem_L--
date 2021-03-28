package lexico;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScannerL {
    private char[] content;
    private int state;
    private int pos;

    public ScannerL(String filename) {
        try {
            String txtContent;
            txtContent = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            System.out.println("Debug --------");
            System.out.println(txtContent);
            System.out.println("--------------");
            content = txtContent.toCharArray();
            pos = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Creating recognizing functions
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); //
    }

    private boolean isOperator(char c) {
        return (c == '>') || (c == '<') || (c == '=') || (c == '!') || (c == '&') || (c == '|') || (c == '+')
                || (c == '-') || (c == '^') || (c == '*') || (c == '/') || (c == '%');
    }

    private boolean isSpace(char c) {
        return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
    }

    private char nextChar() {
        return content[pos++];
    }

    private boolean isEOF() {
        return pos == content.length;
    }
}
