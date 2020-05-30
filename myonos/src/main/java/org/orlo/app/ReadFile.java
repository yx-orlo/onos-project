package org.orlo.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * 读取json文件，测试中不会使用，可以忽略.
 */
public final class ReadFile {
//"63to18":{"13-14":2,"23-13":2,"33-23":2,"43-33":2,"53-43":2,"63-53":2}
    private ReadFile() {
    }
    /*public static JsonObject parsePath(int flowTableNum) {
        String filePath = "/home/sdn/Delay_infoJ.txt";
        String jsonContent = new FileUtil().readFile(filePath);
        JsonNode flowTableArray = null;
        try {
            flowTableArray = new ObjectMapper().readTree(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode aFrameDict = flowTableArray.get(flowTableNum);
        Iterator<String> stringIterator = aFrameDict.fieldNames();
        JsonObject recordAll = new JsonObject();
        String s1, s2;
        int t;
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            JsonNode item = aFrameDict.get(key);
            String out = "";
            Iterator<String> stringIterator1 = item.fieldNames();
            while (stringIterator1.hasNext()) {
                String ikey = stringIterator1.next();
                s1 = ikey.split("-")[0];
                s2 = ikey.split("-")[1];
                t = item.get(ikey).asInt();
                out += s1 + ":" + t + "--";
            }
            recordAll.set(key, out);
        }
        return recordAll;
    }*/

    public static class FileUtil {

        public FileUtil() {
        }

        public String readFile(String path) {
            BufferedReader reader = null;
            String laststr = "";
            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                reader = new BufferedReader(inputStreamReader);
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    laststr += tempString;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return laststr;
        }
    }
}