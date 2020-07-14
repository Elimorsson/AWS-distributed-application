import java.io.Serializable;
import java.util.Vector;

public class ReviewJob implements Serializable {
    private Vector<TitleReviews> titleReviews;

    public Vector<TitleReviews> getTitleReviews() {
        return titleReviews;
    }

    public void setTitleReviews(Vector<TitleReviews> titleReviews) {
        this.titleReviews = titleReviews;
    }

    public void addTitleReview(TitleReviews t) {
        titleReviews.add(t);
    }

    public ReviewJob() {
        this.titleReviews = new Vector<>();
    }
}
