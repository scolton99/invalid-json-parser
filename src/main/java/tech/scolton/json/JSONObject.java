package tech.scolton.json;

import java.util.*;
import java.util.stream.Collectors;

public class JSONObject extends JSONValue implements Map<JSONString, JSONValue> {
    private final Map<JSONString, JSONValue> value = new HashMap<>();

    public JSONObject () {}

    public JSONObject(Map<?, ?> map) throws JSONException {
        this.value.putAll(Parser.marshalMap(map));
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public JSONValue get(Object key) {
        return this.value.get(key);
    }

    public JSONValue putRaw(String key, Object value) throws JSONException {
        JSONString str = new JSONString(key);
        JSONValue val = Parser.marshal(value);

        return this.value.put(str, val);
    }

    @Override
    public JSONValue put(JSONString key, JSONValue value) {
        return this.value.put(key, value);
    }

    @Override
    public JSONValue remove(Object key) {
        return this.value.remove(key);
    }

    public void putAllRaw(Map<String, Object> m) throws JSONException {
        Map<JSONString, JSONValue> toPut = Parser.marshalMap(m);
        this.value.putAll(toPut);
    }

    @Override
    public void putAll(Map<? extends JSONString, ? extends JSONValue> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Set<JSONString> keySet() {
        return this.value.keySet();
    }

    @Override
    public Collection<JSONValue> values() {
        return this.value.values();
    }

    @Override
    public Set<Entry<JSONString,JSONValue>> entrySet() {
        return this.value.entrySet();
    }

    @Override
    public String stringify() {
        String entries = this.value.entrySet().stream().map(x -> {
            String key = x.getKey().stringify();
            String value = x.getValue().stringify();

            return key + ':' + value;
        }).collect(Collectors.joining(","));

        return '{' + entries + '}';
    }

    @Override
    String prettyPrint(int offset) {
        String out = JSONValue.prependString(offset) + "{\n";

        out += this.value.entrySet().stream().map(x -> {
            String key = x.getKey().prettyPrint(offset + 2);
            String value = x.getValue().prettyPrint(offset + 2).trim();

            return key + ": " + value;
        }).collect(Collectors.joining(",\n"));

        return out + '\n' + JSONValue.prependString(offset) + '}';
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON working = s.ignoreWhitespace();

        if (working.charAt(0) != '{')
            throw new JSONException("JSONObject cannot start with " + working.charAt(0));

        working = working.substring(1).ignoreWhitespace();

        while (true) {
            JSONString key;
            key = new JSONString();
            working = key.parse(working).ignoreWhitespace();

            if (working.charAt(0) != ':')
                throw new JSONException("No ':' after key for JSONObject");

            working = working.substring(1).ignoreWhitespace();

            JSONValue value;
            Pair<JSONValue, RawJSON> res = Parser.tryParseAll(working);
            working = res.second();
            value = res.first();

            this.value.put(key, value);

            working = working.ignoreWhitespace();

            if (working.charAt(0) == '}')
                break;
            if (working.charAt(0) != ',') {
                System.out.println(this.value.toString());
                throw new JSONException("Malformed JSONObject: no comma between entries, found " + working.charAt(0) + " instead");
            }

            working = working.substring(1).ignoreWhitespace();
        }

        return working.substring(1);
    }
}
