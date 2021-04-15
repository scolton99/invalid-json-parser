import org.junit.Test;
import tech.scolton.JSON;
import tech.scolton.json.JSONException;
import tech.scolton.json.JSONValue;

import java.util.List;
import java.util.stream.Collectors;

public class ParseTest {
    @Test
    public void testParse() throws JSONException {
        String json = "{\"content\":4}{\"asdf\":6}\n\n\n   [   32, 64.4E8, \"str\", { \"iobj\": null } ]  \r\n\t  ";

        List<JSONValue> j = JSON.parseMany(json);

        System.out.println(j.stream().map(JSONValue::stringify).collect(Collectors.joining("\n")));
    }

    @Test
    public void testParse2() throws JSONException {
        String json = "\n\n [ -18.345E44]";
        JSONValue j = JSON.parse(json);

        System.out.println(j.prettyPrint());
    }
}
