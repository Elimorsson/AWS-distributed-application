package LocalApp;

import Utils.Constant;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class Sqs_handler {

    public static String getQueueUrl(SqsClient sqsClient, String queue_name) {
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queue_name)
                .build();
        return sqsClient.getQueueUrl(getQueueRequest).queueUrl();
    }

    static void sendRegistrationMessage(SqsClient sqsClient, String app_name, String job_queue_name, String N) {
        System.out.println("Sending registration message");
        SendMessageRequest send_msg_request = SendMessageRequest.builder()
                .queueUrl(Sqs_handler.getQueueUrl(sqsClient, Constant.REGISTRATION_QUEUE))
                .messageBody(app_name + "," + job_queue_name + "," + N)
                .delaySeconds(5)
                .build();
        sqsClient.sendMessage(send_msg_request);
    }

    static void sendterminationMessage(SqsClient sqsClient) {
        System.out.println("Sending Termination message");
        SendMessageRequest send_msg_request = SendMessageRequest.builder()
                .queueUrl(Sqs_handler.getQueueUrl(sqsClient, Constant.REGISTRATION_QUEUE))
                .messageBody(Constant.TERMINATE)
                .delaySeconds(5)
                .build();
        sqsClient.sendMessage(send_msg_request);
    }

    static void createJobQueue(SqsClient sqsClient, String job_queue_name) {
        System.out.println("Creating job queue: " + job_queue_name);
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(job_queue_name).build();
        sqsClient.createQueue(createQueueRequest);
    }

    static void sendJobMessage(SqsClient sqsClient, String bucket, String job_queue_name, String file_name) {
        System.out.println("Sending job message. bucket: " + bucket + ". queue name: " +
                job_queue_name + ". file name: " + file_name);
        SendMessageRequest send_msg_request = SendMessageRequest.builder()
                .queueUrl(Sqs_handler.getQueueUrl(sqsClient, job_queue_name))
                .messageBody(bucket + "," + file_name)
                .delaySeconds(5)
                .build();
        sqsClient.sendMessage(send_msg_request);
    }
}
