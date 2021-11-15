package com.ga_gcs.logintest.retrofit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RestServiceTestHelper {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Object object, String filePath) throws Exception {
        final InputStream stream = object.getClass().getClassLoader().getResourceAsStream(filePath);

        String ret = convertStreamToString(stream);
        //Make sure you close all streams.
        stream.close();
        return ret;
    }
}
