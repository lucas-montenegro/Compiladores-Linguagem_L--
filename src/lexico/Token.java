package lexico;

public class Token {

    public static final int unknown = 0;
    public static final int main = 1;
    public static final int id = 2;
    public static final int typeInt = 3;
    public static final int typeFloat = 4;
    public static final int typeChar = 5;
    public static final int typeBool = 6;
    public static final int typeString = 7;
    public static final int constInt = 8;
    public static final int constFloat = 9;
    public static final int constBool = 10;
    public static final int constChar = 11;
    public static final int constString = 12;
    public static final int optAdd = 13;
    public static final int optPow = 14;
    public static final int optMult = 15;
    public static final int optDiv = 16;
    public static final int optSub = 17;
    public static final int optModu = 18;
    public static final int optAttib = 19;
    public static final int optLess = 20;
    public static final int optLessEqu = 21;
    public static final int optGrea = 22;
    public static final int optGreaEqu = 23;
    public static final int optEqu = 24;
    public static final int optNotEqu = 25;
    public static final int optAnd = 26;
    public static final int optOr = 27;
    public static final int optNot = 28;
    public static final int optConcat = 29;
    public static final int condIf = 30;
    public static final int condElsif = 31;
    public static final int condElse = 32;
    public static final int loopWhile = 33;
    public static final int loopFor = 34;
    public static final int read = 35;
    public static final int write = 36;
    public static final int separator = 37;
    public static final int paramInit = 38;
    public static final int paramEnd = 39;
    public static final int arrInit = 40;
    public static final int arrEnd = 41;
    public static final int comment = 42;
    public static final int funcDef = 43;
    public static final int funcReturn = 44;
    public static final int funcInit = 45;
    public static final int funcEnd = 46;
    public static final int sentenEnd = 47;
    public static final int endFile = 48;

    private int type;
    private String text;

    public Token(int type, String text) {
        super();
        this.type = type;
        this.text = text;
    }

    public Token() {
        super();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Token [ type = " + type + ", text = " + text + " ]";
    }

}
