package tech.scolton.json;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONNumber extends JSONValue {
    private Number value;

    public JSONNumber() {  }

    public JSONNumber(Number value) throws JSONException {
        if (value == null)
            throw new JSONException("Value passed to JSONNumber cannot be null.");

        this.value = value;
    }

    @Override
    public String stringify() {
        try {
            return (String) value.getClass().getDeclaredMethod("toString").invoke(value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return value.toString();
        }
    }

    @Override
    String prettyPrint(int offset) {
        return JSONValue.prependString(offset) + this.stringify();
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON working = s.ignoreWhitespace();

        Pattern p = Pattern.compile("^([+-])?(\\d+)(?:\\.(\\d+))?(?:[eE]([+-]?\\d+))?(?:[\\t\\n\\r \\[\\]{}:,]|$)");
        Matcher m = p.matcher(working.toString());

        if (m.find()) {
            String sign = m.group(1);
            String integer = m.group(2);
            String decimal = m.group(3);
            String exponent = m.group(4);

            if (sign == null) sign = "+";

            Number n;
            if (decimal == null) {
                n = Integer.parseInt(integer);
            } else {
                n = Double.parseDouble(integer + "." + decimal);
            }

            if (exponent != null) {
                double exp = Double.parseDouble(exponent);
                if (n instanceof Integer)
                    n = (int)(Math.pow(n.doubleValue(), exp));
                else
                    n = Math.pow(n.doubleValue(), exp);
            }

            if (sign.equals("-") && n instanceof Integer)
                n = Integer.parseInt("-" + n.toString());
            else if (sign.equals("-"))
                n = Double.parseDouble("-" + n.toString());

            this.value = n;

            int group1end = m.end(1);
            int group2end = m.end(2);
            int group3end = m.end(3);
            int group4end = m.end(4);

            int realEnd = Math.max(Math.max(group1end, group2end), Math.max(group3end, group4end));

            return working.substring(realEnd);
        } else {
            throw new JSONException("Number cannot start with " + working.toString().substring(0, Math.min(working.toString().length(), 15)));
        }
    }
}
