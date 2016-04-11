package massiveattendancescannerapplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import massiveattendancescannerapplication.Services.ServiceHandler;
import massiveattendancescannerapplication.Services.TransmitterTask;


public class StudentActivity extends ListActivity {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> studentList;
    JSONArray courses,sections,students;
    JSONObject professor;
    String course_id, section_name, section_id, currentDate;
    private static final String COURSE = "courses";
    private static final String SECTION = "sections";
    private static final String STUDENT = "students";
    private static final String URL = "http://192.168.0.106:3000/professors/56f5fd3a20047f3c15b05f0e";
    private static final String TAG_ID = "_id";
    private static final String TAG_NO = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_STUDENTS = "students";
    private static final String BT_ADDRESS = "btaddress";
    private static final int REQUEST_ENABLE_BT = 1;
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;
    ListView listDevicesFound;
    Button btnScanDevice,btnEndClass;
    TransmitterTask transmitterTask;
    ArrayAdapter<String> btArrayAdapter;
    int assist = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity);
        Intent i = getIntent();

        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listDevicesFound = (ListView)findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<>(StudentActivity.this,
                android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);

        btnScanDevice = (Button)findViewById(R.id.scandevice);
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        btnEndClass = (Button)findViewById(R.id.terminate);
        btnEndClass.setOnClickListener(btnEndClassOnClickListener);

        CheckBlueToothState();

        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));

        course_id = i.getStringExtra("course_id");
        section_id = i.getStringExtra("section_id");
        studentList = new ArrayList<>();
        new LoadStudents().execute();
    }

    @Override
    protected void onStop()
    {
        unregisterReceiver(ActionFoundReceiver);
        super.onStop();
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
                professor = new JSONObject(JSONString);
                if (professor.has(COURSE)) {
                    courses = professor.getJSONArray(COURSE);
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject c = courses.getJSONObject(i);
                        String course = c.getString(TAG_ID);
                        assert course != null;
                        if (course.equals(course_id)){
                            if (c.has(SECTION)) {
                                sections = c.getJSONArray(SECTION);
                                for (int j = 0; j < sections.length(); j++) {
                                    JSONObject s = sections.getJSONObject(j);
                                    String section = s.getString(TAG_ID);
                                    assert section != null;
                                    if (section.equals(section_id)) {
                                        if(s.has(STUDENT)) {
                                            section_name = s.getString(TAG_NAME);
                                            students = s.getJSONArray(TAG_STUDENTS);
                                            for (int x = 0; x < students.length(); x++) {
                                                JSONObject st = students.getJSONObject(x);
                                                String student_id = String.valueOf(x + 1);
                                                String student_no = st.getString(TAG_NO);
                                                String name = st.getString(TAG_NAME);
                                                String lastname = st.getString(TAG_LASTNAME);
                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("student_id", student_id);
                                                map.put(TAG_ID, student_id);
                                                map.put("student_no", student_no);
                                                map.put(TAG_NAME, name);
                                                map.put(TAG_LASTNAME, lastname);
                                                studentList.add(map);
                                            }
                                        }
                                    }
                                }
                            }
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
                            R.layout.list_item_students, new String[]{"section_id", TAG_ID, TAG_NO,
                            TAG_NAME, TAG_LASTNAME}, new int[]{
                            R.id.section_id, R.id.student_id, R.id.student_no, R.id.student_name, R.id.student_lastname});
                    setListAdapter(adapter);

                    setTitle(section_name);
                }
            });
        }
    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            stateBluetooth.setText("Bluetooth NOT supported");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                    btnScanDevice.setEnabled(false);

                }else{
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            }else{
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private Button.OnClickListener btnScanDeviceOnClickListener
            = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            btArrayAdapter.clear();
            bluetoothAdapter.startDiscovery();
        }};

    private Button.OnClickListener btnEndClassOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            try {
                for (int x = 0; x < students.length(); x++) {
                    JSONObject st;
                    boolean scanned = false;
                    st = students.getJSONObject(x);
                    JSONArray assistance;
                    assistance = st.getJSONArray("assistanceTotal");
                    assert assistance != null;
                    currentDate = SDF.format(new Date());
                    for(int g=0; g<assistance.length(); g++) {
                        JSONObject cons = assistance.getJSONObject(g);
                        String consDay = cons.getString("day");
                        if (consDay.equals(currentDate)){
                            scanned = true;
                        }
                    }
                    if(!scanned){
                        assist = st.optInt("assistance");
                        st.put("assistance", assist+1);
                        JSONObject dayAssistance = new JSONObject();
                        dayAssistance.put("day", currentDate);
                        dayAssistance.put("assistance", false);
                        assistance.put(dayAssistance);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            transmitterTask = new TransmitterTask();
            transmitterTask.execute(professor);
            finish();
        }};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                boolean scanned = false;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String device_name = device.getName();
                String device_address = device.getAddress();
                for (int x = 0; x < students.length(); x++) {
                    JSONObject st = null;
                    try {
                        st = students.getJSONObject(x);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String student_bt = null;
                    try {
                        assert st != null;
                        student_bt = st.getString(BT_ADDRESS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(student_bt != null) {
                        if (student_bt.equals(device_address)) {
                            Toast.makeText(getApplicationContext(), "FOUND DEVICE!", Toast.LENGTH_SHORT).show();
                            try {
                                JSONArray assistance;
                                assistance = st.getJSONArray("assistanceTotal");
                                currentDate = SDF.format(new Date());
                                for(int g=0; g<assistance.length(); g++) {
                                    JSONObject cons = assistance.getJSONObject(g);
                                    String consDay = cons.getString("day");
                                    if (consDay.equals(currentDate)){
                                        scanned = true;
                                    }
                                }
                                if(!scanned){
                                    assist = st.optInt("assistance");
                                    st.put("assistance", assist+1);
                                    JSONObject dayAssistance = new JSONObject();
                                    dayAssistance.put("day", currentDate);
                                    dayAssistance.put("assistance", true);
                                    assistance.put(dayAssistance);
                                    }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        String student_no = null;
                        try {
                            student_no = st.getString(TAG_NO);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert student_no != null;
                        if (device_name.equals(student_no)) {
                            Toast.makeText(getApplicationContext(), "FOUND ID!", Toast.LENGTH_SHORT).show();
                            try {
                                st.put("btaddress",device_address);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray assistance;
                                assistance = st.getJSONArray("assistanceTotal");
                                currentDate = SDF.format(new Date());
                                for(int g=0; g<assistance.length(); g++) {
                                    JSONObject cons = assistance.getJSONObject(g);
                                    String consDay = cons.getString("day");
                                    if (consDay.equals(currentDate)){
                                        scanned = true;
                                    }
                                }
                                if(!scanned){
                                    assist = st.optInt("assistance");
                                    st.put("assistance", assist+1);
                                    JSONObject dayAssistance = new JSONObject();
                                    dayAssistance.put("day", currentDate);
                                    dayAssistance.put("assistance", true);
                                    assistance.put(dayAssistance);
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };
}
