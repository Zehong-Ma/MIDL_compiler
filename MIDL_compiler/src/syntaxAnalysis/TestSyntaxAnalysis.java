/**
 * @author mazehong
 * @version v1.0
 */
package syntaxAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;

public class TestSyntaxAnalysis {
    private static ArrayList<String> testFileList = new ArrayList<String>();
    /**
     * 读取测试文件文件夹内的文件名
     * @param file
     * @throws IOException
     */
    public static void getFileNames(File file) throws IOException {

        if(file.isFile()&&file.getName().endsWith(".txt")){
            testFileList.add(file.getAbsolutePath());
        }else if(file.isDirectory()){
            String filePath = file.getAbsolutePath();
            String[] fileList=file.list();
            for(int i=0;i<fileList.length;i++){
                File newFile = new File(filePath+"\\"+fileList[i]);
                getFileNames(newFile);
            }
        }
    }

    /**
     * 读取词法分析输出文件中的tokens
     * @param reader
     * @return
     * @throws IOException
     */
    public static ArrayList<Token> getTokens(BufferedReader reader) throws IOException{
        ArrayList<Token> tokenList = new ArrayList<>();
        String inputLine = null;
        while((inputLine=reader.readLine())!=null){
            String token_info[] = inputLine.split("\\s+");
            if(token_info.length>3){

                int right_quoto_index = inputLine.indexOf("\"",1);
                token_info[0] = inputLine.substring(0,right_quoto_index+1);
                token_info[1] = token_info[token_info.length-2];
                token_info[2] = token_info[token_info.length-1];
            }
            Token inputToken = new Token(token_info[0],TokenType.valueOf(token_info[1]),Integer.valueOf(token_info[2]));
            tokenList.add(inputToken);
        }
        return tokenList;
    }

    /**
     * 前序遍历语法树
     * @param root
     */
    public static void printSyntaxTree(SyntaxTree root,PrintWriter writer,int layerNum){
        if(root==null) return;

        String tabSymbol = "      ";
        if(root.token==null){
            if(root.Opk!=null)
                writer.println(tabSymbol.repeat(layerNum)+root.Opk+root.syntaxTreeType);
            else writer.println(tabSymbol.repeat(layerNum)+root.syntaxTreeType);
        }
        else{
            if(root.Opk!=null)
                writer.println(tabSymbol.repeat(layerNum)+root.Opk+root.syntaxTreeType+": "+root.Opk+root.token.tokenStr);
            else
                writer.println(tabSymbol.repeat(layerNum)+root.syntaxTreeType+": "+root.token.tokenStr);
        }

        printSyntaxTree(root.leftChild,writer,layerNum+1);
        printSyntaxTree(root.rightChild,writer,layerNum+1);
        printSyntaxTree(root.rightSibling,writer,layerNum);
    }

    /**
     * 结果文件生成函数
     * @param inputFile
     * @throws IOException
     */
    public static void generateOutput(File inputFile) throws IOException{
        File inputfile = inputFile;
        String fileName = inputfile.getAbsolutePath();
        String[] fileList = fileName.split("\\\\");
        fileName = "syntaxAnalysisResult_"+fileList[fileList.length-1].substring(19);
        File outfile = new File("src\\outputFile\\syntaxAnalysisOutput\\"+fileName);
        if(!outfile.isFile())
            outfile.createNewFile();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        ArrayList<Token> tokenList = getTokens(reader);

        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
        SyntaxTree root = new SyntaxTree(SyntaxTreeType.struct_type,new Token("struct",TokenType.RESERVEDWORD));
        FileOutputStream fos = new FileOutputStream(outfile);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));

        try{
            syntaxAnalysis.struct_type(root);
            printSyntaxTree(root,writer,0);
        }catch (MatchError e){
            printSyntaxTree(root,writer,0);
            writer.write(e.getMessage());
        }
        writer.close();
    }

    /**
     * 从文件中读取测试用例进行测试
     * @throws IOException
     */
    public void readFileTest() throws IOException{
        System.out.println("自动测试");
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        File inputFile = new File(absolutePath+"\\src\\outputFile\\tokenAnalysisOutput");
        TestSyntaxAnalysis.getFileNames(inputFile);

        for(int i= 0;i<testFileList.size();i++){
            File temFile = new File(testFileList.get(i));
            System.err.println(testFileList.get(i));
            TestSyntaxAnalysis.generateOutput(temFile);
            System.out.println(testFileList.get(i)+"语法分析结束");
        }
    }

    public static void main(String[] args) throws Exception {
        TestSyntaxAnalysis tester = new TestSyntaxAnalysis();
        System.out.println("文件自动读写测试开始：\n");
        Scanner sc = new Scanner(System.in);
        tester.readFileTest();
    }
}
