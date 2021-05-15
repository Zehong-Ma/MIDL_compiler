package syntaxAnalysis;

public class SyntaxTree {
    public SyntaxTreeType syntaxTreeType;
    public SyntaxTree rightSibling=null;
    public SyntaxTree leftChild=null;
    public SyntaxTree rightChild=null;
    public Token token=null;
    public int index=-1;
    public String Opk = null;

    public SyntaxTree(SyntaxTreeType treeType){
        syntaxTreeType = treeType;
    }

    public SyntaxTree(SyntaxTreeType treeType,int index){
        this.syntaxTreeType = treeType;
        this.index = index;
    }

    public SyntaxTree(SyntaxTreeType treeType,Token inputToken){
        syntaxTreeType = treeType;
        token = inputToken;
    }

    public SyntaxTree(SyntaxTreeType treeType,Token inputToken,String opk){
        syntaxTreeType = treeType;
        token = inputToken;
        Opk = opk;
    }

    public void setLeftChild(SyntaxTree leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(SyntaxTree rightChild) {
        this.rightChild = rightChild;
    }

    public void setRightSibling(SyntaxTree rightSibling) {
        this.rightSibling = rightSibling;
    }

    public void setSyntaxTreeType(SyntaxTreeType syntaxTreeType) {
        this.syntaxTreeType = syntaxTreeType;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setOpk(String opk) {
        Opk = opk;
    }
}
