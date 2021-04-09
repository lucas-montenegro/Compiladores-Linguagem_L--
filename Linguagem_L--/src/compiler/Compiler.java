package compiler;

import lexical.LexicalScannerL;
import lexical.Token;
import lexical.TokenClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        if(args.length != 1) {
            System.out.println("Envie apenas um arquivo para ser compilado!");
            return;
        }

        try {
            String lineRow, line, filePath = args[0];
            LexicalScannerL lexicalScanner = new LexicalScannerL();
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            // start lexical analysis
            while (scanner.hasNextLine()) { // loop to read all the program line by line
                line = scanner.nextLine();
                lineRow = "%04d  ";
                System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
                System.out.println(line);

                line = line + '\n';
                lexicalScanner.restartPos(); // restart position to the beginning of each line

                while(true) { // search for all tokens in the current line
                    lexicalScanner.nextColumn(); // updating the current column
                    Token currentToken = lexicalScanner.nextToken(line); // read and classify the current token

                    if(currentToken == null) { break; }
                    else { System.out.println(currentToken.toString()); }
                }

                lexicalScanner.nextRow(); // updating the current row
            }

            lineRow = "%04d  ";
            System.out.println(String.format(lineRow, lexicalScanner.getRow()));
            Token EOFToken = new Token("EOF", TokenClass.ENDFILE, lexicalScanner.getRow(), lexicalScanner.getColumn());
            System.out.println(EOFToken.toString());

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
