package compiler;

import lexical.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {

    public static void main() {
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();

        // start lexical analysis
        LexicalScannerL lexicalScanner = new LexicalScannerL();
        lexicalScanner.readFile(filename);

        // TODO token information
        ArrayList<Token> tokens = lexicalScanner.getTokens();

        for (Token token : tokens) {
            System.out.println(token.toString());
        }


    }
}
