package geex;


import geex.Seed;
import geex.SeedUtils;
import java.util.ArrayList;
import geex.Dependencies;
import java.lang.RuntimeException;
import clojure.lang.APersistentMap;
import geex.Mode;

public class DynamicSeed implements Seed {
    private SeedParameters _params = null;
    private Object _compilationResult = null;
    private Dependencies _deps = new Dependencies();
    private Referents _refs = new Referents();
    private Object _data;
    private int _id = Seed.UNDEFINED_ID;

    public DynamicSeed(SeedParameters p) {
        if (p.description == null) {
            throw new RuntimeException("Missing description");
        }
        if (p.compiler == null) {
            throw new RuntimeException("Missing compiler");
        }
        if (p.mode == null) {
            throw new RuntimeException(
                "Seed mode has not been defined");
        }
        _params = p;
    }

    public APersistentMap getRawDeps() {
        return _params.rawDeps;
    }

    public Mode getMode() {
        return _params.mode;
    }

    public Object getType() {
        return _params.type;
    }

    public void setId(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public String toString() {
        return SeedUtils.toString(this);
    }

    public boolean equals(Object other) {
        return SeedUtils.equals(this, other);
    }

    public int hashCode() {
        return SeedUtils.hashCode(this);
    }

    public Dependencies deps() {
        return _deps;
    }

    public Referents refs() {
        return _refs;
    }

    public void setCompilationResult(Object x) {
        _compilationResult = x;
    }

    public Object getCompilationResult() {
        return _compilationResult;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object o) {
        _data = o;
    }

    public SeedFunction getSeedFunction() {
        return _params.seedFunction;
    }
}
