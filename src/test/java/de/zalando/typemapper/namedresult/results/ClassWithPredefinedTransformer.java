package de.zalando.typemapper.namedresult.results;

import java.util.Arrays;
import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.namedresult.transformer.Hans;

public class ClassWithPredefinedTransformer {

    @DatabaseField
    private Hans hans;

    @DatabaseField
    private List<Hans> hansList;

    public ClassWithPredefinedTransformer() { }

    public ClassWithPredefinedTransformer(final Hans hans, final Hans... hansList) {
        this.hans = hans;
        this.hansList = Arrays.asList(hansList);
    }

    public Hans getHans() {
        return hans;
    }

    public void setHans(final Hans hans) {
        this.hans = hans;
    }

    public List<Hans> getHansList() {
        return hansList;
    }

    public void setHansList(final List<Hans> hansList) {
        this.hansList = hansList;
    }
}
