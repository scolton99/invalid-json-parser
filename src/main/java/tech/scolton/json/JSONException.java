package tech.scolton.json;

public class JSONException extends Exception {
    public JSONException() { }

    public JSONException(String s) {
        super(s);
    }

    public JSONException(String s, Throwable cause) {
        super(s, cause);
    }
}
