package tech.scolton.json;

public class RawJSON implements CharSequence {
    private final StringBuilder value;

    public RawJSON(String s) {
        this.value = new StringBuilder(s);
    }

    public RawJSON ignoreWhitespace() {
        if (this.value.length() == 0) return this;

        int ptr = 0;

        while (ptr < this.value.length() && Parser.isWhitespace(this.value.charAt(ptr)))
            ptr++;

        if (ptr == 0) return this;

        return new RawJSON(this.value.substring(ptr));
    }

    public RawJSON substring(int start) {
        return new RawJSON(this.value.substring(start));
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return index >= this.value.length() ? '\u0000' : this.value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public boolean startsWith(String prefix) {
        if (prefix.length() > this.value.length()) return false;
        return this.value.substring(0, prefix.length()).equals(prefix);
    }
}
