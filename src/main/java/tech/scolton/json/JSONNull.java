package tech.scolton.json;

public class JSONNull extends JSONValue {
    JSONNull() { }

    @Override
    public String stringify() {
        return "null";
    }

    @Override
    String prettyPrint(int offset) {
        return JSONValue.prependString(offset) + this.stringify();
    }

    @Override
    protected RawJSON parse(RawJSON s) throws JSONException {
        RawJSON working = s.ignoreWhitespace();

        if (working.startsWith("null"))
            return working.substring(4);

        int len = working.toString().length();
        throw new JSONException("JSONNull not satisfied by " + working.toString().substring(0, Math.min(5, len)));
    }
}
