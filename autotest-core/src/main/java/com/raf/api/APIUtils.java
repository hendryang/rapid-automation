package com.raf.api;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class APIUtils {
    private static Gson gson;
    public HashMap<String, Supplier<String>> dataSupplier = new HashMap<>();

    public APIUtils() {
        gson = new Gson();
        dataSupplier.put("<autogendatetime>", () -> Long.toString(System.currentTimeMillis()));
//        dataSupplier.put("<autotodaysdate>", ()-> LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        dataSupplier.put("<autotomorrowsdate>", ()->LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        dataSupplier.put("autogenuuid", ()-> UUID.randomUUID().toString());
    }

    public String convertJsonResponseToString(Object response) {
        return gson.toJson(response);
    }

    public <T> T convertJsonResponseToMapCollections(String response, Type tokenType) {
        Object data = null;
//        ArrayList<Map<String, Object>> data = new ArrayList<>();
        //Type mapType = typeToken.getType();
        data = gson.fromJson(response, tokenType);
//        Object json = new JSONTokener(response).nextValue();
//        if(json instanceof JSONArray){
//            mapType = new TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
//            data = gson.fromJson(response, mapType);
//        }
//        else{
//            mapType = new TypeToken<Map<String, Object>>(){}.getType();
//            Map<String, Object> actualMap = gson.fromJson(response, mapType);
//            if(jsonRootNode.equals("")){
//                data.add(actualMap);
//            }
//            else{
//                data = (ArrayList)actualMap.get(jsonRootNode);
//            }
//        }
        return (T) data;
    }

    public void addItemInDataSupplier(String key, Supplier<String> value) {
        dataSupplier.put(key, value);
    }

    public String processData(String data) {
        for (String key : dataSupplier.keySet()) {
            data = data.replace(key, dataSupplier.get(key).get());
        }
        return data;
    }

    public String replaceData(String data, Map<String, String> dataMap) {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            data = data.replace(entry.getKey(), entry.getValue());
        }
        return data;
    }

//    public Map<String, String> verifyResponseMessageFromJsonArray(String jsonRootNode, String uniqueKey, String uniqueValue,
//                                                                  String expectedResponseBody, String actualResponseBody){
////        expectedResponseBody = processData(expectedResponseBody);
//
//        ArrayList<Map<String, Object>> data;
//        Type mapType;
//
//        Object json = new JSONTokener(actualResponseBody).nextValue();
//        if(json instanceof JSONArray){
//            mapType = new TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
//
//            data = gson.fromJson(actualResponseBody, mapType);
//        }
//        else{
//            mapType = new TypeToken<Map<String, Object>>(){}.getType();
//            Map<String, Object> actualdMap = gson.fromJson(actualResponseBody, mapType);
//            data = (ArrayList)actualdMap.get(jsonRootNode);
//        }
//
//        String jsonResponse = "";
//        for(Map<String, Object> brand: data){
//            if(brand.get(uniqueKey).equals(uniqueValue)){
//                jsonResponse = gson.toJson(brand);;
//                break;
//            }
//        }
//
//        return verifyResponseMessage(expectedResponseBody, jsonResponse);
//    }

    public String compareExpectedAndActualResponse(String expectedResponseBody, String actualResponseBody) {
//        expectedResponseBody = processData(expectedResponseBody);
        Object json = new JSONTokener(expectedResponseBody).nextValue();
        Type mapType;
        if (json instanceof JSONArray) {
            mapType = new TypeToken<ArrayList<Map<String, Object>>>() {
            }.getType();
        } else {
            mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
        }
        Map<String, Object> expectedMap = convertJsonResponseToMapCollections(expectedResponseBody, mapType);
        Map<String, Object> actualdMap = convertJsonResponseToMapCollections(actualResponseBody, mapType);

        MapDifference<String, Object> diffMap = Maps.difference(expectedMap, actualdMap);
//        Map<String, String> newDiffMap = new HashMap<>();
        String diffMsg = "";
        if (!diffMap.areEqual()) {
            Map<String, MapDifference.ValueDifference<Object>> tempMap = diffMap.entriesDiffering();
            Map<String, Object> onlyLeft = new HashMap<>(diffMap.entriesOnlyOnLeft());
            Map<String, Object> onlyRight = new HashMap<>(diffMap.entriesOnlyOnRight());
            onlyLeft.entrySet().removeAll(onlyRight.entrySet());
            if (onlyLeft.size() != 0) {
                diffMsg += "Unable to find keys: " + onlyLeft.keySet() +
                        " from actual response!\n";
            }

            for (Map.Entry entry : tempMap.entrySet()) {
                if (!entry.getValue().toString().contains("SKIPCHECK")) {
                    diffMsg += "- Key [" + entry.getKey() + "] contains different value(actual, expected): " + entry.getValue() + "\n";
//                    newDiffMap.put(key, tempMap.get(key).toString());
                }
            }
        }

        return diffMsg;
    }
}
