import syntaxAnalysis.TestSyntaxAnalysis;
import tokenAnalysis.TestTokenAnalysis;

import java.io.IOException;

public class CompilerTest {
    public static void main(String[] args) throws IOException {
        TestTokenAnalysis tokenAnalysisTester = new TestTokenAnalysis();
        TestSyntaxAnalysis syntaxAnalysisTester = new TestSyntaxAnalysis();
        tokenAnalysisTester.readFileTest();
        syntaxAnalysisTester.readFileTest();
    }
}
