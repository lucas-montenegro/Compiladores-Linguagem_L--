package compiler;

import lexical.LexicalScannerL;
import parser.ParserL;

import java.io.FileNotFoundException;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        if(args.length != 1) {
            System.out.println("Envie apenas um arquivo para ser compilado!");
            return;
        }

        LexicalScannerL lexicalScannerL = new LexicalScannerL();
        ParserL parserL = new ParserL(lexicalScannerL);
        parserL.parser(args[0]);
    }
}
