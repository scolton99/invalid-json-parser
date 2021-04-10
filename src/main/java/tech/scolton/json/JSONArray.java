package tech.scolton.json;

import java.util.*;
import java.util.stream.Collectors;

public class JSONArray extends JSONValue implements List<JSONValue> {
    private final List<JSONValue> value = new ArrayList<>();

    public JSONArray() {  }

    public JSONArray(List<?> list) throws JSONException {
        List<JSONValue> marshalled = Parser.marshalList(list);
        this.value.addAll(marshalled);
    }

    @Override
    public String stringify() {
        String out = this.value.stream().map(JSONValue::stringify).collect(Collectors.joining(","));

        return '[' + out + ']';
    }

    @Override
    String prettyPrint(int offset) {
        String out = JSONValue.prependString(offset) + "[\n";

        out += this.value.stream().map(x -> x.prettyPrint(offset + 2)).collect(Collectors.joining(",\n"));

        return out + "\n" + JSONValue.prependString(offset) + "]";
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON trimmed = s.ignoreWhitespace();

        if (trimmed.charAt(0) != '[')
            throw new JSONException("Tried to parse an array but first non-whitespace character found was " + trimmed.charAt(0));

        RawJSON working = trimmed.substring(1).ignoreWhitespace();

        while (true) {
            Pair<JSONValue, RawJSON> res = Parser.tryParseAll(working);
            working = res.second();
            JSONValue element = res.first();

            this.value.add(element);

            working = working.ignoreWhitespace();
            if (working.charAt(0) == ']')
                break;
            if (working.charAt(0) != ',')
                throw new JSONException("Malformed JSONArray: no comma between elements or array ends without ]");

            working = working.substring(1).ignoreWhitespace();
        }

        return working.substring(1);
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
    public boolean contains(Object o) {
        return this.value.contains(o);
    }

    @Override
    public Iterator<JSONValue> iterator() {
        return this.value.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.value.toArray(a);
    }

    public boolean addRaw(Object o) throws JSONException {
        return this.value.add(Parser.marshal(o));
    }

    @Override
    public boolean add(JSONValue jsonValue) {
        return this.value.add(jsonValue);
    }

    @Override
    public boolean remove(Object o) {
        return this.value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.value.containsAll(c);
    }

    public boolean addAllRaw(Collection<?> c) throws JSONException {
        Collection<JSONValue> marshalled = Parser.marshalList(c);
        return this.value.addAll(marshalled);
    }

    public boolean addAllRaw(int index, Collection<?> c) throws JSONException {
        Collection<JSONValue> marshalled = Parser.marshalList(c);
        return this.value.addAll(index, marshalled);
    }

    @Override
    public boolean addAll(Collection<? extends JSONValue> c) {
        return this.value.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends JSONValue> c) {
        return this.value.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.value.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.value.retainAll(c);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public JSONValue get(int index) {
        return this.value.get(index);
    }

    public JSONValue setRaw(int index, Object element) throws JSONException {
        JSONValue j = Parser.marshal(element);
        return this.value.set(index, j);
    }

    @Override
    public JSONValue set(int index, JSONValue element) {
        return this.value.set(index, element);
    }

    public void addRaw(int index, Object element) throws JSONException {
        JSONValue j = Parser.marshal(element);
        this.value.add(index, j);
    }

    @Override
    public void add(int index, JSONValue element) {
        this.value.add(index, element);
    }

    @Override
    public JSONValue remove(int index) {
        return this.value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.value.lastIndexOf(o);
    }

    @Override
    public ListIterator<JSONValue> listIterator() {
        return this.value.listIterator();
    }

    @Override
    public ListIterator<JSONValue> listIterator(int index) {
        return this.value.listIterator(index);
    }

    @Override
    public List<JSONValue> subList(int fromIndex, int toIndex) {
        return this.value.subList(fromIndex, toIndex);
    }
}
