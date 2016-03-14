package massiveattendancescannerapplication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import massiveattendancescannerapplication.Services.JSONParser;
import massiveattendancescannerapplication.Services.ServiceHandler;

public class SectionActivity extends ListActivity {

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> sectionList;
    JSONArray courses = null;
    String course_id, course_name;
    private static final String URL = "http://192.168.0.101:3000/sections";
    private static final String TAG_ID = "_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_SEMESTER = "semester";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_activity);

        Intent i = getIntent();
        course_id = i.getStringExtra("course_id");
        sectionList = new ArrayList<HashMap<String, String>>();
        new LoadSections().execute();
        ListView lv = getListView();

        /**
         * Listview on item click listener
         * StudentActivity will be lauched by passing album id, song id
         * */
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                // On selecting single track get song information
                Intent i = new Intent(getApplicationContext(), StudentActivity.class);

                // to get song information
                // both album id and song is needed
                String course_id = ((TextView) view.findViewById(R.id.course_id)).getText().toString();
                String section_id = ((TextView) view.findViewById(R.id.section_id)).getText().toString();

                i.putExtra("course_id", course_id);
                i.putExtra("section_id", section_id);

                startActivity(i);
            }
        });

    }

    class LoadSections extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SectionActivity.this);
            pDialog.setMessage("Cargando Secciones ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            ServiceHandler sh = new ServiceHandler();
            String JSONString = null;
            try {
                JSONString = sh.getServiceCall(URL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray sections = new JSONArray(JSONString);

                for (int i = 0; i < sections.length(); i++) {
                    JSONObject c = sections.getJSONObject(i);

                    String section_id = c.getString(TAG_ID);
                    String section_no = String.valueOf(i + 1);
                    String name = c.getString(TAG_NAME);
                    String semester = c.getString(TAG_SEMESTER);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("course_id", course_id);
                    map.put(TAG_ID, section_id);
                    map.put("section_no", section_no + ".");
                    map.put(TAG_NAME, name);
                    map.put(TAG_SEMESTER, semester);

                    // adding HashList to ArrayList
                    sectionList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all tracks
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            SectionActivity.this, sectionList,
                            R.layout.list_item_sections, new String[] { "course_id", TAG_ID, "track_no",
                            TAG_NAME, TAG_SEMESTER }, new int[] {
                            R.id.course_id, R.id.section_id, R.id.section_no, R.id.section_name, R.id.section_semester });
                    setListAdapter(adapter);
                    setTitle(course_name);
                }
            });

        }

    }
}