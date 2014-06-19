package kr.betterfuture.betternews.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kr.betterfuture.betternews.MainActivity;

/**
 * Created by Edward on 2014-06-19.
 */
public abstract class NetHelper {
    static DefaultHttpClient _client;

    private static DefaultHttpClient getHttpClientInstance() {
        if (_client == null) {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            BasicHttpParams params = new BasicHttpParams();
            ConnManagerParams.setMaxTotalConnections(params, 100);
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {

                @Override

                public int getMaxForRoute(HttpRoute route) {
                    return 35;
                }

            });
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setUseExpectContinue(params, true);

            ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, registry);
            _client = new DefaultHttpClient(connManager, params);
        }


        return _client;
    }

    public static String SendRESTRequest(String url) {
        return SendRESTRequest(url, false);
    }

    public static String SendRESTRequest(String url, boolean isPost) {
        String r = "";
        StringBuilder builder = new StringBuilder();
        HttpClient client = getHttpClientInstance();

        try {
            HttpResponse response;

            if (isPost) {
                HttpPost httpPost = new HttpPost(url);
                /*AuthHelper.getAuthorizationHeader(httpPost);

                for (Header i : httpPost.getHeaders("Content-Type")) {
                    httpPost.removeHeader(i);
                }*/

                response = client.execute(httpPost);
            } else {
                HttpGet httpGet = new HttpGet(url);
                /*AuthHelper.getAuthorizationHeader(httpGet);

                for (Header i : httpGet.getHeaders("Content-Type")) {
                    httpGet.removeHeader(i);
                }*/

                response = client.execute(httpGet);
            }

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                return builder.toString();
            } else {
                Log.e(MainActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }
}
