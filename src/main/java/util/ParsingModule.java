package util;

import controller.ParsingController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsingModule {

    // Opcode 추출
    public static String extractOpcode(String input) {
        int percentIdx = input.indexOf('%');
        if (percentIdx > 0) {
            return input.substring(0, percentIdx);
        }
        return null;
    }

    // DataStruct 추출
    public static ParsingController.DataStruct extractData(String input) {
        ParsingController.DataStruct data = new ParsingController.DataStruct();
        Map<String, List<String>> tempMap = new HashMap<>();

        int firstPercent = input.indexOf('%');
        
        if (firstPercent == -1) {
            return data;
        }

        // 마지막 %가 없으면 문자열 끝까지 사용
        int lastPercent = input.lastIndexOf('%');
        if (lastPercent == firstPercent) {
            // 마지막 %가 없으면 문자열 끝까지 사용
            lastPercent = input.length();
        }

        String dataSection = input.substring(firstPercent + 1, lastPercent);
        System.out.println("[DEBUG] dataSection: '" + dataSection + "'");
        String[] entries = dataSection.split("&");

        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] kv = entry.split("\\$", 2);
            if (kv.length == 2) {
                tempMap.computeIfAbsent(kv[0], k -> new ArrayList<>()).add(kv[1]);
            }
        }

        data.id = toArray(tempMap.get("id"));
        data.password = toArray(tempMap.get("password"));
        data.name = toArray(tempMap.get("name"));
        data.userAddress = toArray(tempMap.get("userAddress"));

        return data;
    }

    private static String[] toArray(List<String> list) {
        return list != null ? list.toArray(new String[0]) : null;
    }

    // Map<String, String> 형태로 데이터 추출
    public static Map<String, String> extractDataToMap(String request) {
        Map<String, String> dataMap = new HashMap<>();
        try {
            int start = request.indexOf('%', 1);
            int end = request.lastIndexOf('%');
            if (start == -1 || end == -1 || start >= end) {
                return dataMap;
            }
            String dataString = request.substring(start + 1, end);
            if (dataString.isEmpty()) {
                return dataMap;
            }
            String[] pairs = dataString.split("&");
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                String[] keyValue = pair.split("\\$", 2);
                if (keyValue.length == 2) {
                    dataMap.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("extractDataToMap error: " + e.getMessage());
        }
        return dataMap;
    }

    // 보내는 사람 userId 추출
    public static String extractSenderUserId(String input) {
        try {
            int firstPercent = input.indexOf('%');
            int secondPercent = input.indexOf('%', firstPercent + 1);
            int lastPercent = input.lastIndexOf('%');
            if (firstPercent == -1 || secondPercent == -1 || lastPercent == -1 || secondPercent >= lastPercent) {
                return null;
            }

            String dataSection = input.substring(secondPercent + 1, lastPercent);
            String[] entries = dataSection.split("&");
            for (String entry : entries) {
                if (entry.startsWith("user$")) {
                    return entry.substring("user$".length());
                }
            }
        } catch (Exception e) {
            System.err.println("extractSenderUserId error: " + e.getMessage());
        }
        return null;
    }
}
