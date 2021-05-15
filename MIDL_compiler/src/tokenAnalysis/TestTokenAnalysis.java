/**
 * @author mazehong
 * @version v1.0
 */
package tokenAnalysis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestTokenAnalysis {

    private static ArrayList<String> testFileList = new ArrayList<String>();
    private static String allTokens = "";

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
     * 结果文件生成函数
     * @param inputFile
     * @throws IOException
     */
    public static void generateOutput(File inputFile) throws IOException{
        File inputfile = inputFile;
        String fileName = inputfile.getAbsolutePath();
        String[] fileList = fileName.split("\\\\");
        fileName = "TokenAnalysisResult_"+fileList[fileList.length-2]+"_"+fileList[fileList.length-1];
        File outfile = new File("src\\outputFile\\tokenAnalysisOutput\\"+fileName);
        if(!outfile.isFile())
            outfile.createNewFile();
        FileReader reader = new FileReader(inputfile);
        FileWriter writer = new FileWriter(outfile);
        int length = (int) inputfile.length();
        char buf[] = new char[length+1];
        reader.read(buf);
        reader.close();
        String res = "";

        Scan tokenScan = new Scan(buf);
        res = tokenScan.wordAnalyze();
        allTokens = allTokens + res + "\n";
        writer.write(res);
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
        File inputFile = new File(absolutePath+"\\src\\inputFile\\语法测试文件");
        TestTokenAnalysis.getFileNames(inputFile);

        for(int i= 0;i<testFileList.size();i++){
            File temFile = new File(testFileList.get(i));
            TestTokenAnalysis.generateOutput(temFile);
            System.out.println(testFileList.get(i)+"词法分析结束");
        }
//        File outfile = new File("src\\outputFile\\tokenAnalysisOutput\\"+"tokenOut.txt");
//        if(!outfile.isFile())
//            outfile.createNewFile();
//        FileWriter writer = new FileWriter(outfile);
//        writer.write(allTokens);
//        writer.close();
    }

    /**
     * 命令行输入测试
     * @throws IOException
     */
    public void commandInputTest() throws IOException{
        System.out.println("控制台测试");

        // //测试输入输出
        Scanner sc = new Scanner(System.in);
        String input;
        while(true){
            String res="";
            input=sc.nextLine();
            int length2 = input.length();
            char[] testStr = new char[length2+1];
            char[] inputChar=input.toCharArray();
            if(testStr.equals("quit"))  break;
            for(int i=0;i<input.length();i++){
                testStr[i]=inputChar[i];
            }
            testStr[length2] = '\0';
            Scan commandScan = new Scan(testStr);
            res = commandScan.wordAnalyze();
            System.out.println(res);
        }
        sc.close();

    }
    public static void main(String[] args) throws Exception {
        TestTokenAnalysis tester = new TestTokenAnalysis();

        System.out.println("文件自动读写测试：\n");
        tester.readFileTest();

    }
}
