package tech.scolton.json.util;

public class Pair<X, Y> {
    private X first;
    private Y second;

    public Pair(X x, Y y) {
        this.first = x;
        this.second = y;
    }

    public X first() {
        return this.first;
    }

    public Y second() {
        return this.second;
    }

    public void setFirst(X x) {
        this.first = x;
    }

    public void setSecond(Y y) {
        this.second = y;
    }
}
