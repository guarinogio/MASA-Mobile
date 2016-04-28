package massiveattendancescannerapplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import massiveattendancescannerapplication.Services.FileReader;
import massiveattendancescannerapplication.Services.ServiceHandler;

public class CourseActivity extends ListActivity {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> courseList;
    JSONArray courses;
    JSONObject professor;
    String JSONString;
    private static final String COURSE = "courses";
    String URL;
    private static final String TAG_ID = "_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CODE = "code";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_activity);

        URL = FileReader.getUrl(getApplicationContext());
        courseList = new ArrayList<>();
        new LoadCourses().execute();

        ListView lv = getListView();

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                Intent i = new Intent(getApplicationContext(), SectionActivity.class);
                String course_id = ((TextView) view.findViewById(R.id.course_id)).getText().toString();
                i.putExtra("course_id", course_id);
                startActivity(i);
            }
        });
    }

    class LoadCourses extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CourseActivity.this);
            pDialog.setMessage("Cargando Materias ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {

            ServiceHandler sh = new ServiceHandler();
            try {
                JSONString = sh.getServiceCall(URL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                professor = new JSONObject(JSONString);
                if (professor.has(COURSE)) {
                    courses = professor.getJSONArray(COURSE);
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject c = courses.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String code = c.getString(TAG_CODE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_CODE, code);
                        courseList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
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