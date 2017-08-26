package com.pixelogical.picolink;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Connection {
    String result = "";
    static Boolean skip = false;

    public String connect(String input) {
        Log.i("SSSSSSSSSSSSSS", "runnnnniiiinggg");
        try {
            String method = "GET";
            URL url = new URL(input);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setAllowUserInteraction(false);
//			connection.setRequestProperty("User-Agent",
//					"Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod(method);
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();
            int resCode = connection.getResponseCode();
            if (resCode == 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    if (skip)
                        break;
                    else
                        result += str;
                }
                skip = false;
                in.close();
                Log.i("CONNECTION", "Message:" + result);
                connection.disconnect();
                return result;
            } else {
                Log.e("CONNECTION", "Message: DC" + resCode);
                return "disconnect";
            }
        } catch (Exception e) {
            Log.e("CONNECTION", "Exception : " + e.getMessage());
            return e.getMessage();
        }
    }
}
