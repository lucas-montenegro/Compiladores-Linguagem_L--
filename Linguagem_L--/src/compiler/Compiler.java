package compiler;

import lexical.LexicalScannerL;
import lexical.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        try {
            // start lexical analysis
            if(args.length > 1 || args.length == 0) {
                System.out.println("Envie apenas um arquivo para ser compilado!");
                return;
            }
            LexicalScannerL lexicalScanner = new LexicalScannerL(args);

            // program token information
            ArrayList<Token> tokens = lexicalScanner.getTokens();

            //lexicalScanner.ErrorMessages();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }
}
