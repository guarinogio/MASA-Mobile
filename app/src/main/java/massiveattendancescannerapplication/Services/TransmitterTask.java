package massiveattendancescannerapplication.Services;

import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Handler;

/**
 * <p>
 *     This is an AsyncTask that handles the construction of the Answer object and the AnswerArray
 *     which is the JSON object containing a JSON array of JSON objects. After the final JSON object
 *     is created this function invokes the POST method to send the information o the corresponding
 *     service.
 * </p>
 *
 * @author Reynaldo
 */

public class TransmitterTask extends AsyncTask<JSONObject, Void, Void> {

    String URL;
    boolean result;

    final  Handler myHandler = new Handler();
    public TransmitterTask(String URL) {
        this.URL = URL;
    }

    protected Void doInBackground(JSONObject... professor) {

        while (true){
            ServiceHandler pt = new ServiceHandler();
            try{
                result = pt.postServiceCall(URL,professor[0]);
                if(result){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
