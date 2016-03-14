package com.example.masa.massiveattendancescannerapplication.Services;

import com.example.masa.massiveattendancescannerapplication.Data.Course;
import com.example.masa.massiveattendancescannerapplication.Data.Section;

import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**<p>
 *      This class parses a JSON Object which is received from the Main Activity.
 *      The methods in this class are private except for GetQuestionary(),which is the main call for
 *      this class to parse,fill the questions objects and return an ArrayList consisting of several
 *      question objects that later are used to construct the survey.
 * </p>
 * @author Reynaldo
 */

public class JSONParser {

    public static ArrayList<Course> getCourses(String JSONString) {

        try {
            return parse(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Course> parse(String JSONString) throws JSONException {
        try {
            JSONArray json = new JSONArray(JSONString);
            return readFile(json);
        }  catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** <p>
     *      This function receives a JSON object and parses trough all the elements until a Course
     *      tag is reached. The JSON object contains various nested JSON Arrays that represent
     *      different levels of the survey info, such as Courses, Sections, and Students,
     *      these arrays must be parsed in order to reach the courses that are needed to construct
     *      the list. Once the Course tag is reached, the function calls
     *      readCourses(JSON Object).
     *  </p>
     *
     * @param json a JsonObject containing Courses info.
     * @return an ArrayList containing various course objects.
     * @throws JSONException if JSON object is empty or null.
     */

    private static ArrayList<Course> readFile(JSONArray json) throws IOException, JSONException {

        //final String DATA = "Courses";
        ArrayList<Course> qCourses = new ArrayList<>();
        //if (json.has(DATA)) {
            //JSONArray courses = json.getJSONArray(DATA);
            for (int h = 0; h < json.length(); h++) {
                qCourses.add(Course.createFromJSON(json.getJSONObject(h)));
            }
        //}
        return qCourses;
    }
}