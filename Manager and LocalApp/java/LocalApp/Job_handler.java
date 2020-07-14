package LocalApp;

import Utils.Review;
import Utils.ReviewJob;
import Utils.TitleReviews;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Job_handler {
    public Job_handler() {
    }

    static ReviewJob create_review_job(String[] args, int index) {
        System.out.println("Creating job from input files");
        Gson g = new Gson();
        ReviewJob job = new ReviewJob();
        for(int i = 0; i < index; i++) {
            try {
                FileInputStream fis = new FileInputStream(args[i]);
                Scanner sc = new Scanner(fis);
                while (sc.hasNextLine()) {
                    TitleReviews t = g.fromJson(sc.nextLine(), TitleReviews.class);
                    HashMap<String, Review> reviewHashMap = job.getTitleReviews().get(t.getTitle());
                    if (reviewHashMap == null) {
                        reviewHashMap = CreateReviewMap(t);
                        job.getTitleReviews().put(t.getTitle(), reviewHashMap);
                    }
                    else {
                        for(Review r : t.getReviews()){
                            reviewHashMap.put(r.getId(), r);
                        }
                    }
                }
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        job.setReviewsToProcess(CalculateReviewNumber(job));
        job.setReviews();
        return job;
    }

    private static HashMap<String, Review> CreateReviewMap(TitleReviews t){
        HashMap<String, Review> ans_map = new HashMap<>();
        for(Review r : t.getReviews()){
            ans_map.put(r.getId(), r);
        }
        return ans_map;
    }

    private static int CalculateReviewNumber(ReviewJob job) {
        int review_number = 0;
        for(Map.Entry<String, HashMap<String, Review>> entity : job.getTitleReviews().entrySet()){
            review_number += entity.getValue().size();
        }
        return review_number;
    }
}
