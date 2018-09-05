package com.usu.tinyservice.network.parsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@SuppressWarnings("rawtypes")
public class WordDataParser implements IDataParser {
	@Override
    public Class getDataClass() {
        return String.class;
    }

    @Override
    public Object loadObject(String path) throws Exception {
        String url = "http://129.123.7.172:3883/sm/html/b8.html";
        return getTextFromHttps(url);
    }

    @Override
    public Object parseBytesToObject(byte[] byteData) throws Exception {
    	return new String(byteData);
    }

    @Override
    public byte[] parseObjectToBytes(Object objData) throws Exception {
    	if (objData instanceof String) {
    		return ((String) objData).getBytes();
    	} else if (objData instanceof TopWords) {
    		return ((TopWords) objData).words.toString().getBytes();
    	}
    	return new byte[0];
    }

    @Override
    public byte[] getPartFromObject(Object data, int firstOffset, int lastOffset) {
        String dataStr = (String) data;
        int dataLen = dataStr.length();
        double firstIdx = dataLen * ((double) firstOffset / 100);
        double lastIdx = dataLen * ((double) lastOffset / 100);
        if (firstIdx < 0) firstIdx = 0;
        if (lastIdx >= dataLen) lastIdx = dataLen - 1;
        String subData = dataStr.substring((int) firstIdx, (int) lastIdx);
        return subData.getBytes();

    }

    @Override
    public Object createPlaceHolder(Object dataObject) {
        return new TopWords();
    }

    @Override
    public Object copyPartToHolder(Object placeholderObj, byte[] partObj, int firstOffset, int lastOffset) {
        String listWords = new String(partObj);
        TopWords topWords = (TopWords) placeholderObj;
        try {
        	JsonObject jsonWords = new JsonObject();
            
        	Set<String> wordKeys = jsonWords.keySet();
            String key = "";
            int count = 0;
            while (wordKeys.hasNext()) {
                key = wordKeys.next();
                if (topWords.words.containsKey(key)) {
                    count = topWords.words.get(key) + jsonWords.getInt(key);
                } else {
                    count = jsonWords.getInt(key);
                }
                topWords.words.put(key, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return
        return topWords;
    }

    public class TopWords {
        public HashMap<String, Integer> words = new HashMap<>();
    }

    private String getTextFromHttps(String url) {
        try {
            // open http connection
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            // get the page's data
            StringBuffer buffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null){
                buffer.append(line);
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
