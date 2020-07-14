import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.*;


public class Worker {
    static SentimentHandler sentimentHandler = new SentimentHandler();
    static NlpHandler nlpHandler = new NlpHandler();

    public static void main(String[] args) {
        SqsClient sqsClient = SqsClient.builder().region(Constant.region).build();
        String to_do_queue_url = Sqs_handler.getQueueUrl(sqsClient, Constant.TO_DO_QUEUE);
        boolean terminate = false;
        do {
            System.out.println("Reciving message");
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(to_do_queue_url)
                    .messageAttributeNames("id", "title", "job_name")
                    .waitTimeSeconds(5)
                    .maxNumberOfMessages(1)
                    .visibilityTimeout(100)
                    .build();
            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            for (Message m : messages) {
                System.out.println("Message: " + m.toString());
                if(m.body().equals(Constant.TERMINATE)) {
                    terminate = true;
                }
                else {
                    System.out.println("Body: " + m.body());
                    String message_body = m.body();
                    System.out.println("Get sentiment");
                    int sentiment = sentimentHandler.findSentiment(message_body);
                    System.out.println("Sentiment: " + sentiment + "\nGet entities");
                    Vector<String> entitys = nlpHandler.getEntities(message_body);
                    SendMessageRequest send_msg_request = SendMessageRequest.builder()
                            .queueUrl(Sqs_handler.getQueueUrl(sqsClient, Constant.RESULTS_QUEUE))
                            .messageBody(createResultMessage(sentiment, entitys))
                            .messageAttributes(m.messageAttributes())
                            .delaySeconds(5)
                            .build();
                    sqsClient.sendMessage(send_msg_request);
                    System.out.println("Send result message: " + send_msg_request.messageBody());
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(to_do_queue_url)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteRequest);
                }
            }
        } while (!terminate);

    }

    private static String createResultMessage(int sentiment, Vector<String> entitys){
        StringBuilder s = new StringBuilder();
        s.append(sentiment);
        for(String entity : entitys){
            s.append(",").append(entity);
        }
        return s.toString();
    }
}
