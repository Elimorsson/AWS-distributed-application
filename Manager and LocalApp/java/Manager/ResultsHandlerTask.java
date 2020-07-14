package Manager;

import Utils.Constant;
import Utils.Review;
import Utils.ReviewJob;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ResultsHandlerTask implements Runnable {
    private Database database;
    private SqsClient sqs;

    public ResultsHandlerTask() {
        database = Database.getInstance();
        sqs = SqsClient.builder().region(Constant.region).build();
    }

    @Override
    public void run() {
        System.out.println("Results handler running");
        String jobQueueUrl = Sqs_handler.getQueueUrl(sqs, Constant.RESULTS_QUEUE);
        do {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .messageAttributeNames("id", "title", "job_name")
                    .visibilityTimeout(60)
                    .maxNumberOfMessages(10)
                    .queueUrl(jobQueueUrl)
                    .waitTimeSeconds(5)
                    .build();
            List<Message> messages = sqs.receiveMessage(receiveRequest).messages();
            for (Message m : messages) {
                String body = m.body();
                ReviewJob job_to_handle = database.jobNameTojob.get(m.messageAttributes().get("job_name").stringValue());
                if(job_to_handle != null) {
                    HashMap<String, Review> title_reviews = job_to_handle.getTitleReviews().get(m.messageAttributes().get("title").stringValue());
                    Review r = title_reviews.get(m.messageAttributes().get("id").stringValue());
                    r.setNlpAnalysis(Integer.parseInt(body.substring(0, 1)));
                    System.out.println("Review id: " + m.messageAttributes().get("id").stringValue());
                    r.setEntities(body.substring(2));
                    int review_to_process = job_to_handle.decrementReviewsToProcess();
                    if (review_to_process == 0) {
                        Thread t = new Thread(new EndOfJobTask(m.messageAttributes().get("job_name").stringValue()));
                        t.start();
                    }
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(jobQueueUrl)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqs.deleteMessage(deleteRequest);
                }
            }

        } while (true);
    }
}
