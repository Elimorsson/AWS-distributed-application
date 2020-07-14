package Manager;

import Utils.Constant;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

public class RegistrationHandlerTask implements Runnable {
    private Database database;
    private SqsClient sqs;
    private boolean isNParameterInitialized = false;

    public RegistrationHandlerTask() {
        database = Database.getInstance();
        sqs = SqsClient.builder().region(Constant.region).build();
    }
    @Override
    public void run() {
        System.out.println("Registration thread start run");
        String registrationQueueUrl = Sqs_handler.getQueueUrl(sqs, Constant.REGISTRATION_QUEUE);
        boolean terminate = false;
        do {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .maxNumberOfMessages(5)
                    .queueUrl(registrationQueueUrl)
                    .waitTimeSeconds(20)
                    .build();
            List<Message> messages = sqs.receiveMessage(receiveRequest).messages();
            for (Message m : messages) {
                System.out.println("Message: " + m.body() + " id: " + m.messageId());
                if(m.body().equals(Constant.TERMINATE)) {
                    terminate = true;
                }
                else {
                    String[] messageArray = m.body().split(",");
                    if(!isNParameterInitialized) {
                        database.N.set(Integer.parseInt(messageArray[2]));
                        isNParameterInitialized = true;
                    }
                    database.localToJobQueueMap.put(messageArray[0], messageArray[1]);
                    Thread t = new Thread(new LocalAppHandlerTask(messageArray[1], messageArray[0]));
                    t.start();
                    database.localToThreadMap.put(messageArray[0], t);
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(registrationQueueUrl)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqs.deleteMessage(deleteRequest);
                    System.out.println("Register local: " + messageArray[0] + " and delete message");
                }
            }
            if (terminate) {
                database.terminateLocalThreads.set(true);
                while(!database.localToThreadMap.isEmpty()){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                database.terminateManager.set(true);
            }
        } while (!terminate);
    }
}
