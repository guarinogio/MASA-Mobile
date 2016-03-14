package massiveattendancescannerapplication.Services;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>
 *     Function that parses a file to get a URL direction. The text files must be placed inside the
 *     \Android\data\com.smartmatic.sitesurvey\files\ folder to be able to read them.
 * </p>
 *
 * @author Reynaldo
 */
public class FileReader {

    static String response;
    private static final String professors = "professors.txt";
    private static final String courses = "courses.txt";
    private static final String sections = "sections.txt";
    private static final String students = "students.txt";

    /**
     * <p>
     *     This function returns the URL direction parsed from the .txt file contained in the phone.
     * </p>
     * @param context Application Context.
     * @param method Method representing POST of GET methods.
     * @return String containing the URL.
     */
    public static String getUrl(Context context, int method) {
        try {
            return parse(getConfigFile(context,method));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>
     *     This function handles the type of method of connecting (POST and GET) returning the URL
     *     direction for each type.
     * </p>
     * @param context Application Context.
     * @param method Method representing POST of GET methods.
     * @return BufferedReader containing the information inside the file.
     */
    private static BufferedReader getConfigFile(Context context,int method) {
        if(isExternalStorageReadable()){
            // Get the directory for the app's private files
            File file = null;
            try {
                if(method == 1) file = new File(context.getExternalFilesDir(null), professors);
                if(method == 2) file = new File(context.getExternalFilesDir(null), courses);
                if(method == 3) file = new File(context.getExternalFilesDir(null), sections);
                if(method == 4) file = new File(context.getExternalFilesDir(null), students);

                if (file!=null) {
                    java.io.FileReader fr = new java.io.FileReader(file);
                    return new BufferedReader(fr);
                }else{
                    return null;
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    // Checks if external storage is available to at least read
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static String parse(BufferedReader reader) throws IOException {
        try {
            String line = reader.readLine();
            while(line != null){
                response = line;
                line = reader.readLine();
            }
            return response;
        } finally {
            reader.close();
        }
    }
}