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
    private static final String url = "url.txt";

    /**
     * <p>
     *     This function returns the URL direction parsed from the .txt file contained in the phone.
     * </p>
     * @param context Application Context.
     * @return String containing the URL.
     */
    public static String getUrl(Context context) {
        try {
            return parse(getConfigFile(context));
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
     * @return BufferedReader containing the information inside the file.
     */
    private static BufferedReader getConfigFile(Context context) {
        if(isExternalStorageReadable()){
            // Get the directory for the app's private files
            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/data");
            File file;
            try {
                file = new File(mFolder.getAbsolutePath(), url);
                java.io.FileReader fr = new java.io.FileReader(file);
                return new BufferedReader(fr);

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