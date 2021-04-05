package lexical;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.Hashtable;

public class LexicalScannerL {
    private ArrayList<Token> tokens;
    Hashtable<String, TokenClass> tokenClasses = new Hashtable<String, TokenClass>();
    private String line;
    private int pos = 0;
    private int row = 0;
    private int column = 0;

    public LexicalScannerL(String[] args) {
        this.tokens = new ArrayList<Token>();
        fillTokenClasses();
        String filePath = args[0];

        try {
            readFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFile(String filePath) throws FileNotFoundException { // read all the file line by line
        String lineRow;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { // loop to read all the program line by line
                line = scanner.nextLine();
                lineRow = "%04d  ";
                System.out.printf(String.format(lineRow, row));
                System.out.println(line);

                line = line + '\n';
                pos = 0; // restart position to the beginning of each line

                while(true) { // search for all tokens in the current line
                    nextColumn(); // updating the current column
                    Token currentToken = nextToken(); // read and classify the current token

                    if(currentToken == null) { break; }
                    else if(currentToken.getTokenClass() == TokenClass.COMMENT) {
                        tokens.add(currentToken);
                        System.out.println(currentToken.toString());
                        break;
                    }
                    else {
                        tokens.add(currentToken);
                        System.out.println(currentToken.toString());
                    }
                }

                nextRow(); // updating the current row
            }

            lineRow = "%04d  ";
            System.out.println(String.format(lineRow, row));
            Token EOFToken = new Token("EOF", TokenClass.ENDFILE, row, column);
            tokens.add(new Token("EOF", TokenClass.ENDFILE, row, column));
            System.out.println(EOFToken.toString());

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Token nextToken() {
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
                        else if(isDigit(currentChar)) { // if the initial terminal is a digit, goes to state 2
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 2;
                        }
                        else if(isArithmeticOperator(currentChar)) { // if the initial terminal is an arithmetic operator, goes to state 5
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 5;
                        }
                        else if(isEqualOrAssignment(currentChar)) { // if the initial terminal is '=', goes to state 6
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 6;
                        }
                        else if(isRelationalOperator(currentChar)) { // if the initial terminal is a relational operator (except with '==' and '!='), goes to state 7
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 7;
                        }
                        else if(isNOTOperator(currentChar)) { // if the initial terminal is a not operator, goes to state 9
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 9;
                        }
                        else if(isANDOperator(currentChar)) { // if the initial terminal is &, goes to state 10
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 10;
                        }
                        else if(isOROperator(currentChar)) { // if the initial terminal is |, goes to state 11
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 11;
                        }
                        else if(isDelimiter(currentChar)) { // if the initial terminal is a delimiter, goes to state 13
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 13;
                        }
                        else if(isComment(currentChar)) { // if the initial terminal is the commentary symbol '$', goes to state 14
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 14;
                        }
                        else if(isSimpleQuote(currentChar)) { // if the initial terminal is simple quote ('), goes to state 15
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 15;
                        }
                        else if(isDoubleQuote(currentChar)) { // if the initial terminal is double quote ("), goes to state 19
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 19;
                        }
                        else if(currentChar == '@') { // if the initial terminal is the concat operator (@), goes to state 22
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 22;
                        }
                        else if(isSpace(currentChar) || isNewLine(currentChar)) { // if the initial terminal is a space or '\n', ignore it and remains in state 0
                            nextChar();
                            nextColumn(); // updates the column if the previous char was a space or \n
                            state = 0;
                        }
                        else {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 1:
                        if(isChar(currentChar) || isDigit(currentChar) || isUnderline(currentChar))  {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 1;
                        }
                        else {
                            return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                        }
                        break;
                    case 2:
                        if(isDigit(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 2;
                        }
                        else if(isDot(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 3;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.CONSTINT, row, column);
                        }
                        break;
                    case 3:
                        if(isDigit(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 4;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 4:
                        if(isDigit(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 4;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.CONSTFLOAT, row, column);
                        }
                        break;
                    case 5:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 6:
                        if(isEqualOrAssignment(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 8;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.ATRIB, row, column);
                        }
                        break;
                    case 7:
                        if(isEqualOrAssignment(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 8;
                        }
                        else {
                            return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                        }
                        break;
                    case 8:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 9:
                        if(isEqualOrAssignment(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 8;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.OPTNOT, row, column);
                        }
                        break;
                    case 10:
                        if(isANDOperator(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 12;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 11:
                        if(isOROperator(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 12;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 12:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 13:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 14:
                        return new Token(currentTokenValue, TokenClass.COMMENT, row, column);
                    case 15:
                        if(isChar(currentChar) || isDigit(currentChar) || (isSymbol(currentChar) && currentChar != '#')) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 16;
                        }
                        else if(currentChar == '\\') {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 17;
                        }
                        else if(isSimpleQuote(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 18;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 16:
                        if(isSimpleQuote(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 18;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 17:
                        if(isEscapeChar(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 16;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 18:
                        return new Token(currentTokenValue, TokenClass.CONSTCHAR, row, column);
                    case 19:
                        if(isChar(currentChar) || isDigit(currentChar) || isSymbol(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 19;
                        }
                        else if(currentChar == '\\') {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 20;
                        }
                        else if(isDoubleQuote(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 21;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 20:
                        if(isEscapeChar(currentChar)) {
                            currentTokenValue = currentTokenValue + currentChar;
                            nextChar();
                            state = 19;
                        }
                        else {
                            return new Token(currentTokenValue, TokenClass.UNKNOWN, row, column);
                        }
                        break;
                    case 21:
                        return new Token(currentTokenValue, TokenClass.CONSTSTRING, row, column);
                    case 22:
                        return new Token(currentTokenValue, TokenClass.OPTCONCAT, row, column);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fillTokenClasses() { // fills the hashtable with some token classes
        // reserved words
        tokenClasses.put("main", TokenClass.MAIN);
        tokenClasses.put("function", TokenClass.FUNCDEF);
        tokenClasses.put("return", TokenClass.FUNCRETURN);
        tokenClasses.put("if", TokenClass.CONDIF);
        tokenClasses.put("elsif", TokenClass.CONDELSIF);
        tokenClasses.put("else", TokenClass.CONDELSE);
        tokenClasses.put("for", TokenClass.LOOPFOR);
        tokenClasses.put("while", TokenClass.LOOPWHILE);
        tokenClasses.put("null", TokenClass.NULL);
        tokenClasses.put("break", TokenClass.BREAK);
        tokenClasses.put("void", TokenClass.TYPEVOID);
        tokenClasses.put("int", TokenClass.TYPEINT);
        tokenClasses.put("float", TokenClass.TYPEFLOAT);
        tokenClasses.put("char", TokenClass.TYPECHAR);
        tokenClasses.put("bool", TokenClass.TYPEBOOL);
        tokenClasses.put("string", TokenClass.TYPESTRING);
        tokenClasses.put("array", TokenClass.TYPEARRAY);
        tokenClasses.put("read", TokenClass.READ);
        tokenClasses.put("write", TokenClass.WRITE);
        tokenClasses.put("true", TokenClass.CONSTBOOL);
        tokenClasses.put("false", TokenClass.CONSTBOOL);

        // arithmetic operators
        tokenClasses.put("+", TokenClass.OPTADD);
        tokenClasses.put("-", TokenClass.OPTSUB);
        tokenClasses.put("/", TokenClass.OPTDIV);
        tokenClasses.put("*", TokenClass.OPTMULT);
        tokenClasses.put("^", TokenClass.OPTPOW);
        tokenClasses.put("%", TokenClass.OPTMOD);

        // concat operator
        tokenClasses.put("@", TokenClass.OPTCONCAT);

        // relational operators
        tokenClasses.put("<", TokenClass.OPTLESS);
        tokenClasses.put(">", TokenClass.OPTGREAT);
        tokenClasses.put("<=", TokenClass.OPTLESSEQ);
        tokenClasses.put(">=", TokenClass.OPTGREATEQ);
        tokenClasses.put("==", TokenClass.OPTEQ);
        tokenClasses.put("!=", TokenClass.OPTNOTEQ);

        // logical operators
        tokenClasses.put("&&", TokenClass.OPTAND);
        tokenClasses.put("||", TokenClass.OPTOR);

        // delimiters
        tokenClasses.put(",", TokenClass.SEPARATOR);
        tokenClasses.put(";", TokenClass.SENTENEND);
        tokenClasses.put("(", TokenClass.PARAMINIT);
        tokenClasses.put(")", TokenClass.PARAMEND);
        tokenClasses.put("[", TokenClass.ARRINIT);
        tokenClasses.put("]", TokenClass.ARREND);
        tokenClasses.put("{", TokenClass.FUNCINIT);
        tokenClasses.put("}", TokenClass.FUNCEND);
    }

    // character recognizing methods
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); //
    }

    /*private boolean isOperator(char c) {
        return (c == '>') || (c == '<') || (c == '=') || (c == '!') || (c == '&') || (c == '|') || (c == '+')
                || (c == '-') || (c == '^') || (c == '*') || (c == '/') || (c == '%');
    }*/

     private boolean isSymbol(char c) {
        return (c == '>') || (c == '<') || (c == '=') || (c == '!') || (c == '&') || (c == '|') || (c == '+')
                || (c == '-') || (c == '^') || (c == '*') || (c == '/') || (c == '%') || (c == ' ') || (c == ';') || (c == '.') || (c == ',') || (c == ':') || (c == '?') || (c == '_') || (c == '@') || (c == '#') || (c == '$') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{') || (c == '}');
    }

    private boolean isDelimiter(char c) {
        return (c == ',') || (c == ';') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{') || (c == '}');
    }

    private boolean isArithmeticOperator(char c) {
        return (c == '+') || (c == '-') || (c == '^') || (c == '*') || (c == '/') || (c == '%');
    }

    private boolean isRelationalOperator(char c) {
        return (c == '<') || (c == '>');
    }

    private boolean isNOTOperator(char c) {
        return (c == '!');
    }

    private boolean isANDOperator(char c) {
        return (c == '&');
    }

    private boolean isOROperator(char c) {
        return (c == '|');
    }

    private boolean isEqualOrAssignment(char c) {
        return c == '=';
    }

    private boolean isSpace(char c) {
        return (c == ' ');
    }

    private boolean isNewLine(char c) {
        return (c == '\n');
    }

    private boolean isEscapeChar(char c) {
        return (c == '\\') || (c == '\"') || (c == '\'') || (c == 'n') || (c == '#');
    }

    private boolean isSimpleQuote(char c) {
        return (c == '\'');
    }

    private boolean isDoubleQuote(char c) {
        return (c == '\"');
    }

    private boolean isUnderline(char c) {
        return c == '_';
    }

    private boolean isComment(char c) {
        return (c == '$');
    }

    private boolean isDot(char c) {
        return c == '.';
    }

    private boolean isEOF() { // check if the line reached the end
        return pos == line.length();
    }
    // end of character recognizing methods

    // Token classes recognizing methods
    private TokenClass checkTokenClasses(String TokenValue) {
        if(tokenClasses.containsKey(TokenValue)) {
            return tokenClasses.get(TokenValue);
        }
        else {
            return TokenClass.ID;
        }
    }

    // end of Token classes recognizing methods

    private char getCurrentChar() { return line.charAt(pos); }

    private void nextChar() { pos++; }

    /*private void previousChar() {
    pos--;
    }*/

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
                System.out.println("Lexical error: Unknown token type at row: " + token.getTokenRow() + ", column: " + token.getTokenColumn());
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