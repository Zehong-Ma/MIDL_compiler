package syntaxAnalysis;

public class MatchError extends Exception{
    public MatchError() {
        super();
    }
    public MatchError(String str) {
        super(str);
    }
}
