package com.example.masa.massiveattendancescannerapplication.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Section {

    public String name;
    public String semester;
    public List<Student> student;

    public Section (String _name, String _semester, List<Student> _student){
        name = _name;
        semester = _semester;
        student = _student;
    }

    public static Section createFromJSON(JSONObject json) throws JSONException {

        String name = json.getString("name");
        String semester = json.getString("semester");
        List<Student> qStudents = new ArrayList<Student>();


        if (json.has("Students")){
            JSONArray students = json.getJSONArray("Students");

            for (int k = 0; k < students.length(); k++)
            {
                qStudents.add(Student.createFromJSON(students.getJSONObject(k)));
            }
        }
        return new Section(name, semester, qStudents);
    }


}
