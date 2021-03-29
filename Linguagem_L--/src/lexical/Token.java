package lexical;

public class Token {
    private final String tokenValue;
    private final TokenClass tokenClass;
    private final int tokenRow;
    private final int tokenColumn;


    public Token(String tokenValue, TokenClass tokenClass, int tokenRow, int tokenColumn) {
        this.tokenValue = tokenValue;
        this.tokenClass = tokenClass;
        this.tokenRow = tokenRow;
        this.tokenColumn = tokenColumn;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenClass getTokenClass() {
        return tokenClass;
    }

    public int getTokenRow() {
        return tokenRow;
    }

    public int getTokenColumn() {
        return tokenColumn;
    }

    @Override
    public String toString() {
        return "Token [ Value = " + tokenValue + ", Type = " + tokenClass + ", Row = " + tokenRow + ", Column = " + tokenColumn+ " ]";
    }

}
