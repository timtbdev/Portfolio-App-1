package info.tumur.resume.app.data;

public class Constant {

    // WEB_URL with your url. Make sure you have backslash('/') in the end url
    public static String WEB_URL = "http://tumur.info/";

    // Path to image
    public static String getURLimg(String file_name) {
        return WEB_URL + "app/images/" + file_name;
    }
}
