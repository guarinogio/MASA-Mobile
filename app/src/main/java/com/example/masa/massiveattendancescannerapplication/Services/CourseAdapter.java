package com.example.masa.massiveattendancescannerapplication.Services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.masa.massiveattendancescannerapplication.Data.Course;
import com.example.masa.massiveattendancescannerapplication.R;

import java.util.ArrayList;

public class CourseAdapter extends ArrayAdapter<Course> {
    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_course, parent, false);
        }
        // Lookup view for data population
        TextView Name = (TextView) convertView.findViewById(R.id.Name);
        TextView Code = (TextView) convertView.findViewById(R.id.Code);
        // Populate the data into the template view using the data object
        Name.setText(course.name);
        Code.setText(course.code);
        // Return the completed view to render on screen
        return convertView;
    }
}