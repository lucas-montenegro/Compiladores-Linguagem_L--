package parser;

import lexical.LexicalScannerL;
import lexical.Token;
import lexical.TokenClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParserL {
    private LexicalScannerL lexicalScannerL;
    private Token currentToken = null;
    private TokenClass currentTokenClass = null;
    private String line, lineRow;
    private Scanner scanner;

    public ParserL(LexicalScannerL lexicalScannerL) {
        this.lexicalScannerL = lexicalScannerL;
    }

    public void readNextLine() {
        if(scanner.hasNextLine()) {
            lexicalScannerL.nextRow(); // updating the current row
            line = scanner.nextLine();
            lineRow = "%04d  ";
            System.out.printf(String.format(lineRow, lexicalScannerL.getRow()));
            System.out.println(line);
            line = line + '\n';
            lexicalScannerL.restartPos(); // restart position to the beginning of each line
            getNextToken();
        }
        else {
            lineRow = "%04d  ";
            System.out.println(String.format(lineRow, lexicalScannerL.getRow()));
            currentToken = new Token("EOF", TokenClass.ENDFILE, lexicalScannerL.getRow(), lexicalScannerL.getColumn());
            currentTokenClass = currentToken.getTokenClass();
        }
    }

    public void getNextToken() {
        lexicalScannerL.nextColumn(); // updating the current column

        if(lexicalScannerL.isEOF(line) || line.charAt(lexicalScannerL.getPos()) != '\n' ) {
            currentToken = lexicalScannerL.nextToken(line); // read and classify the current token
            if(currentToken == null) { readNextLine(); }
            currentTokenClass = currentToken.getTokenClass();
        }
        else {
            readNextLine();
        }
    }

    public void tokenExpected(String tokens) {
        currentToken.toString();
        System.out.println();
        String tokenPosition = String.format("[%04d, %04d]", currentToken.getTokenRow(), currentToken.getTokenColumn());
        System.err.println("Error: Expected " + tokens + " at " + tokenPosition);
        System.exit(1);
    }

    public void printProduction(String leftProduction, String rightProduction) {
        String format = "%10s%s = %s";
        System.out.println(String.format(format, "", leftProduction, rightProduction));
    }

    public void parser(String filePath) {
        try {
            File file = new File(filePath);
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // preparing the variables to read the first token
        line = scanner.nextLine();
        lineRow = "%04d  ";
        System.out.printf(String.format(lineRow, lexicalScannerL.getRow()));
        System.out.println(line);

        line = line + '\n';
        lexicalScannerL.restartPos(); // restart position to the beginning of each line

        getNextToken();
        S();

        scanner.close();
    }

    void S() {
        if(typeCheck(currentTokenClass) || currentTokenClass == TokenClass.TYPEARRAY || currentTokenClass == TokenClass.FUNCDEF) {
            printProduction("S", "Decl S");
            Decl();
            S();
        }
        else if(currentTokenClass == TokenClass.ID) {
            printProduction("S", "Atrib S");
            Atrib();
            S();
        }
        else if(currentTokenClass == TokenClass.ENDFILE) {
            printProduction("S", "'eof'");
            System.out.println(currentToken.toString());
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array', 'function', 'id', 'eof'");
        }
    }

    public void Decl() {
        if(typeCheck(currentTokenClass)) {
            printProduction("Decl", "DeclVar");
            DeclVar();
        }
        else if(currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("Decl", "DeclArr");
            DeclArr();
        }
        else if(currentTokenClass == TokenClass.FUNCDEF) {
            printProduction("Decl", "DeclFunc");
            DeclFunc();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array', 'function'");
        }
    }

    public void DeclVar() {
        if(typeCheck(currentTokenClass)) {
            printProduction("DeclVar", "Type VarOp ';'");
            Type();
            VarOp();

            if(currentTokenClass == TokenClass.SENTENEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void VarOp() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("VarOp", "'id' CommaOp");
            System.out.println(currentToken.toString());
            getNextToken();
            CommaOp();
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void CommaOp() {
        if(currentTokenClass == TokenClass.SEPARATOR) {
            printProduction("CommaOp", "',' VarOp");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOp();
        }
        else {
            printProduction("CommaOp", "'épsilon'");
        }
    }

    public void DeclArr() {
        if(currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("DeclArr", "'array' Type ArrOp ';'");
            System.out.println(currentToken.toString());
            getNextToken();

            Type();
            ArrOp();

            if(currentTokenClass == TokenClass.SENTENEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }
        else {
            tokenExpected("'array'");
        }
    }

    public void ArrOp() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("ArrOp", "'id' '[' Expr ']' ArrCommaOp");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.ARRINIT) {
                System.out.println(currentToken.toString());
                getNextToken();

                Expr();

                if(currentTokenClass == TokenClass.ARREND) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    ArrCommaOp();
                }
                else {
                    tokenExpected("']'");
                }

            }
            else {
                tokenExpected("'['");
            }
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void ArrCommaOp() {
        if(currentTokenClass == TokenClass.SEPARATOR) {
            printProduction("ArrCommaOp", "',' ArrOp");
            System.out.println(currentToken.toString());
            getNextToken();
            ArrOp();
        }
        else {
            printProduction("ArrCommaOp", "'épsilon'");
        }
    }

    public void DeclFunc() {
        if(currentTokenClass == TokenClass.FUNCDEF) {
            printProduction("DeclFunc", "'function' TypeOrVoid MainOrId DeclParam Scope");
            System.out.println(currentToken.toString());
            getNextToken();

            TypeOrVoid();
            MainOrId();
            DeclParam();
            Scope();
        }
        else {
            tokenExpected("'function'");
        }
    }

    public void TypeOrVoid() {
        if(typeCheck(currentTokenClass)) {
            printProduction("TypeOrVoid", "Type");
            Type();
        }
        else if(currentTokenClass == TokenClass.TYPEVOID) {
            printProduction("TypeOrVoid", "'void'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'void'");
        }
    }

    public void MainOrId() {
        if(currentTokenClass == TokenClass.MAIN) {
            printProduction("MainOrId", "'main'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if(currentTokenClass == TokenClass.ID) {
            printProduction("MainOrId", "'id'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'main', 'id'");
        }
    }

    public void DeclParam() {
        if(currentTokenClass == TokenClass.PARAMINIT) {
            printProduction("DeclParam", "'(' ParamOpOrNoParam ')'");
            System.out.println(currentToken.toString());
            getNextToken();

            ParamOpOrNoParam();

            if(currentTokenClass == TokenClass.PARAMEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("')'");
            }
        }
        else {
            tokenExpected("'('");
        }
    }

    public void ParamOpOrNoParam() {
        if(typeCheck(currentTokenClass) || currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("ParamOpOrNoParam", "ParamOp");
            ParamOp();
        }
        else {
            printProduction("ParamOrNoParam", "'épsilon'");
        }
    }

    public void ParamOp() {
        if(typeCheck(currentTokenClass)) {
            printProduction("ParamOp", "DeclVarOp");
            DeclVarOp();
        }
        else if(currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("ParamOp", "DeclArrOp");
            DeclArrOp();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array'");
        }
    }

    public void DeclVarOp() {
        if(typeCheck(currentTokenClass)) {
            printProduction("DeclVarOp", "Type 'id' ParamCommaOp");
            Type();

            if(currentTokenClass == TokenClass.ID) {
                System.out.println(currentToken.toString());
                getNextToken();

                ParamCommaOp();
            }
            else {
                tokenExpected("'id'");
            }
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void DeclArrOp() {
        if(currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("DeclArrOp", "'array' Type 'id' '[' ']' ParamCommaOp");
            System.out.println(currentToken.toString());
            getNextToken();

            if(typeCheck(currentTokenClass)) {
                Type();

                if(currentTokenClass == TokenClass.ID) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    if(currentTokenClass == TokenClass.ARRINIT) {
                        System.out.println(currentToken.toString());
                        getNextToken();

                        if(currentTokenClass == TokenClass.ARREND) {
                            System.out.println(currentToken.toString());
                            getNextToken();

                            ParamCommaOp();
                        }
                        else {
                            tokenExpected("']'");
                        }
                    }
                    else {
                        tokenExpected("'['");
                    }
                }
                else {
                    tokenExpected("'id'");
                }
            }
            else {
                tokenExpected("'int', 'float', 'bool', 'char', 'string'");
            }
        }
        else {
            tokenExpected("'array'");
        }
    }

    public void ParamCommaOp() {
        if(currentTokenClass == TokenClass.SEPARATOR) {
            printProduction("ParamCommaOp", "',' ParamOp");
            System.out.println(currentToken.toString());
            getNextToken();
            ParamOp();
        }
        else {
            printProduction("ParamCommaOp", "'épsilon'");
        }
    }

    public void Scope() {
        if(currentTokenClass == TokenClass.FUNCINIT) {
            printProduction("Scope", "'{' Sentences '}'");
            System.out.println(currentToken.toString());
            getNextToken();
            Sentences();

            if(currentTokenClass == TokenClass.FUNCEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("'}'");
            }
        }
        else {
            tokenExpected("'{'");
        }
    }

    public void Sentences() {
        if(typeCheck(currentTokenClass)) {
            printProduction("Sentences", "DeclVar Sentences");
            DeclVar();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.TYPEARRAY) {
            printProduction("Sentences", "DeclArr Sentences");
            DeclArr();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.ID) {
            printProduction("Sentences", "AtribOrFuncCall Sentences");
            AtribOrFuncCall();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.READ || currentTokenClass == TokenClass.WRITE) {
            printProduction("Sentences", "Instructions Sentences");
            Instructions();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.CONDIF) {
            printProduction("Sentences", "IfConditional Sentences");
            IfConditional();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.LOOPWHILE) {
            printProduction("Sentences", "LogicalLoop Sentences");
            LogicalLoop();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.LOOPFOR) {
            printProduction("Sentences", "CountLoop Sentences");
            CountLoop();
            Sentences();
        }
        else if(currentTokenClass == TokenClass.FUNCRETURN) {
            printProduction("Sentences", "'return' Return ';' Sentences");
            System.out.println(currentToken.toString());
            getNextToken();
            Return();

            if(currentTokenClass == TokenClass.SENTENEND) {
                System.out.println(currentToken.toString());
                getNextToken();
                Sentences();
            }
            else {
                tokenExpected("';'");
            }
        }
        else if(currentTokenClass == TokenClass.BREAK) {
            printProduction("Sentences", "'break' ';' Sentences");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.SENTENEND) {
                System.out.println(currentToken.toString());
                getNextToken();
                Sentences();
            }
            else {
                tokenExpected("';'");
            }
        }
        else {
            printProduction("Sentences", "'épsilon'");
        }
    }

    public void AtribOrFuncCall() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("AtribOrFuncCall", "'id' CheckAtribOrFuncCall");
            System.out.println(currentToken.toString());
            getNextToken();
            CheckAtribOrFuncCall();
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void CheckAtribOrFuncCall() {
        if(currentTokenClass == TokenClass.ARRINIT) {
            printProduction("CheckAtribOrFuncCall", "'[' AtribOp1");
            System.out.println(currentToken.toString());
            getNextToken();
            AtribOp1();
        }
        else if(currentTokenClass == TokenClass.ATRIB) {
            printProduction("CheckAtribOrFuncCall", "'=' AtribOp2");
            System.out.println(currentToken.toString());
            getNextToken();
            AtribOp2();
        }
        else if(currentTokenClass == TokenClass.PARAMINIT) {
            printProduction("CheckAtribOrFuncCall", "'(' FuncCall");
            System.out.println(currentToken.toString());
            getNextToken();
            FuncCall();
        }
        else {
            tokenExpected("'[', '=', '('");
        }
    }

    public void AtribOp1() {
        if(faCheck(currentTokenClass)) {
            printProduction("AtribOp1", "Expr ']' '=' Expr ';'");
            Expr();

            if(currentTokenClass == TokenClass.ARREND) {
                System.out.println(currentToken.toString());
                getNextToken();

                if(currentTokenClass == TokenClass.ATRIB) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    Expr();

                    if (currentTokenClass == TokenClass.SENTENEND) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    } else {
                        tokenExpected("';'");
                    }
                } else {
                    tokenExpected("'='");
                }
            }
            else {
                tokenExpected("']'");
            }
        }
        else {
            tokenExpected("'(', '-', 'id', 'constInt', 'constFloat', 'constChar', 'constString', 'constBool'");
        }
    }

    public void AtribOp2() {
        printProduction("AtribOp2", "Expr ';'");
        Expr();

        if (currentTokenClass == TokenClass.SENTENEND) {
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("';'");
        }

    }

    public void FuncCall() {
        printProduction("FuncCall", "VarOpOrNoVar ')' ';'");
        VarOpOrNoVar();

        if(currentTokenClass == TokenClass.PARAMEND) {
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.SENTENEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }
        else {
            tokenExpected("')'");
        }
    }

    public void VarOpOrNoVar() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("VarOpOrNoVar", "VarOrArrParam");
            VarOrArrParam();
        }
        else {
            printProduction("VarOpOrNoVar", "'épsilon'");
        }
    }

    public void Instructions() {
        if (currentTokenClass == TokenClass.READ) {
            printProduction("Instructions", "'read' '(' VarOrArrParam ')' ';'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();
                VarOrArrParam();

                if (currentTokenClass == TokenClass.PARAMEND) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    if (currentTokenClass == TokenClass.SENTENEND) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    }
                    else {
                        tokenExpected("';'");
                    }
                }
                else {
                    tokenExpected("')'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else if (currentTokenClass == TokenClass.WRITE) {
            printProduction("Instructions", "'write' '(' 'constString' WriteParam ')' ';'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();

                if (currentTokenClass == TokenClass.CONSTSTRING) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    WriteParam();

                    if (currentTokenClass == TokenClass.PARAMEND) {
                        System.out.println(currentToken.toString());
                        getNextToken();

                        if (currentTokenClass == TokenClass.SENTENEND) {
                            System.out.println(currentToken.toString());
                            getNextToken();
                        }
                        else {
                            tokenExpected("';'");
                        }
                    }
                    else {
                        tokenExpected("')'");
                    }
                }
                else {
                    tokenExpected("'constString'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else {
            tokenExpected("'read', 'write'");
        }
    }

    public void WriteParam() {
        if (currentTokenClass == TokenClass.SEPARATOR) {
            printProduction("WriteParam", "',' VarOrArrParam");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOrArrParam();
        }
        else {
            printProduction("WriteParam", "'épsilon'");
        }
    }

    public void VarOrArrParam() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("VarOrArrParam", "'id' VarOrArr VarOrArrCommaOp");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOrArr();
            VarOrArrCommaOp();
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void VarOrArr() {
        if(currentTokenClass == TokenClass.ARRINIT) {
            printProduction("VarOrArr", "'[' Expr ']'");
            System.out.println(currentToken.toString());
            getNextToken();
            Expr();

            if(currentTokenClass == TokenClass.ARREND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("']'");
            }
        }
        else {
            printProduction("VarOrArr", "'épsilon'");
        }
    }

    public void VarOrArrCommaOp() {
        if(currentTokenClass == TokenClass.SEPARATOR) {
            printProduction("VarOrArrCommaOp", "',' VarOrArrParam");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOrArrParam();
        }
        else {
            printProduction("VarOrArrCommaOp", "'épsilon'");
        }
    }

    public void IfConditional() {
        if(currentTokenClass == TokenClass.CONDIF) {
            printProduction("IfConditional", "'if' '(' Expr ')' Scope OtherConditional");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();
                Expr();

                if(currentTokenClass == TokenClass.PARAMEND) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    Scope();
                    OtherConditional();
                }
                else {
                    tokenExpected("')'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else {
            tokenExpected("'if'");
        }
    }

    public void OtherConditional() {
        if(currentTokenClass == TokenClass.CONDELSIF) {
            printProduction("OtherConditional", "ElsifConditional");
            ElsifConditional();
        }
        else if(currentTokenClass == TokenClass.CONDELSE) {
            printProduction("OtherConditional", "ElseConditional");
            ElseConditional();
        }
        else {
            printProduction("OtherConditional", "'épsilon'");
        }
    }

    public void ElsifConditional() {
        if(currentTokenClass == TokenClass.CONDELSIF) {
            printProduction("ElsifConditional", "'elsif' '(' Expr ')' Scope OtherConditional");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();
                Expr();

                if(currentTokenClass == TokenClass.PARAMEND) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    Scope();
                    OtherConditional();
                }
                else {
                    tokenExpected("')'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else {
            tokenExpected("'elsif'");
        }
    }

    public void ElseConditional() {
        if (currentTokenClass == TokenClass.CONDELSE) {
            printProduction("ElseConditional", "'else' Scope");
            System.out.println(currentToken.toString());
            getNextToken();
            Scope();
        }
        else {
            tokenExpected("'else'");
        }
    }

    public void LogicalLoop() {
        if (currentTokenClass == TokenClass.LOOPWHILE) {
            printProduction("LogicalLoop", "'while' '(' Expr ')' Scope");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();
                Expr();

                if(currentTokenClass == TokenClass.PARAMEND) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    Scope();
                }
                else {
                    tokenExpected("')'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else {
            tokenExpected("'while'");
        }
    }

    public void CountLoop() {
        if(currentTokenClass == TokenClass.LOOPFOR) {
            printProduction("CountLoop", "'for' '(' 'int' 'id' ',' 'constInt' ',' 'constInt' ',' 'constInt' ')' Scope");
            System.out.println(currentToken.toString());
            getNextToken();

            if(currentTokenClass == TokenClass.PARAMINIT) {
                System.out.println(currentToken.toString());
                getNextToken();

                if(currentTokenClass == TokenClass.TYPEINT) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    if(currentTokenClass == TokenClass.ID) {
                        System.out.println(currentToken.toString());
                        getNextToken();

                        if(currentTokenClass == TokenClass.SEPARATOR) {
                            System.out.println(currentToken.toString());
                            getNextToken();

                            if(currentTokenClass == TokenClass.CONSTINT) {
                                System.out.println(currentToken.toString());
                                getNextToken();

                                if(currentTokenClass == TokenClass.SEPARATOR) {
                                    System.out.println(currentToken.toString());
                                    getNextToken();

                                    if(currentTokenClass == TokenClass.CONSTINT) {
                                        System.out.println(currentToken.toString());
                                        getNextToken();

                                        if(currentTokenClass == TokenClass.SEPARATOR) {
                                            System.out.println(currentToken.toString());
                                            getNextToken();

                                            if(currentTokenClass == TokenClass.CONSTINT) {
                                                System.out.println(currentToken.toString());
                                                getNextToken();

                                                if(currentTokenClass == TokenClass.PARAMEND) {
                                                    System.out.println(currentToken.toString());
                                                    getNextToken();
                                                    Scope();
                                                }
                                                else {
                                                    tokenExpected("')'");
                                                }
                                            }
                                            else {
                                                tokenExpected("'constInt'");
                                            }
                                        }
                                        else {
                                            tokenExpected("','");
                                        }
                                    }
                                    else {
                                        tokenExpected("'constInt'");
                                    }
                                }
                                else {
                                    tokenExpected("','");
                                }
                            }
                            else {
                                tokenExpected("'constInt'");
                            }
                        }
                        else {
                            tokenExpected("','");
                        }
                    }
                    else {
                        tokenExpected("'id'");
                    }
                }
                else {
                    tokenExpected("'int'");
                }
            }
            else {
                tokenExpected("'('");
            }
        }
        else {
            tokenExpected("'for'");
        }
    }

    public void Return() {
        if(faCheck(currentTokenClass)) {
            printProduction("Return", "Expr");
            Expr();
        }
        else {
            printProduction("Return", "'épsilon'");
        }
    }

    public void Atrib() {
        if(currentTokenClass == TokenClass.ID) {
            printProduction("Atrib", "'id' IndiceOp '=' Expr ';'");
            System.out.println(currentToken.toString());
            getNextToken();
            IndiceOp();

            if(currentTokenClass == TokenClass.ATRIB) {
                System.out.println(currentToken.toString());
                getNextToken();
                Expr();

                if(currentTokenClass == TokenClass.SENTENEND) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                }
                else {
                    tokenExpected("';'");
                }
            }
            else {
                tokenExpected("'='");
            }
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void IndiceOp() {
        if(currentTokenClass == TokenClass.ARRINIT) {
            printProduction("IndiceOp", "'[' Expr ']'");
            System.out.println(currentToken.toString());
            getNextToken();
            Expr();

            if(currentTokenClass == TokenClass.ARREND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("']'");
            }
        }
        else {
            printProduction("IndiceOp", "'épsilon'");
        }
    }

    public void Expr() {
        if(faCheck(currentTokenClass) || currentTokenClass == TokenClass.OPTNOT) {
            printProduction("Expr", "Eb Exprl");
            Eb();
            Exprl();
        }
        else { // checking here to avoid unnecessary checks later
            tokenExpected("'!', '(', '-', 'id', 'constInt', 'constFloat', 'constChar', 'constString', 'constBool'");
        }
    }

    public void Exprl() {
        if(currentTokenClass == TokenClass.OPTCONCAT) {
            printProduction("Exprl", "'optConcat' Eb Exprl");
            System.out.println(currentToken.toString());
            getNextToken();
            Eb();
            Exprl();
        }
        else {
            printProduction("Exprl", "'épsilon'");
        }
    }

    public void Eb() {
        printProduction("Eb", "Tb Ebl");
        Tb();
        Ebl();
    }

    public void Ebl() {
        if(currentTokenClass == TokenClass.OPTOR) {
            printProduction("Ebl", "'optOr' Tb Ebl");
            System.out.println(currentToken.toString());
            getNextToken();
            Tb();
            Ebl();
        }
        else {
            printProduction("Ebl", "'épsilon'");
        }
    }

    public void Tb() {
        printProduction("Tb", "Fb Tbl");
        Fb();
        Tbl();
    }

    public void Tbl() {
        if(currentTokenClass == TokenClass.OPTAND) {
            printProduction("Tbl", "'optAnd' Fb Tbl");
            System.out.println(currentToken.toString());
            getNextToken();
            Fb();
            Tbl();
        }
        else {
            printProduction("Tbl", "'épsilon'");
        }
    }

    public void Fb() {
        if(currentTokenClass == TokenClass.OPTNOT) {
            printProduction("Fb", "'optNot' Fb Fbl");
            System.out.println(currentToken.toString());
            getNextToken();
            Fb();
            Fbl();
        }
        else if(faCheck(currentTokenClass)) {
            printProduction("Fb", "Ra Fbl");
            Ra();
            Fbl();
        }
        else {
            tokenExpected("'!', '(', '-', 'id', 'constInt', 'constFloat', 'constChar', 'constString', 'constBool'");
        }
    }

    public void Fbl() {
        if(optRel1Check(currentTokenClass)) {
            printProduction("Fbl", "OptRel1 Ra Fbl");
            OptRel1();
            Ra();
            Fbl();
        }
        else {
            printProduction("Fbl", "'épsilon'");
        }
    }

    public void Ra() {
        printProduction("Ra", "Ea Ral");
        Ea();
        Ral();
    }

    public void Ral() {
        if(optRel2Check(currentTokenClass)) {
            printProduction("Ral", "OptRel2 Ea Ral");
            OptRel2();
            Ea();
            Ral();
        }
        else {
            printProduction("Ral", "'épsilon'");
        }
    }

    public void Ea() {
        printProduction("Ea", "Ta Eal");
        Ta();
        Eal();
    }

    public void Eal() {
        if(currentTokenClass == TokenClass.OPTADD) {
            printProduction("Eal", "'optAdd' Ta Eal");
            System.out.println(currentToken.toString());
            getNextToken();
            Ta();
            Eal();
        }
        else if(currentTokenClass == TokenClass.OPTSUB) {
            printProduction("Eal", "'optSub' Ta Eal");
            System.out.println(currentToken.toString());
            getNextToken();
            Ta();
            Eal();
        }
        else {
            printProduction("Eal", "'épsilon'");
        }
    }

    public void Ta() {
        printProduction("Ta", "Pa Tal");
        Pa();
        Tal();
    }

    public void Tal() {
        if(currentTokenClass == TokenClass.OPTMULT) {
            printProduction("Tal", "'optMul' Pa Tal");
            System.out.println(currentToken.toString());
            getNextToken();
            Pa();
            Tal();
        }
        else if(currentTokenClass == TokenClass.OPTDIV) {
            printProduction("Tal", "'optDiv' Pa Tal");
            System.out.println(currentToken.toString());
            getNextToken();
            Pa();
            Tal();
        }
        else if(currentTokenClass == TokenClass.OPTMOD) {
            printProduction("Tal", "'optMod' Pa Tal");
            System.out.println(currentToken.toString());
            getNextToken();
            Pa();
            Tal();
        }
        else {
            printProduction("Tal", "'épsilon'");
        }
    }

    public void Pa() {
        printProduction("Pa", "Fa Pal");
        Fa();
        Pal();
    }

    public void Pal() {
        if (currentTokenClass == TokenClass.OPTPOW) {
            printProduction("Pal", "'optPow' Fa Pal");
            System.out.println(currentToken.toString());
            getNextToken();
            Fa();
            Pal();
        } else {
            printProduction("Pal", "'épsilon'");
        }
    }

    public void Fa() {
        if(currentTokenClass == TokenClass.PARAMINIT) {
            printProduction("Fa", "'(' Expr ')'");
            System.out.println(currentToken.toString());
            getNextToken();
            Expr();

            if(currentTokenClass == TokenClass.PARAMEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("')'");
            }
        }
        else if(currentTokenClass == TokenClass.OPTSUB) {
            printProduction("Fa", "'optSub' Fa");
            System.out.println(currentToken.toString());
            getNextToken();
            Fa();
        }
        else if(currentTokenClass == TokenClass.ID) {
            printProduction("Fa", "Variables");
            Variables();
        }
        else if(constCheck(currentTokenClass)) {
            printProduction("Fa", "Const");
            Const();
        }
        else {
            tokenExpected("'(', '-', 'id', 'constInt', 'constFloat', 'constChar', 'constString', 'constBool'");
        }
    }

    public void OptRel1() {
        if (currentTokenClass == TokenClass.OPTGREAT) {
            printProduction("OptRel1", "'>'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.OPTGREATEQ) {
            printProduction("OptRel1", "'>='");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.OPTLESSEQ) {
            printProduction("OptRel1", "'<='");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.OPTLESS) {
            printProduction("OptRel1", "'<'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'<=', '>=', '<', '>'");
        }
    }

    public void OptRel2() {
        if (currentTokenClass == TokenClass.OPTEQ) {
            printProduction("OptRel2", "'=='");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.OPTNOTEQ) {
            printProduction("OptRel2", "'!='");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'==', '!='");
        }
    }

    public void Variables() {
        if (currentTokenClass == TokenClass.ID) {
            printProduction("Variables", "'id' VarOrArrOrFunc");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOrArrOrFunc();
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void VarOrArrOrFunc() {
        if (currentTokenClass == TokenClass.ARRINIT) {
            printProduction("VarOrArrOrFunc", " '[' Expr ']'");
            System.out.println(currentToken.toString());
            getNextToken();
            Expr();

            if (currentTokenClass == TokenClass.ARREND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("']'");
            }
        }
        else if (currentTokenClass == TokenClass.PARAMINIT) {
            printProduction("VarOrArrOrFunc", "'(' VarOpOrNoVar ')'");
            System.out.println(currentToken.toString());
            getNextToken();
            VarOpOrNoVar();

            if (currentTokenClass == TokenClass.PARAMEND) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("')'");
            }
        }
        else {
            printProduction("VarOrArrOrFunc", "'épsilon'");
        }
    }

    public void Const() {
        if (currentTokenClass == TokenClass.CONSTBOOL) {
            printProduction("Const", "'constBool'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.CONSTINT) {
            printProduction("Const", "'constInt'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.CONSTFLOAT) {
            printProduction("Const", "'constFloat'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.CONSTCHAR) {
            printProduction("Const", "'constChar'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.CONSTSTRING) {
            printProduction("Const", "'constString'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'constInt', 'constFloat', 'constBool', 'constChar', 'consString'");
        }
    }

    public void Type() {
        if (currentTokenClass == TokenClass.TYPEBOOL) {
            printProduction("Type", "'bool'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.TYPEINT) {
            printProduction("Type", "'int'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.TYPEFLOAT) {
            printProduction("Type", "'float'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.TYPECHAR) {
            printProduction("Type", "'char'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenClass == TokenClass.TYPESTRING) {
            printProduction("Type", "'string'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public boolean optRel1Check(TokenClass currentTokenClass) {
        if (currentTokenClass == TokenClass.OPTLESS || currentTokenClass == TokenClass.OPTLESSEQ || currentTokenClass == TokenClass.OPTGREAT || currentTokenClass == TokenClass.OPTGREATEQ) {
            return true;
        }
        return false;
    }

    public boolean optRel2Check(TokenClass currentTokenClass) {
        if (currentTokenClass == TokenClass.OPTEQ || currentTokenClass == TokenClass.OPTNOTEQ) {
            return true;
        }
        return false;
    }

    public boolean faCheck(TokenClass currentTokenClass) {
        if(constCheck(currentTokenClass) || currentTokenClass == TokenClass.PARAMINIT || currentTokenClass == TokenClass.OPTSUB || currentTokenClass == TokenClass.ID) {
            return true;
        }
        return false;
    }

    public boolean constCheck(TokenClass currentTokenClass) {
        if (currentTokenClass == TokenClass.CONSTBOOL || currentTokenClass == TokenClass.CONSTINT || currentTokenClass == TokenClass.CONSTCHAR || currentTokenClass == TokenClass.CONSTFLOAT || currentTokenClass == TokenClass.CONSTSTRING) {
            return true;
        }
        return false;
    }

    public boolean typeCheck(TokenClass currentTokenClass) {
        if (currentTokenClass == TokenClass.TYPEBOOL || currentTokenClass == TokenClass.TYPEINT || currentTokenClass == TokenClass.TYPECHAR || currentTokenClass == TokenClass.TYPEFLOAT || currentTokenClass == TokenClass.TYPESTRING) {
            return true;
        }
        return false;
    }
}
