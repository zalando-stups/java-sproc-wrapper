package de.zalando.typemapper.sproctest.result;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

public class ArrayResult extends PrimitiveResult {

    @DatabaseField(name = "id")
    private Integer id;
    @DatabaseField(name = "msg")
    private String msg;
    @DatabaseField(name = "movies")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(final List<Movie> movies) {
        this.movies = movies;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

}
