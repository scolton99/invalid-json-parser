package tech.scolton.json;

import tech.scolton.json.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private static final char[] WHITESPACE = {' ', '\n', '\r', '\t'};
    private static final List<Class<? extends JSONValue>> JSON_VALUES = List.of(
            JSONArray.class,
            JSONBoolean.class,
            JSONNull.class,
            JSONNumber.class,
            JSONObject.class,
            JSONString.class
    );

    static boolean isWhitespace(char c) {
        for (char ws : WHITESPACE)
            if (c == ws) return true;

        return false;
    }

    public static JSONValue parse(RawJSON j) throws JSONException {
        Pair<JSONValue, RawJSON> res = Parser.tryParseAll(j);
        return res.first();
    }

    public static Pair<JSONValue, RawJSON> tryParseAll(RawJSON s) throws JSONException {
        List<String> parseErrors = new ArrayList<>(Parser.JSON_VALUES.size());

        for (Class<? extends JSONValue> c : Parser.JSON_VALUES) {
            JSONValue j;

            try {
                j = c.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Couldn't instantiate specific type of JSONValue: " + c.getName());
            }

            try {
                RawJSON ret = j.parse(s);
                return new Pair<>(j, ret);
            } catch (JSONException e) {
                parseErrors.add(e.getMessage());
            }
        }

        String errors = String.join("\n", parseErrors);
        throw new JSONException("Couldn't parse string as anything. String: \"" + s.toString() + "\", \nErrors: \n" + errors);
    }

    static JSONValue marshal(Object o) throws JSONException {
        if (o == null)
            return new JSONNull();

        if (o instanceof JSONAble)
            return ((JSONAble) o).toJson();

        if (o instanceof Number)
            return new JSONNumber((Number) o);

        if (o instanceof String)
            return new JSONString((String) o);

        if (o instanceof List)
            return new JSONArray((List<?>) o);

        if (o instanceof Map)
            return new JSONObject((Map<?, ?>) o);

        if (o instanceof Boolean)
            return new JSONBoolean((Boolean) o);

        throw new JSONException("Unable to marshal object " + o.toString());
    }

    private static JSONValue safeMarshal(Object x) {
        try {
            return Parser.marshal(x);
        } catch (JSONException e) {
            return null;
        }
    }

    static List<JSONValue> marshalList(Collection<?> x) throws JSONException {
        List<JSONValue> marshalled = x.stream().map(Parser::safeMarshal).collect(Collectors.toList());

        if (marshalled.contains(null))
            throw new JSONException("Error occurred in marshalling list");

        return marshalled;
    }

    static Map<JSONString, JSONValue> marshalMap(Map<?, ?> x) throws JSONException {

        Map<JSONString, JSONValue> marshalled = new HashMap<>();
        List<JSONException> exceptions = new ArrayList<>();

        x.forEach((key, value) -> {
            try {
                JSONValue marshalledKey = Parser.marshal(key);
                JSONValue marshalledValue = Parser.marshal(value);

                if (!(marshalledKey instanceof JSONString))
                    throw new JSONException();

                marshalled.put((JSONString) marshalledKey, marshalledValue);
            } catch (JSONException e) {
                exceptions.add(e);
            }
        });

        if (!exceptions.isEmpty())
            throw new JSONException("Failed to marshal map entry: ", exceptions.get(exceptions.size() - 1));

        return marshalled;
    }
}

