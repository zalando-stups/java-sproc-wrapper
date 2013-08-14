package de.zalando.typemapper.parser.postgres;

import java.io.Serializable;

import java.util.List;

public class Element implements Serializable {

    private static final long serialVersionUID = 3551202055534227672L;

    private List<String> rowList;

    /**
     * @return  the rowList
     */
    public List<String> getRowList() {
        return rowList;
    }

    /**
     * @param  rowList  the rowList to set
     */
    public void setRowList(final List<String> rowList) {
        this.rowList = rowList;
    }

    /**
     * @see  Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Element [rowList=");
        builder.append(rowList);
        builder.append("]");
        return builder.toString();
    }
}
