package com.brenopolanski.movies.model;

import java.io.Serializable;

public class Review implements Serializable {
    private String author;
    private String content;

    public String getAuthor() {

        return author;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }
}
