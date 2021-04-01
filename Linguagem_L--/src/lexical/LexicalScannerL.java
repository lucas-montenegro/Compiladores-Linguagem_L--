package lexical;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class LexicalScannerL {
    private ArrayList<Token> tokens;
    private String line;
    private int pos = 0;
    private int row = 0;
    private int column = 0;
    //private int state = 0;

    public LexicalScannerL() {
        this.tokens = new ArrayList<Token>();
    }

    public void readFile(String filename) throws FileNotFoundException { // read all the file line by line
        try {
            File file = new File("C:\\Users\\lucas\\ufal\\5_periodo\\compiladores\\Compiladores-Linguagem_L--\\Linguagem_L--\\src\\compiler\\teste.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { // loop to read all the program line by line
                line = scanner.nextLine();
                pos = 0; // restart position to the beginning of each line

                while(true) { // search for all tokens in the current line
                    nextColumn(); // updating the current column
                    Token currentToken = nextToken(); // read and classify the current token

                    if(currentToken == null) { break; }
                    else { tokens.add(currentToken); }
                }

                nextRow(); // updating the current row
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Token nextToken() { // TODO
        char currentChar;
        int state = 0;
        String currentTokenValue = "";

        try {
            while(true) {
                if(isEOF()) { return null; }
                currentChar = getCurrentChar();

                switch (state) {
                    case 0: // initial state of the automaton
                        if(isChar(currentChar)) { // if the initial terminal is a character, goes to state 1
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 1;
                        }
                        else if(isSpace(currentChar)) { // if the initial terminal is a space, ignore it and remains in state 0
                            nextChar();
                            nextColumn(); // updates the column if the previous char was a space
                            state = 0;
                        }
                        else {
                            state = 10; // TODO goes to default case
                        }
                        break;
                    case 1:
                        if(isChar(currentChar) || isDigit(currentChar) || currentChar == '_')  {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 1;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.ID, row, column);
                        }

                        break;
                    default:
                        return null;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    private char getCurrentChar() { return line.charAt(pos); }

    private void nextChar() { pos++; }

    private void previousChar() { pos--; }

    private void nextColumn() { // update the current column
        column = pos;
    }

    private void nextRow() { // update the current row
        row++;
    }

    public void ErrorMessages() { // print all the possible lexical errors
        int errorFlag = 0;
        for (Token token : tokens) {
            if(token.getTokenClass() ==  TokenClass.UNKNOWN) {
                System.out.println("Lexical error: Unknown token type at line: " + token.getTokenRow() + ", column: " + token.getTokenColumn());
                errorFlag = 1;
            }
        }

        if(errorFlag == 0) {
            System.out.println("Compiled successful with no lexical errors");
        }
    }

    public ArrayList<Token> getTokens() { // get all the tokens of the program
        return tokens;
    }
}
