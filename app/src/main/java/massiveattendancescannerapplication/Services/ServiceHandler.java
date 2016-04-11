package massiveattendancescannerapplication.Services;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>
 *     This class creates a URL connection to a host, and GETs or POSTs a JSON object containing
 *     survey information.
 * </p>
 * @author Reynaldo
 */

@SuppressWarnings("ALL")
public class ServiceHandler {

    static String response;
    public ServiceHandler() {
    }
    /**
     * <p>
     *     This function receives a URL direction and a method of connection, connects to a host
     *     through the url and receives a JSON Object.
     * </p>
     * @param url url to establish a connection.
     * @return a String response containing the JSON Object.
     * @throws IOException if the InputStream is null.
     * @author Reynaldo
     */
    public String getServiceCall(String url) throws IOException {
        response = "";
        try {
            URL urlUp = new URL(url);
            URLConnection conn = urlUp.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String data = "";

            while ((data = reader.readLine()) != null) {
                response += data;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * <p>
     *     This function receives a URL direction and a JSON object, connects to a host
     *     through the url and sends the JSON Object.
     * </p>
     * @param url url to establish a connection.
     * @return a boolean in case of successful POST.
     * @throws IOException  if the InputStream is null.
     * @author Reynaldo
     */

    public boolean postServiceCall(String url, JSONObject jsonobject) throws IOException {
        int inputStream;
        response="";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);

        String json = jsonobject.toString();
        StringEntity se = new StringEntity(json);
        httpPut.setEntity(se);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        HttpResponse httpResponse = httpclient.execute(httpPut);
        inputStream = httpResponse.getStatusLine().getStatusCode();
        if (inputStream == 200){
            return true;
        }else {
            return false;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}