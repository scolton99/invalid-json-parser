package tech.scolton.json;

public class JSONBoolean extends JSONValue {
    private Boolean value;

    public JSONBoolean() { }

    public JSONBoolean(Boolean b) throws JSONException {
        if (b == null)
            throw new JSONException("Cannot pass null to JSONBoolean");

        this.value = b;
    }

    @Override
    public String stringify() {
        return this.value ? "true" : "false";
    }

    @Override
    String prettyPrint(int offset) {
        return JSONValue.prependString(offset) + this.stringify();
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON working = s.ignoreWhitespace();

        if (working.startsWith("true")) {
            this.value = true;
            return working.substring(4);
        } else if (working.startsWith("false")) {
            this.value = false;
            return working.substring(5);
        }

        int len = working.toString().length();
        throw new JSONException("JSONBoolean not satisfied by value " + working.toString().substring(0, Math.min(len, 5)));
    }
}
