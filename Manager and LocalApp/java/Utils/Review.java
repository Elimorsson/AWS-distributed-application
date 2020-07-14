package Utils;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

public class Review implements Serializable {
    private String id;
    private String link;
    private String title;
    private String text;
    private Integer rating;
    private String author;
    private Date date;
    private Integer nlpAnalysis;
    private String entities;

    public Review(String id, String link, String title, String text, Integer rating, String author, Date date) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.text = text;
        this.rating = rating;
        this.author = author;
        this.date = date;
    }

    public Integer getNlpAnalysis() {
        return nlpAnalysis;
    }

    public void setNlpAnalysis(Integer nlpAnalysis) {
        this.nlpAnalysis = nlpAnalysis;
    }

    public String getEntities() {
        return entities;
    }

    public void setEntities(String entities) {
        this.entities = entities;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Integer getRating() {
        return rating;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
