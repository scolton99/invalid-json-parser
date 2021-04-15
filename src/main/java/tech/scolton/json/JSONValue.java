package tech.scolton.json;

public abstract class JSONValue {
    public abstract String stringify();
    public String prettyPrint() {
        return this.prettyPrint(0);
    }
    abstract String prettyPrint(int offset);
    protected abstract RawJSON parse(RawJSON s) throws JSONException;
    protected static String prependString(int size) {
        return " ".repeat(size);
    }
}
