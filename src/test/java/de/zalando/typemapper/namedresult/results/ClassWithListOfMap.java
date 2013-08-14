package de.zalando.typemapper.namedresult.results;

import java.util.List;
import java.util.Map;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  Ingolf Wagner <ingolf.wagner@zalando.de>
 */
public class ClassWithListOfMap {

    @DatabaseField(name = "map_array")
    private List<Map<String, String>> mapList;

    @DatabaseField(name = "str")
    private String str;

    public List<Map<String, String>> getMapList() {
        return mapList;
    }

    public void setMapList(final List<Map<String, String>> mapList) {
        this.mapList = mapList;
    }

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }
}
