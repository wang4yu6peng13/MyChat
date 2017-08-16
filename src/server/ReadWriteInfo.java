package server;

import utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadWriteInfo {
    public static Map<String, String> readUserInfoFromFile(String path) {
        Map<String, String> map = new HashMap<>();
        File file = new File(path);
        if (!file.exists()) {
            //System.out.println("不存在");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        ArrayList<String> lines = FileHelper.readByLinesList(path);
        for (String line : lines) {
            String[] words = line.split("\\s+");
            map.put(words[0], words[1]);
        }
        return map;
    }

    public static void writeUserInfoToFile(String path, String name, String passwd) {
        FileHelper.saveAs(name + "\t" + passwd + "\n", path, true);
    }

    public static Map<String, String> readRoomInfoFromFile(String path) {
        Map<String, String> map = new HashMap<>();
        File file = new File(path);
        if (!file.exists()) {
            //System.out.println("不存在");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        ArrayList<String> lines = FileHelper.readByLinesList(path);
        for (String line : lines) {
            String[] words = line.split("\\s+");
            map.put(words[0], words[1]);
        }
        return map;
    }

    public static void writeRoomInfoToFile(String path, String name, String info) {
        FileHelper.saveAs(name + "\t" + info + "\n", path, true);
    }
}
