package massiveattendancescannerapplication.Data;

import org.json.JSONException;
import org.json.JSONObject;

public class Student {

    public String id;
    public String name;
    public String lastname;
    public String btAddress;

    public Student (String _id, String _name, String _lastname, String _btAddress){
        id = _id;
        name = _name;
        lastname = _lastname;
        btAddress = _btAddress;
    }

    public static Student createFromJSON(JSONObject json) throws JSONException {

        String id = json.getString("id");
        String name = json.getString("name");
        String lastname = json.getString("lastname");
        String btAddress = json.getString("btAddress");

        return new Student(id, name, lastname, btAddress);
    }

}
