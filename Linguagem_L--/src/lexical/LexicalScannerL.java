package lexical;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class LexicalScannerL {
    private ArrayList<Token> tokens;
    private String line;
    private int pos = 0;
    private int row = 0;
    private int column = 0;

    public LexicalScannerL() {
        this.tokens = new ArrayList<Token>();
    }

    public void readFile(String filename) { // read all the file line by line
        try {
            File program = new File(filename);
            Scanner myReader = new Scanner(program);

            while (myReader.hasNextLine()) { // loop to read all the program line by line
                line = myReader.nextLine();
                pos = 0; // restart position to the beginning of each line

                while(!isEOF()) { // search for all tokens in the current line
                    nextColumn(); // updating the current column
                    Token currentToken = nextToken(); // read and classify the current token
                    tokens.add(currentToken);
                }

                nextRow(); // updating the current row
            }

            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Token nextToken() { // TODO
        return null;
        //return new Token(tokenValue, tokenClass, row, tokenColumn);
    }


    // recognizing methods
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

    private boolean isEOF() { // check if the line reached the end
        return pos == line.length();
    }
    // end of recognizing methods

    private char nextChar() { // goes to the next char of the line
        return line.charAt(pos++);
    }

    private void nextColumn() { // update the current column
        column = pos;
    }

    private void nextRow() { // update the current row
        row++;
    }

    public ArrayList<Token> getTokens() { // get all the tokens of the program
        return tokens;
    }
}
