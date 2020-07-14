package Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReviewJob implements Serializable {
    private HashMap<String, HashMap<String, Review>> titleReviews;
    private AtomicInteger reviewsToProcess;
    private int reviews;

    public int getReviews() {
        return reviews;
    }

    public void setReviews() {
        this.reviews = reviewsToProcess.get();
    }

    public ReviewJob() {
        titleReviews = new HashMap<>();
        reviewsToProcess = new AtomicInteger(0);
    }

    public HashMap<String, HashMap<String, Review>> getTitleReviews() {
        return titleReviews;
    }

    public void setTitleReviews(HashMap<String, HashMap<String, Review>> titleReviews) {
        this.titleReviews = titleReviews;
    }

    public AtomicInteger getReviewsToProcess() {
        return reviewsToProcess;
    }

    public int decrementReviewsToProcess(){
        return reviewsToProcess.decrementAndGet();
    }

    public void setReviewsToProcess(int reviewsToProcess) {
        this.reviewsToProcess = new AtomicInteger(reviewsToProcess);
    }
}
