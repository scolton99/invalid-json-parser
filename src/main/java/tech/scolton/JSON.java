package tech.scolton;

import tech.scolton.json.*;
import tech.scolton.json.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class JSON {
    public static JSONValue parse(String s) throws JSONException {
        RawJSON r = new RawJSON(s);
        return Parser.parse(r);
    }

    public static List<JSONValue> parseMany(String s) {
        List<JSONValue> out = new ArrayList<>();

        RawJSON r = new RawJSON(s);
        while (true) {
            try {
                Pair<JSONValue, RawJSON> res = Parser.tryParseAll(r);
                out.add(res.first());
                r = res.second();
            } catch (JSONException e) {
                break;
            }
        }

        return out;
    }
}
