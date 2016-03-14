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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import massiveattendancescannerapplication.Services.ServiceHandler;


public class StudentActivity extends ListActivity {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> studentList;
    private static final String URL = "http://192.168.0.101:3000/sections";
    private static final String TAG_ID = "_id";
    private static final String TAG_NO = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_STUDENTS = "students";
    JSONArray students = null;
    String section_id = null;
    String section_name = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity);
        Intent i = getIntent();
        section_id = i.getStringExtra("section_id");
        studentList = new ArrayList<>();
        new LoadStudents().execute();
        ListView lv = getListView();
    }

    class LoadStudents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StudentActivity.this);
            pDialog.setMessage("Cargando Alumnos ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            Bundle b = getIntent().getExtras();
            String section_id = b.getString("section_id");
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
                    String section = c.getString(TAG_ID);
                    assert section != null;
                    if (section.equals(section_id)){
                        section_name = c.getString(TAG_NAME);
                        students = c.getJSONArray(TAG_STUDENTS);
                        for (int x = 0; x < students.length(); x++) {
                            JSONObject s = students.getJSONObject(x);
                            String student_id = String.valueOf(x + 1);
                            String student_no = s.getString(TAG_NO);
                            String name = s.getString(TAG_NAME);
                            String lastname = s.getString(TAG_LASTNAME);

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("student_id", student_id);
                            map.put(TAG_ID, student_id);
                            map.put("student_no", student_no);
                            map.put(TAG_NAME, name);
                            map.put(TAG_LASTNAME, lastname);

                            studentList.add(map);
                        }
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
                            StudentActivity.this, studentList,
                            R.layout.list_item_students, new String[] { "section_id", TAG_ID, TAG_NO,
                            TAG_NAME, TAG_LASTNAME }, new int[] {
                            R.id.section_id, R.id.student_id, R.id.student_no, R.id.student_name, R.id.student_lastname });
                    setListAdapter(adapter);

                    setTitle(section_name);
                }
            });
        }
    }
}
