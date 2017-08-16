package test;

import utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class test {

    public static void main(String[] args) throws IOException {
        String path = "test1.txt";
//        FileHelper.saveAs("test0\ttset0\n",path,true);
//        FileHelper.saveAs("test1\ttset1\n",path,true);
//        FileHelper.saveAs("test2\ttset2\n",path,true);
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("不存在");
            file.createNewFile();
        }
        ArrayList<String> res = FileHelper.readByLinesList(path);
        for (String s : res)
            System.out.println(s);
    }
}
