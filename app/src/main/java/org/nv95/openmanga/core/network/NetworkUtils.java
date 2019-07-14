package org.nv95.openmanga.core.network;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nv95.openmanga.BuildConfig;
import org.nv95.openmanga.core.sources.ConnectionSource;
import org.nv95.openmanga.di.KoinJavaComponent;
import org.nv95.openmanga.items.RESTResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import timber.log.Timber;

/**
 * Created by nv95 on 29.11.16.
 */

public class NetworkUtils {

    public static final String TAG = "NetworkUtils";
    public static final String TAG_REQUEST = TAG + "-request";
    public static final String TAG_RESPONSE = TAG + "-response";
    public static final String TAG_ERROR = TAG + "-error";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    public static boolean setUseTor(Context context, boolean enabled) {
        boolean isTor = NetCipher.getProxy() == NetCipher.ORBOT_HTTP_PROXY;
        if (isTor == enabled) {
            return isTor;
        }
        if (enabled) {
            if (OrbotHelper.get(context).init()) {
                NetCipher.useTor();
                return true;
            } else {
                return false;
            }
        } else {
            NetCipher.clearProxy();
            return false;
        }
    }

    public static Document httpGet(@NonNull String url, @Nullable String cookie) throws IOException {
        InputStream is = null;
        try {
            requestLog(url, cookie);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            //con.setDoOutput(true);
            if (!TextUtils.isEmpty(cookie)) {
                con.setRequestProperty("Cookie", cookie);
            }
            con.setConnectTimeout(15000);
            is = con.getInputStream();
            return parseHtml(url, is, con);
        } catch (Exception error) {
            Timber.tag(TAG_ERROR).e(error);
            throw error;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static Document httpPost(@NonNull String url, @Nullable String cookie, @Nullable String[] data) throws IOException {
        InputStream is = null;
        try {
            requestLog(url, cookie);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            if (!TextUtils.isEmpty(cookie)) {
                con.setRequestProperty("Cookie", cookie);
            }
            if (data != null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());

                out.writeBytes(makeQuery(data));
                out.flush();
                out.close();
            }
            is = con.getInputStream();
            return parseHtml(url, is, con);
        } catch (Exception error) {
            Timber.tag(TAG_ERROR).e(error);
            throw error;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @NonNull
    public static String getRaw(@NonNull String url, @Nullable String cookie) throws IOException {
        BufferedReader reader = null;
        try {
            requestLog(url, cookie);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            if (!TextUtils.isEmpty(cookie)) {
                con.setRequestProperty("Cookie", cookie);
            }
            con.setConnectTimeout(15000);
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            String string = out.toString();
            Timber.tag(TAG_RESPONSE).d(string);
            return string;
        } catch (Exception error) {
            Timber.tag(TAG_ERROR).e(error);
            throw error;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @NonNull
    public static String postRaw(@NonNull String url, @Nullable String cookie, @Nullable String body) throws IOException {
        BufferedReader reader = null;
        try {
            requestLog(url, cookie);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            if (!TextUtils.isEmpty(cookie)) {
                con.setRequestProperty("Cookie", cookie);
            }
            if (body != null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());

                out.writeBytes(body);
                out.flush();
                out.close();
            }
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            String string = out.toString();
            Timber.tag(TAG_RESPONSE).d(string);
            return string;
        } catch (Exception error) {
            Timber.tag(TAG_ERROR).e(error);
            throw error;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    public static JSONObject getJsonObject(@NonNull String url) throws IOException, JSONException {
        return new JSONObject(getRaw(url, null));
    }

    @Nullable
    public static CookieParser authorize(String url, String... data) {
        DataOutputStream out = null;
        try {
            requestLog(url, null);
            HttpURLConnection con = NetCipher.getHttpURLConnection(url);
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(true);
            out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(makeQuery(data));
            out.flush();
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return new CookieParser(con.getHeaderFields().get("Set-Cookie"));
            } else {
                return null;
            }
        } catch (Exception e) {
            Timber.tag(TAG_ERROR).e(e);
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Timber.tag(TAG_ERROR).e(e);
            }
        }
    }

    public static RESTResponse restQuery(String url, @Nullable String token, String method, String... data) {
        BufferedReader reader = null;
        try {
            HttpURLConnection con = NetCipher.getHttpURLConnection(
                    HTTP_GET.equals(method) ? url + "?" + makeQuery(data) : url
            );
            if (con instanceof HttpsURLConnection) {
                ((HttpsURLConnection) con).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            if (!TextUtils.isEmpty(token)) {
                con.setRequestProperty("X-AuthToken", token);
            }
            con.setConnectTimeout(15000);
            con.setRequestMethod(method);
            if (!HTTP_GET.equals(method)) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(NetworkUtils.makeQuery(data));
                out.flush();
                out.close();
            }
            int respCode = con.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(isOk(respCode) ? con.getInputStream() : con.getErrorStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            String json = out.toString();
            Timber.tag(TAG_RESPONSE).d(json);
            return new RESTResponse(new JSONObject(json), respCode);
        } catch (Exception e) {
            Timber.tag(TAG_ERROR).e(e);
            return RESTResponse.fromThrowable(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    private static String makeQuery(@NonNull String[] data) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < data.length; i = i + 2) {
            query.append(URLEncoder.encode(data[i], "UTF-8")).append("=").append(URLEncoder.encode(data[i + 1], "UTF-8")).append("&");
        }
        if (query.length() > 1) {
            query.deleteCharAt(query.length()-1);
        }
        String queryString = query.toString();
        Timber.tag(TAG_REQUEST).d(queryString);
        return queryString;
    }

    /**
     * Use {@link ConnectionSource#isConnectionAvailable()}
     */
    @Deprecated
    public static boolean checkConnection(Context context) {
        return KoinJavaComponent.get(ConnectionSource.class).isConnectionAvailable();
    }

    private static boolean isOk(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    /**
     * Use {@link ConnectionSource#isConnectionAvailable(boolean)}
     */
    @Deprecated
    public static boolean checkConnection(Context context, boolean onlyWiFi) {
        return KoinJavaComponent.get(ConnectionSource.class).isConnectionAvailable(onlyWiFi);
    }

    private static void requestLog(String url, String cookie) {
        Timber.tag(TAG_REQUEST).d("request: %s", url);
        Timber.tag(TAG_REQUEST).d("cookie: %s", cookie);
    }

    private static Document parseHtml(@NonNull final String url, final InputStream is,
            final HttpURLConnection con) throws IOException {
        Document document = Jsoup.parse(is, con.getContentEncoding(), url);
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG_RESPONSE).d(document.html());
        }
        return document;
    }

}
