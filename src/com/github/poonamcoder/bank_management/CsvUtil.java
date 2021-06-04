package com.github.poonamcoder.bank_management;

import java.io.*;
import java.util.*;

public class CsvUtil {

    public static List<LinkedHashMap<String, String>> read(String fileName) {
        List<LinkedHashMap<String, String>> csvData = new ArrayList<>();
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader(fileName));
            String row;
            String[] headers = csvReader.readLine().replaceAll("\"","").split(",");
            while ((row = csvReader.readLine()) != null) {
                String[] rowData = row.replaceAll("\"","").split(",");
                LinkedHashMap<String, String> csvRow = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    csvRow.put(headers[i].trim(), rowData[i].trim());
                }
                csvData.add(csvRow);
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return csvData;
    }

    public static void write(String fileName, String data, boolean append) {
        fileName = System.getProperty("user.dir") + File.separator + fileName;
        File directory = new File(fileName.substring(0, fileName.lastIndexOf(File.separator)));
        if (!directory.exists())
            directory.mkdirs();

        FileWriter fw;
        try {
            fw = new FileWriter(fileName, append);
            fw.append(data);
            fw.append("\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String fileName, List<LinkedHashMap<String, String>> csvData, boolean append) {
        if (csvData == null || csvData.size() == 0)
            return;
        Set<String> headers;
        if (!append || read(fileName).isEmpty()) {
            headers = csvData.get(0).keySet();
            write(fileName, String.join(",", headers), false);
        }
        for (LinkedHashMap<String, String> csvRow : csvData) {
            write(fileName, String.join(",", csvRow.values()), true);
        }
    }
}
