package net.rcsms.rcsmsapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * HTTP請求與回應公用程式
 * 
 * @author macdidi
 */
public class HttpClientUtil {

	// 網頁應用程式伺服器IP位址、埠號、應用程式主目錄與路徑
    public static String SERVER_HOST = "192.168.1.17";
    public static String SERVER_PORT = "8080";
    public static String SERVER_CONTEXT = "RCSMS_Web";
    public static String MOBILE_SERVICE = "mobile";

    // 網頁應用程式伺服器URL
    public static String SERVER_URL = "http://" + SERVER_HOST + ":"
            + SERVER_PORT + "/" + SERVER_CONTEXT + "/";
    // 網頁應用程式伺服器行動裝置服務URL
    public static String MOBILE_URL = SERVER_URL + MOBILE_SERVICE + "/";

    /**
     * 設定伺服器IP位址與埠號
     *
     * @param host 伺服器IP位址
     * @param port 伺服器埠號
     */
    public static void setMobile_URL(String host, String port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
        SERVER_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT + "/"
                + SERVER_CONTEXT + "/";
        MOBILE_URL = SERVER_URL + MOBILE_SERVICE + "/";
    }


    /**
     * 設定Preference儲存的伺服器IP位址與埠號
     *
     * @param context Android Context物件
     */
    public static void setMobile_URL(Context context) {
    	// 讀取Preference儲存的伺服器IP位址與埠號
        SERVER_HOST = TurtleUtil.getServerIP(context);
        SERVER_PORT = TurtleUtil.getServerPort(context);

        setMobile_URL(SERVER_HOST, SERVER_PORT);
    }

    /**
     * 傳送HTTP POST請求
     *
     * @param url 傳送請求的URL
     * @return HTTP回應的內容
     */
    public static String sendPost(String url) {
        return getResult(url, "POST");
    }

    /**
     * 傳送HTTP POST請求
     *
     * @param url 傳送請求的URL
     * @param url 傳送請求的資料
     * @return HTTP回應的內容
     */
    public static String sendPost(String url, Map<String, String> parameters) {
        String result = null;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder();

            Set<String> keys = parameters.keySet();

            for (String key : keys) {
                builder.appendQueryParameter(key, parameters.get(key));
            }

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                result = getStringFromInputStream(conn.getInputStream());
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    /**
     * 傳送HTTP GET請求
     *
     * @param url 傳送請求的URL
     * @return HTTP回應的內容
     */
    public static String sendGet(String url) {
        return getResult(url, "GET");
    }

    // 傳送請求後，讀取伺服器回應的資料
    private static String getResult(String url, String method) {
        String result = null;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) (new URL(url).openConnection());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            StringBuffer sb = new StringBuffer();

            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
            
//            conn.setRequestMethod(method);
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.connect();
//
//            int responseCode = conn.getResponseCode();
//
//            if (responseCode == 200) {
//                result = getStringFromInputStream(conn.getInputStream());
//            }
        }
        catch (MalformedURLException e) {
            Log.d("getResult", e.toString());
        }
        catch (IOException e) {
            Log.d("getResult", e.toString());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len = -1;

        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        is.close();
        String state = os.toString();
        os.close();

        return state;
    }

    /**
     * 傳送HTTP GET請求，取得位元型態的回傳資料
     *
     * @param url 傳送請求的URL
     * @return HTTP回應的內容
     */
    public static byte[] sendGetForByte(String url) {
        byte[] result = null;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len = -1;

                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }

                result = os.toByteArray();

                is.close();
            }

        }
        catch (MalformedURLException e) {
            Log.d("getResult", e.toString());
        }
        catch (IOException e) {
            Log.d("getResult", e.toString());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

//    /**
//     * 傳送HTTP POST請求
//     *
//     * @param url 傳送請求的URL
//     * @return HTTP回應的內容
//     */
//    public static String sendPost(String url) {
//        return getResult(new HttpPost(url));
//    }
//
//    /**
//     * 傳送HTTP POST請求
//     *
//     * @param url 傳送請求的URL
//     * @param url 傳送請求的資料
//     * @return HTTP回應的內容
//     */
//    public static String sendPost(String url, List<NameValuePair> parameters) {
//        UrlEncodedFormEntity entity = null;
//
//        try {
//        	// 建立包裝請求資料的Entity物件，指定資料編碼為UTF8
//            entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
//        }
//        catch (UnsupportedEncodingException e) {
//            return null;
//        }
//
//        // 建立HTTP POST請求物件
//        HttpPost request = new HttpPost(url);
//        request.setEntity(entity);
//
//        return getResult(request);
//    }
//
//    /**
//     * 傳送HTTP GET請求
//     *
//     * @param url 傳送請求的URL
//     * @return HTTP回應的內容
//     */
//    public static String sendGet(String url) {
//        return getResult(new HttpGet(url));
//    }
//
//    // 傳送請求後，讀取伺服器回應的資料
//    private static String getResult(HttpRequestBase base) {
//        String result = null;
//
//        try {
//            HttpParams httpParameters = new BasicHttpParams();
//            int timeoutConnection = 3000;
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//            int timeoutSocket = 3000;
//            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
//
//            // 執行請求與取得回應物件
//            HttpResponse response = httpClient.execute(base);
//
//            // 判斷HTTP請求是否成功
//            if (response.getStatusLine().getStatusCode() == 200) {
//            	// 讀取HTTP回應的內容
//                result = EntityUtils.toString(response.getEntity());
//            }
//        }
//        catch (ConnectTimeoutException cte) {
//        	result = "Network Exception: " + cte;
//        }
//        catch (ClientProtocolException cpe) {
//            result = "Network Exception: " + cpe;
//        }
//        catch (IOException ioe) {
//            result = "Network Exception: " + ioe;
//        }
//
//        return result;
//    }
//
//    /**
//     * 傳送HTTP GET請求，取得位元型態的回傳資料
//     *
//     * @param url 傳送請求的URL
//     * @return HTTP回應的內容
//     */
//    public static byte[] sendGetForByte(String url) {
//        return getByteResult(new HttpGet(url));
//    }
//
//    // 傳送請求後，讀取伺服器回應的位元型態資料
//    private static byte[] getByteResult(HttpRequestBase base) {
//        byte[] result = null;
//
//        try {
//            HttpResponse response = new DefaultHttpClient().execute(base);
//
//            if (response.getStatusLine().getStatusCode() == 200) {
//                result = EntityUtils.toByteArray(response.getEntity());
//            }
//        }
//        catch (ClientProtocolException cpe) {
//
//        }
//        catch (IOException ioe) {
//
//        }
//
//        return result;
//    }

}
