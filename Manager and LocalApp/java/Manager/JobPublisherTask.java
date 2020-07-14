package Manager;

import Utils.Constant;
import Utils.Review;
import Utils.ReviewJob;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JobPublisherTask implements Runnable {
    private Database database;
    private SqsClient sqsClient;


    public JobPublisherTask() {
        database = Database.getInstance();
        sqsClient = SqsClient.builder().region(Constant.region).build();

    }

    @Override
    public void run() {
        while(true){
            if(!database.jobsQueue.isEmpty()) {
                try {
                    System.out.println("Job publisher to your service");
                    ReviewJob job = database.jobsQueue.peek();
                    String job_name = "job" + System.currentTimeMillis();
                    database.jobNameTojob.put(job_name, job);
                    database.reviewsToDo.addAndGet(job.getReviewsToProcess().get());
                    List<Thread> publishers = new LinkedList<>();
                    for (Map.Entry<String, HashMap<String, Review>> entry : job.getTitleReviews().entrySet()) {
                        Thread publisher = new Thread(new PublisherTask(entry, job_name));
                        publisher.start();
                        publishers.add(publisher);
                    }
                    for(Thread t : publishers)
                        t.join();
                    database.jobsQueue.take();
                    System.out.println("I'm done with job: " + job_name);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
