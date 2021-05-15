package syntaxAnalysis;

import javax.swing.text.AsyncBoxView;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SyntaxAnalysis {
    private ArrayList<Token> tokenList = new ArrayList<>();
    private int tokenPos;
    private Token currentToken;
    private int layerNum=0;
    String tab_symbol = "\t";
    String signed_int_list[] = {"short","int16","long","int32","int64","int8"}; //只有单个关键字，多个关键字的数据类型额外处理
    String unsigned_int_list[] = {"unsigned","uint8","uint16","uint32","uint64"}; //只有单个关键字，多个关键字的数据类型额外处理
    String res = "";

    public SyntaxAnalysis(ArrayList<Token> inputTokenList){
        tokenList = inputTokenList;
        tokenPos = 0;
        currentToken = inputTokenList.get(tokenPos);
    }

    private void match(TokenType expectedTokenType) throws MatchError{
        if(currentToken.tokenType==expectedTokenType){
            tokenPos++;
            if(tokenPos!=tokenList.size()){ //当等于tokenList长度时表示已经全部匹配完毕
                currentToken = tokenList.get(tokenPos);
            }
        }else{
            System.err.println("error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" doesn't match "+expectedTokenType);
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" doesn't match "+expectedTokenType;
            throw new MatchError(errorMsg);
        }
    }

    private void match(String expectedTokenStr) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase(expectedTokenStr)){
            tokenPos++;
            if(tokenPos!=tokenList.size()){ //当等于tokenList长度时表示已经全部匹配完毕
                currentToken = tokenList.get(tokenPos);
            }

        }else{
            System.err.println("error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" is not equal to "+expectedTokenStr);
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" is not equal to "+expectedTokenStr;
            throw new MatchError(errorMsg);
        }
    }

    public SyntaxTree struct_type(SyntaxTree currentNode) throws MatchError{
        // match struct
        match("struct");
        if(currentNode==null) currentNode = new SyntaxTree(SyntaxTreeType.struct_type,tokenList.get(tokenPos-1));
        // match ID
        match(TokenType.ID);
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.ID,tokenList.get(tokenPos-1));
        // match {
        match("{");
        // match member_list

        currentNode.rightChild = new SyntaxTree(SyntaxTreeType.member_list);
        member_list(currentNode.rightChild);
        // match }
        match("}");
        return currentNode;
    }

    private void member_list(SyntaxTree currentNode) throws MatchError{
        int index = 1;
        while(!currentToken.tokenStr.equals("}")){
            if(index ==1) {
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.member,index);
                currentNode = currentNode.leftChild;
            }else{
                currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.member,index);
                currentNode = currentNode.rightSibling;
            }
            // match type_spec
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.type_spec);
            type_spec(currentNode.leftChild);
            // match declarators
            currentNode.rightChild = new SyntaxTree(SyntaxTreeType.declarators);
            declarators(currentNode.rightChild);
            // match semicolon
            match(";");
            index++;
        }
    }

    private void type_spec(SyntaxTree currentNode) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase("struct")){
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.struct_type,new Token("struct",TokenType.RESERVEDWORD));
            struct_type(currentNode.leftChild);
        }else{
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.base_type_spec);
            base_type_spec(currentNode.leftChild);
        }
    }

    private void declarators(SyntaxTree currentNode) throws MatchError{
        int index = 1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.declarator,index);
        declarator(currentNode.leftChild);
        currentNode = currentNode.leftChild;
        while(currentToken.tokenStr.equals(",")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.declarator,index);
            currentNode=currentNode.rightSibling;
            match(",");
            declarator(currentNode);
        }
    }

    private void declarator(SyntaxTree currentNode) throws MatchError{
        match(TokenType.ID);
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.ID,tokenList.get(tokenPos-1));
        if(currentToken.tokenStr.equals("[")){
            currentNode.rightChild = new SyntaxTree(SyntaxTreeType.exp_list);
            exp_list(currentNode.rightChild);
        }
    }

    private void exp_list(SyntaxTree currentNode) throws MatchError{
        int index=1;
        match("[");
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.or_expr,index);
        currentNode = currentNode.leftChild;
        or_expr(currentNode);

        while(currentToken.tokenStr.equals(",")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.or_expr,index);
            currentNode = currentNode.rightSibling;
            match(",");
            or_expr(currentNode);
        }
        match("]");
    }

    private void or_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.xor_expr,index);
        currentNode = currentNode.leftChild;
        xor_expr(currentNode);
        while(currentToken.tokenStr.equals("|")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.xor_expr,index);
            currentNode = currentNode.rightSibling;
            match("|");
            xor_expr(currentNode);
        }
    }

    private void xor_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.and_expr,index);
        currentNode = currentNode.leftChild;
        and_expr(currentNode);
        while(currentToken.tokenStr.equals("^")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.and_expr,index);
            currentNode = currentNode.rightSibling;
            match("^");
            and_expr(currentNode);
        }
    }

    private void and_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.shift_expr,index);
        currentNode = currentNode.leftChild;
        shift_expr(currentNode);
        while(currentToken.tokenStr.equals("&")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.shift_expr,index);
            currentNode = currentNode.rightSibling;
            match("&");
            shift_expr(currentNode);
        }
    }

    private void shift_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.add_expr,index);
        currentNode = currentNode.leftChild;
        add_expr(currentNode);
        while(currentToken.tokenStr.equals(">>")||currentToken.tokenStr.equals("<<")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.add_expr,index);
            currentNode = currentNode.rightSibling;
            if(currentToken.tokenStr.equals(">>")) {
                match(">>");
                currentNode.setOpk(">>");
            }
            else{
                match("<<");
                currentNode.setOpk("<<");
            }
            add_expr(currentNode);
        }
    }

    private void add_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.mult_expr,index);
        currentNode = currentNode.leftChild;
        mult_expr(currentNode);
        while(currentToken.tokenStr.equals("+")||currentToken.tokenStr.equals("-")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.mult_expr,index);
            currentNode = currentNode.rightSibling;
            if(currentToken.tokenStr.equals("+")) {
                match("+");
                currentNode.setOpk("+");
            }
            else{
                match("-");
                currentNode.setOpk("-");
            }
            mult_expr(currentNode);
        }
    }

    private void mult_expr(SyntaxTree currentNode) throws MatchError{
        int index=1;
        currentNode.leftChild = new SyntaxTree(SyntaxTreeType.unary_expr,index);
        currentNode = currentNode.leftChild;
        unary_expr(currentNode);
        while(currentToken.tokenStr.equals("*")||currentToken.tokenStr.equals("/")||currentToken.tokenStr.equals("%")){
            index++;
            currentNode.rightSibling = new SyntaxTree(SyntaxTreeType.unary_expr,index);
            currentNode = currentNode.rightSibling;
            if(currentToken.tokenStr.equals("*")) {
                match("*");
                currentNode.setOpk("*");
            }
            else if(currentToken.tokenStr.equals("/")) {
                match("/");
                currentNode.setOpk("/");
            }
            else {
                match("%");
                currentNode.setOpk("%");
            }
            unary_expr(currentNode);
        }
    }

    private void unary_expr(SyntaxTree currentNode) throws MatchError{
        String Opk = null;
        if(currentToken.tokenStr.equals("+")){
            match("+");
            Opk = "+";
        }
        else if(currentToken.tokenStr.equals("-")){
            match("-");
            Opk = "-";
        }
        else if(currentToken.tokenStr.equals("~")){
            match("~");
            Opk = "~";
        }
        if(currentToken.tokenType==TokenType.INTEGER){
            match(TokenType.INTEGER);
            if(Opk!=null){
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.INTEGER,tokenList.get(tokenPos-1),Opk);
            }else{
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.INTEGER,tokenList.get(tokenPos-1));
            }
        }
        else if(currentToken.tokenType==TokenType.STRING){
            match(TokenType.STRING);
            if(Opk!=null){
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.STRING,tokenList.get(tokenPos-1),Opk);
            }else{
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.STRING,tokenList.get(tokenPos-1));
            }
        }
        else if(currentToken.tokenType==TokenType.BOOLEAN){
            match(TokenType.BOOLEAN);
            if(Opk!=null){
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.BOOLEAN,tokenList.get(tokenPos-1),Opk);
            }else{
                currentNode.leftChild = new SyntaxTree(SyntaxTreeType.BOOLEAN,tokenList.get(tokenPos-1));
            }
        }
        else{
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" doesn't match (INTEGER|STRING|BOOLEAN) in unary_expr\n";
            System.err.println(errorMsg);
            throw new MatchError(errorMsg);
        }
    }

    private void base_type_spec(SyntaxTree currentNode) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase("char")) {
            match("char");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("boolean")) {
            match("boolean");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("float")
                ||currentToken.tokenStr.equalsIgnoreCase("double")
                ||(currentToken.tokenStr.equalsIgnoreCase("long")&&tokenList.get(tokenPos+1).tokenStr.equalsIgnoreCase("double"))
                ) {
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.floating_pt_type);
            floating_pt_type(currentNode.leftChild);
        }
        else if(is_signed_int()||is_unsigned_int()) {
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.integer_type);
            integer_type(currentNode.leftChild);
        }
        else{
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" doesn't match base_type_spec\n";
            System.err.println(errorMsg);
            throw new MatchError(errorMsg);
        }


    }

    private void floating_pt_type(SyntaxTree currentNode) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase("float")) {
            match("float");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("double")) {
            match("double");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if((currentToken.tokenStr.equalsIgnoreCase("long")&&tokenList.get(tokenPos+1).tokenStr.equalsIgnoreCase("double"))){
            match("long");
            match("double");
            currentNode.setToken(new Token("long double",TokenType.RESERVEDWORD));
        }else{
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" doesn't match floating_pt_type\n";
            System.err.println(errorMsg);
            throw new MatchError(errorMsg);
        }
    }

    private void integer_type(SyntaxTree currentNode) throws MatchError{
        if(is_unsigned_int()) {
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.unsigned_int);
            unsigned_int(currentNode.leftChild);
        }
        else{
            currentNode.leftChild = new SyntaxTree(SyntaxTreeType.signed_int);
            signed_int(currentNode.leftChild);
        }
    }

    private void unsigned_int(SyntaxTree currentNode) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase("unsigned")){
            if(tokenList.get(tokenPos+1).tokenStr.equalsIgnoreCase("short")){
                match("unsigned");
                match("short");
                currentNode.setToken(new Token("unsigned short",TokenType.RESERVEDWORD));
            }else if(tokenList.get(tokenPos+1).tokenStr.equalsIgnoreCase("long")){
                if(tokenList.get(tokenPos+2).tokenStr.equalsIgnoreCase("long")){
                    match("unsigned");
                    match("long");
                    match("long");
                    currentNode.setToken(new Token("unsigned long long",TokenType.RESERVEDWORD));
                }else{
                    match("unsigned");
                    match("long");
                    currentNode.setToken(new Token("unsigned long",TokenType.RESERVEDWORD));
                }
            }else{
                String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                        +currentToken.tokenType+" "+currentToken.tokenStr+" unsigned must be followed by short or long in unsigned_int\n";
                System.err.println(errorMsg);
                throw new MatchError(errorMsg);
            }
        }else if(currentToken.tokenStr.equalsIgnoreCase("uint8")) {
            match("uint8");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("uint16")) {
            match("uint16");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("uint32")) {
            match("uint32");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("uint64")) {
            match("uint64");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else{
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" unsigned_int match error\n";
            System.err.println(errorMsg);
            throw new MatchError(errorMsg);
        }
    }

    private void signed_int(SyntaxTree currentNode) throws MatchError{
        if(currentToken.tokenStr.equalsIgnoreCase("long")){
            if(tokenList.get(tokenPos+1).tokenStr.equalsIgnoreCase("long")){
                match("long");
                match("long");
                currentNode.setToken(new Token("long long",TokenType.RESERVEDWORD));
            }else{
                match("long");
                currentNode.setToken(tokenList.get(tokenPos-1));
            }
        }else if(currentToken.tokenStr.equalsIgnoreCase("short")) {
            match("short");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("int8")) {
            match("int8");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("int16")) {
            match("int16");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("int32")) {
            match("int32");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else if(currentToken.tokenStr.equalsIgnoreCase("int64")) {
            match("int64");
            currentNode.setToken(tokenList.get(tokenPos-1));
        }
        else{
            String errorMsg = "error happens in row "+String.valueOf(currentToken.tokenLineNum)+": "
                    +currentToken.tokenType+" "+currentToken.tokenStr+" signed_int match error\n";
            System.err.println(errorMsg);
            throw new MatchError(errorMsg);
        }
    }

    private boolean is_signed_int(){

        for(int i=0;i<signed_int_list.length;i++){
            if(currentToken.tokenStr.equalsIgnoreCase(signed_int_list[i])){
                return true;
            }
        }
        return false;
    }
    private boolean is_unsigned_int(){
        for(int i=0;i<unsigned_int_list.length;i++){
            if(currentToken.tokenStr.equalsIgnoreCase(unsigned_int_list[i])){
                return true;
            }
        }
        return false;
    }

}
