/**
 * @author mazehong
 * @version v1.0
 */
package syntaxAnalysis;

public class Token {
    public String tokenStr;
    public TokenType tokenType;
    public int tokenLineNum;
    public Token(String inputTokenStr, TokenType inputTokenType){
        this.tokenStr = inputTokenStr;
        this.tokenType = inputTokenType;
    }

    public Token(String inputTokenStr, TokenType inputTokenType, int inputLineNum){
        this.tokenStr = inputTokenStr;
        this.tokenType = inputTokenType;
        this.tokenLineNum = inputLineNum;
    }

    public boolean equals(Token inputToken){
        if(inputToken.tokenStr.equals(tokenStr)&&inputToken.tokenType==tokenType){
            return true;
        }else{
            return false;
        }
    }
}
