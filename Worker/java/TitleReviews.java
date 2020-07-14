import java.io.Serializable;

public class TitleReviews implements Serializable {
    private String title;
    private Review[] Reviews;

    public TitleReviews(String title, Review[] Reviews) {
        this.title = title;
        this.Reviews = Reviews;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReviews(Review[] Reviews) {
        this.Reviews = Reviews;
    }

    public String getTitle() {
        return title;
    }

    public Review[] getReviews() {
        return Reviews;
    }
}
