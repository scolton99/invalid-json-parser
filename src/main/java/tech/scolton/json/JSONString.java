package tech.scolton.json;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONString extends JSONValue {
    private String value;
    private int idx;

    public JSONString() { }

    public JSONString(String s) throws JSONException {
        if (s == null)
            throw new JSONException("Cannot pass null to JSONString");

        this.value = s;
    }

    @Override
    public String stringify() {
        String x = this.value;

        x = x.replaceAll("\\\\", "\\\\\\\\");
        for (int i = 0x0; i < 0x20; ++i) {
            String pat = String.format("\\u%04x", i);
            String replacement = String.format("\\\\u%04x", i);
            Pattern p = Pattern.compile(replacement);
            Matcher m = p.matcher(x);
            x = m.replaceAll(replacement);
        }

        x = x.replaceAll("\"", "\\\\\"");

        return '"' + x + '"';
    }

    @Override
    String prettyPrint(int offset) {
        return JSONValue.prependString(offset) + this.stringify();
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON working = s.ignoreWhitespace();

        if (working.charAt(0) != '"')
            throw new JSONException("JSONString cannot start with " + working.charAt(0));

        Pattern p = Pattern.compile("(?<!\\\\|^)\"");
        Matcher m = p.matcher(working.toString());

        int end;

        String raw;
        if (m.find()) {
            end = m.end();
            raw = s.toString().substring(1, end - 1);
        } else {
            throw new JSONException("Couldn't find end to String");
        }

        Pattern invalid = Pattern.compile("[\\x01\\x02\\x03\\x04\\x05\\x06\\x07\\x08\\x09\\x0A\\x0B\\x0C\\x0D\\x0E\\x0F\\x10\\x11\\x12\\x13\\x14\\x15\\x16\\x17\\x18\\x19\\x1A\\x1B\\x1C\\x1D\\x1E\\x1F]");
        Matcher invalidMatcher = invalid.matcher(raw);

        if (invalidMatcher.find()) {
            System.out.println(invalidMatcher.group());
            throw new JSONException("Invalid control characters in string literal");
        }

        List<Character> allowed = List.of('\\', '"', '/', 'b', 'f', 'n', 'r', 't');
        int start = 0;
        int idx;
        while ((idx = raw.indexOf('\\', start)) != -1) {
            if (idx + 1 > raw.length())
                throw new JSONException("Escape character at end of string");
            if (raw.charAt(idx + 1) == 'u') {
                String numTest = raw.substring(idx + 2, idx + 6);
                Pattern numPattern = Pattern.compile("[0-9a-fA-F]{4}");
                Matcher numMatcher = numPattern.matcher(numTest);
                if (!numMatcher.matches())
                    throw new JSONException("Invalid unicode point in string");
                start = idx + 6;
                continue;
            }
            if (!allowed.contains(raw.charAt(idx + 1))) {
                throw new JSONException("Invalid escape character");
            }
            start = idx + 2;
        }

        Pattern unicode = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher unicodeMatcher = unicode.matcher(raw);

        String out = unicodeMatcher.replaceAll(mr -> {
            String uniChar = mr.group(1);
            char uniCharVal = (char) Integer.parseInt(uniChar, 16);

            return Character.toString(uniCharVal);
        });

        String[] otherEscapes = {"\\\\\"", "\\\\\\\\", "\\\\/", "\\\\b", "\\\\f", "\\\\n", "\\\\r", "\\\\t"};
        String[] replacements = {"\"", "\\\\", "/", "\b", "\f", "\n", "\r", "\t"};
        Pattern[] patterns = new Pattern[otherEscapes.length];

        for (int i = 0; i < otherEscapes.length; ++i) {
            String escape = otherEscapes[i];
            patterns[i] = Pattern.compile(escape);
        }

        for (int i = 0; i < patterns.length; ++i){
            Pattern escapePattern = patterns[i];
            Matcher escapeMatcher = escapePattern.matcher(out);
            out = escapeMatcher.replaceAll(replacements[i]);
        }

        this.value = out;

        return working.substring(end);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JSONString)
            return ((JSONString) o).value.equals(this.value);

        if (o instanceof String)
            return o.equals(this.value);

        return false;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
