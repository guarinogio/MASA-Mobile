package com.example.masa.massiveattendancescannerapplication;


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

import com.example.masa.massiveattendancescannerapplication.Services.JSONParser;
import com.example.masa.massiveattendancescannerapplication.Services.ServiceHandler;

public class CourseActivity extends ListActivity {

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> courseList;
    JSONArray course = null;
    private static final String URL = "http://192.168.0.101:3000/courses";
    // ALL JSON node names
    private static final String TAG_ID = "_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CODE = "code";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_activity);


        courseList = new ArrayList<HashMap<String, String>>();
        new LoadCourses().execute();

        ListView lv = getListView();

        /**
         * Listview item click listener
         * SectionActivity will be lauched by passing album id
         * */
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                // on selecting a single album
                // SectionActivity will be launched to show tracks inside the album
                Intent i = new Intent(getApplicationContext(), SectionActivity.class);

                // send album id to tracklist activity to get list of songs under that album
                String course_id = ((TextView) view.findViewById(R.id.course_id)).getText().toString();
                i.putExtra("course_id", course_id);

                startActivity(i);
            }
        });
    }

    /**
     * Background Async Task to Load all Courses by making http request
     * */
    class LoadCourses extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CourseActivity.this);
            pDialog.setMessage("Cargando Materias ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Courses JSON
         * */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            ServiceHandler sh = new ServiceHandler();
            String JSONString = null;
            try {
                JSONString = sh.getServiceCall(URL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                course = new JSONArray(JSONString);

                // looping through All courses
                for (int i = 0; i < course.length(); i++) {
                    JSONObject c = course.getJSONObject(i);

                    // Storing each json item values in variable
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String code = c.getString(TAG_CODE);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, name);
                    map.put(TAG_CODE, code);

                    // adding HashList to ArrayList
                    courseList.add(map);
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
            // dismiss the dialog after getting all courses
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            CourseActivity.this, courseList,
                            R.layout.list_item_courses, new String[] { TAG_ID,
                            TAG_NAME, TAG_CODE }, new int[] {
                            R.id.course_id, R.id.course_name, R.id.course_code });
                    setListAdapter(adapter);
                }
            });
        }
    }
}