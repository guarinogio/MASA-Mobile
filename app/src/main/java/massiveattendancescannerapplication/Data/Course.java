package massiveattendancescannerapplication.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Course{

    public String name;
    public String code;
    public List<Section> section;

    public Course (String _name, String _code, List<Section> _section){
        name = _name;
        code = _code;
        section = _section;
    }

    public static Course createFromJSON(JSONObject json) throws JSONException {

        List<Section> qSections = new ArrayList<>();
        String name = json.getString("name");
        String code = json.getString("code");


        if (json.has("Sections")){
            JSONArray sections = json.getJSONArray("Sections");

            for (int k = 0; k < sections.length(); k++)
            {
                qSections.add(Section.createFromJSON(sections.getJSONObject(k)));
            }
        }
        return new Course(name, code,qSections);
    }

}

