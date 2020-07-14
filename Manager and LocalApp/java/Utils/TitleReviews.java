package Utils;

import java.io.Serializable;
import java.util.Vector;

public class TitleReviews implements Serializable {
    private String title;
    private Vector<Review> Reviews;

    public TitleReviews(String title, Vector<Review> Reviews) {
        this.title = title;
        this.Reviews = Reviews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Vector<Review> getReviews() {
        return Reviews;
    }

    public void setReviews(Vector<Review> reviews) {
        Reviews = reviews;
    }
}
