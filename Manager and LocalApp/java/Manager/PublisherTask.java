package Manager;

import Utils.Constant;
import Utils.Review;
import Utils.ReviewJob;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.Map;

public class PublisherTask implements Runnable {
    private Database database;
    private SqsClient sqsClient;
    private Map.Entry<String, HashMap<String, Review>> entry;
    private String job_name;


    public PublisherTask(Map.Entry<String, HashMap<String, Review>> entry, String job_name) {
        database = Database.getInstance();
        sqsClient = SqsClient.builder().region(Constant.region).build();
        this.entry = entry;
        this.job_name = job_name;
    }

    @Override
    public void run() {
        HashMap<String, Review> title_map = entry.getValue();
        System.out.println("This is the title i'm working on: " + entry.getKey());
        int counter = 0;
        for (Map.Entry<String, Review> child_entry : title_map.entrySet()) {
            Review r = child_entry.getValue();
            final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put("id", MessageAttributeValue.builder().stringValue(r.getId()).dataType("String").build());
            messageAttributes.put("title", MessageAttributeValue.builder().stringValue(entry.getKey()).dataType("String").build());
            messageAttributes.put("job_name", MessageAttributeValue.builder().stringValue(job_name).dataType("String").build());
            SendMessageRequest send_msg_request = SendMessageRequest.builder()
                    .queueUrl(Sqs_handler.getQueueUrl(sqsClient, Constant.TO_DO_QUEUE))
                    .messageBody(r.getText())
                    .messageAttributes(messageAttributes)
                    .delaySeconds(5)
                    .build();
            sqsClient.sendMessage(send_msg_request);
            counter++;
        }
        System.out.println("Finished with title: " + entry.getKey() + ". num of reviews: " + counter);
    }
}
