package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadWriteInfo {
    public static Map<String, String> readInfoFromFile(String path) {
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
            if (words.length > 1)
                map.put(words[0], words[1]);
        }
        return map;
    }

    public static void writeInfoToFile(String path, String first, String second) {
        FileHelper.saveAs(first + "\t" + second + "\n", path, true);
    }


    public static Map<String, String> readUserInfoFromFile(String path) {
        return readInfoFromFile(path);
    }

    public static void writeUserInfoToFile(String path, String name, String passwd) {
        writeInfoToFile(path, name, passwd);
    }

    public static Map<String, String> readRoomInfoFromFile(String path) {
        return readInfoFromFile(path);
    }

    public static void writeRoomInfoToFile(String path, String name, String info) {
        writeInfoToFile(path, name, info);
    }

    public static String readMsgInfoFromFile(String path, String roomName) {
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
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            String[] words = line.split("\\|s\\|e\\|g\\|");
            if (words.length > 1 && words[0].equals(roomName)) {
                sb.append(words[1]).append("\n");
            }
        }
        return sb.toString();
    }

    public static void writeMsgInfoToFile(String path, String roomName, String msg) {
        FileHelper.saveAs(roomName + "|s|e|g|" + msg + "\n", path, true);
    }
}
