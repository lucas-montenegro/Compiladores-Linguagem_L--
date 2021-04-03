package compiler;

import lexical.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String filename = "C:\\Users\\lucas\\ufal\\5_periodo\\compiladores\\Compiladores-Linguagem_L--\\Linguagem_L--\\src\\compiler\\teste.txt"; // scanner.nextLine();

            // start lexical analysis
            LexicalScannerL lexicalScanner = new LexicalScannerL();
            lexicalScanner.readFile(filename);

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
