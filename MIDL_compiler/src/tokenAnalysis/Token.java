package tokenAnalysis;

public class Token {
    public String tokenStr;
    public TokenType tokenType;
    public Token(String inputTokenStr, TokenType inputTokenType){
        this.tokenStr = inputTokenStr;
        this.tokenType = inputTokenType;
    }
}
