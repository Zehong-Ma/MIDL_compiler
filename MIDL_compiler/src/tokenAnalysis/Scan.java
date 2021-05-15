/**
 * @author mazehong
 * @version v1.0
 */
package tokenAnalysis;

public class Scan {
    public enum StateType{
        START, STATE1, STATE2, STATE3, STATE4, STATE5, STATE6, STATE7, STATE8, STATE9, DONE
    };
    private String reservedWords[] = {"struct", "float", "boolean", "short", "long", "double",
            "int8", "int16", "int32", "int64","uint8", "uint16", "uint32",
            "uint64" , "char", "unsigned" };
    private char specificSymbol[] = {'{', '}', ';', '[', ']', '*', '+', '-', '~', '/', '%', '>', '<',
                                        '&', '^', '|', ','};
    private char ch;
    private int ch_pos = 1;
    private int lineNum = 1;
    private char[] inputChars;

    public Scan(char[] inputChars){
        this.inputChars = inputChars;
        ch = inputChars[0];
    }

    /**
     * 词法分析的主入口
     * @return
     */
    public String wordAnalyze(){
        String res="";
        while(ch_pos<inputChars.length){

            Token token;
            if(ch == ' '||ch == '\t'||ch == '\n'||ch == '\r'){
                //行数计数
                if(ch=='\n')
                    lineNum++;
            }else{
                /** 专用字符的识别*/
                if(isSpecificSymbol(ch)){
                    if(ch=='<'||ch=='>'){
                        ch = getNextChar();
                        if(ch=='<'){
                            token = new Token("<<",TokenType.SPECIFICSYMBOL);
                            res += token.tokenStr + "\t \t" + String.valueOf(token.tokenType) +"\t \t"+ String.valueOf(lineNum) + '\n';
                            ch = getNextChar();
                            continue;
                        }else if(ch=='>'){
                            token = new Token(">>",TokenType.SPECIFICSYMBOL);
                            res += token.tokenStr + "\t \t" + String.valueOf(token.tokenType) +"\t \t"+ String.valueOf(lineNum)+ '\n';
                            ch = getNextChar();
                            continue;
                        }
                        continue;
                    }else{
                        token = new Token(String.valueOf(ch),TokenType.SPECIFICSYMBOL);
                        res += token.tokenStr + "\t \t" + String.valueOf(token.tokenType) +"\t \t"+ String.valueOf(lineNum)+ '\n';

                    }
                }else{
                    /*  四个正则表达式的识别 */
                    token = getToken();

                    if(token.tokenType==TokenType.ERROR){
                        res+= "Error happens in row"+ lineNum +" : " + token.tokenStr+'\n';
                        //System.err.println("Error happens in row"+ lineNum +" : " + token.tokenStr+'\n');
                        if(token.tokenStr.endsWith("escape character is not valid!"))  // 由于双引号不匹配了，所以要杀死进程
                            return res;
                    }else res += token.tokenStr + "\t \t" + String.valueOf(token.tokenType) +"\t \t"+ String.valueOf(lineNum)+ '\n';

                }

            }
            ch = getNextChar();

        }

        return res;
    }

    /**
     * 状态机实现（正则表达式识别）
     * @return
     */
    public Token getToken(){
        /* token的字符索引*/
        int tokenStringIndex = ch_pos;
        /* 需要返回的当前token */
        Token currentToken ;
        /* token类型 */
        TokenType currentTokenType=TokenType.ERROR;
        /* token对应String */
        String currentTokenString = "";
        /* 状态机当前状态*/
        StateType state = StateType.START;
        String errorDescription="";
        while(state!=StateType.DONE){

            switch (state){
                case START:
                    if(ch==' ' || ch=='\t' || ch=='\n'){
                        state  = StateType.START;
                    }
                    else if(isLetter(ch)){
                        state = StateType.STATE1;
                    }
                    else if(ch=='0'){
                        state = StateType.STATE3;
                    }
                    else if(ch>='1'&&ch<='9'){
                        state = StateType.STATE4;
                    }
                    else if(ch=='"'){
                        state = StateType.STATE6;
                    }
                    break;
                case STATE1:
                    if(isLetter(ch) || isDigit(ch)){
                        state = StateType.STATE1;
                    }
                    else if(ch=='_'){
                        state = StateType.STATE2;
                    }
                    else{
                        state = StateType.DONE;
                        unGetNextChar();
                        currentTokenType = TokenType.ID;
                    }
                    break;
                case STATE2:
                    if(isLetter(ch) || isDigit(ch)){
                        state = StateType.STATE1;
                    }else{
                        state = StateType.DONE;
                        unGetNextChar();
                        currentTokenType = TokenType.ERROR;
                        errorDescription = " is not valid because there must be digit or letter after the \"_\"!";

                    }
                    break;
                case STATE3:
                    if(ch=='l' || ch=='L'){
                        state = StateType.STATE5;
                    }
                    else if(isDigit(ch)){
                        state = StateType.DONE;
                        currentTokenType = TokenType.ERROR;
                        errorDescription = " because zero can't be followed by digit";
                    }
                    else{
                        state = StateType.DONE;
                        unGetNextChar();
                        currentTokenType = TokenType.INTEGER;
                    }
                    break;
                case STATE4:
                    if(ch=='l' || ch=='L'){
                        state = StateType.STATE5;
                    }
                    else if(isDigit(ch)){
                        state = StateType.STATE4;
                    }
                    else{
                        state = StateType.DONE;
                        unGetNextChar();
                        currentTokenType = TokenType.INTEGER;
                    }
                    break;
                case STATE5:
                    state = StateType.DONE;
                    unGetNextChar();
                    currentTokenType = TokenType.INTEGER;
                    break;
                case STATE6:
                    if(ch!='\\' && ch!='\"'&&!isSpecificSymbol(ch)){
                        state = StateType.STATE6;
                    }
                    else if(ch=='\\'){
                        state = StateType.STATE7;
                    }
                    else if(ch=='\"'){
                        state = StateType.STATE8;
                    }
                    break;
                case STATE7:
                    if(ch=='b'||ch=='t'||ch=='n'||ch=='f'||ch=='r'||ch=='"'||ch=='\\'){
                        state = StateType.STATE6;
                    }
                    else{

                        state = StateType.DONE;
                        unGetNextChar();
                        currentTokenType = TokenType.ERROR;
                        errorDescription = " escape character is not valid!";

                    }
                    break;
                case STATE8:
                    state = StateType.DONE;
                    unGetNextChar();
                    currentTokenType = TokenType.STRING;
                    break;
                default: /* should never happen */
                    state = StateType.DONE;
                    currentTokenType = TokenType.ERROR;
                    break;
            }
            if(state!=StateType.DONE)
                currentTokenString+=ch;
            else{
                if(currentTokenType==TokenType.ERROR){
                    currentTokenString+=ch;
                }
                if(currentTokenType==TokenType.ID){ //当为ID类型时，进行后续检查
                    if(isReservedWord(currentTokenString)){  //检查是否为保留字
                        currentToken = new Token(currentTokenString, TokenType.RESERVEDWORD);
                        return currentToken;
                    }
                    /* State9，boolean表达式判断 */
                    if(currentTokenString.equalsIgnoreCase("false")||currentTokenString.equalsIgnoreCase("true")){
                        currentToken = new Token(currentTokenString, TokenType.BOOLEAN);
                        return currentToken;
                    }
                }
                if(currentTokenType==TokenType.INTEGER){
                    if(isLetter(inputChars[ch_pos])){ //数字后假如有letter字符，则报ID错误
                        currentTokenString = "Identifier can't start with num "+ currentTokenString;
                        currentTokenType = TokenType.ERROR;
                        currentToken = new Token(currentTokenString,currentTokenType);
                        return currentToken;
                        //System.err.println(currentTokenString);
                    }
                }
                if(currentTokenType==TokenType.ERROR){
                    currentTokenString=currentTokenString+ errorDescription;
                }
            }
            if(state!=StateType.DONE)
                ch = getNextChar();
            //System.err.println("current ch is "+ch);
        }
        currentToken = new Token(currentTokenString,currentTokenType);

        return  currentToken;
    }

    private char getNextChar(){
        if(ch_pos>=inputChars.length) return '\0';  //防止数组越界
        char nextChar = inputChars[ch_pos];
        ch_pos++;
        return nextChar;
    }

    private void unGetNextChar(){
        ch_pos--;

        return;
    }
    //判断是否是保留字
    public boolean isReservedWord(String str){
        for(int i = 0;i < reservedWords.length;i++){
            if(reservedWords[i].equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    //判断是否是字母
    public boolean isLetter(char letter){
        if((letter >= 'a' && letter <= 'z')||(letter >= 'A' && letter <= 'Z'))
            return true;
        else
            return false;
    }
    //判断是否是数字
    public boolean isDigit(char digit){
        if(digit >= '0' && digit <= '9')
            return true;
        else
            return false;
    }

    public boolean isSpecificSymbol(char symbol){
        for(int i=0;i<specificSymbol.length;i++){
            if(symbol==specificSymbol[i]){
                return true;
            }
        }
        return false;
    }



}
