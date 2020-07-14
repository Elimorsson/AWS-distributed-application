package Manager;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import Utils.Constant;

import java.util.HashMap;


public class Manager {

    public static void main(String[] args) {
       Ec2Client ec2 = Ec2Client.builder().region(Constant.region).build();
        SqsClient sqsClient = SqsClient.builder().region(Constant.region).build();
        Database database = Database.getInstance();
        createQueuesForWorkers(sqsClient);
        Thread registration_handler = new Thread(new RegistrationHandlerTask());
        registration_handler.start();
        Thread job_publisher = new Thread(new JobPublisherTask());
        job_publisher.start();
        Thread result_handler1 = new Thread(new ResultsHandlerTask());
        Thread result_handler2 = new Thread(new ResultsHandlerTask());
        Thread result_handler3 = new Thread(new ResultsHandlerTask());
        boolean firstTime = true;
        while(!database.terminateManager.get()) {
            int workers_we_have = database.workers.size();
            int workers_we_need = (database.reviewsToDo.get() / database.N.get()) - workers_we_have;
            if(workers_we_need > 0) {
                System.out.println("Workers we have: " + workers_we_have);
                System.out.println("Workers we need: " + workers_we_need);
                for (int i = 0; i < workers_we_need; i++) {
                    System.out.println("creating worker num: " + i);
                    CreateEc2Worker.create_instance("worker" + System.currentTimeMillis(), Constant.AMI_ID_WORKER, ec2);
               }
                if(firstTime) {
                    firstTime = false;
                    result_handler1.start();
                    result_handler2.start();
                    result_handler3.start();
                }
            }
        }
        Thread terminator = new Thread(new terminationTask());
        terminator.start();
        try {
            terminator.join();
        } catch (Exception e){

        }
        registration_handler.stop();
        job_publisher.stop();
        result_handler1.stop();
        result_handler2.stop();
        result_handler3.stop();
    }

    private static void createQueuesForWorkers(SqsClient sqsClient) {
        HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
        attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
        CreateQueueRequest createQueueRequest1 = CreateQueueRequest.builder()
                .queueName(Constant.TO_DO_QUEUE)
                .attributes(attributes).build();
        sqsClient.createQueue(createQueueRequest1);
        CreateQueueRequest createQueueRequest2 = CreateQueueRequest.builder()
                .queueName(Constant.RESULTS_QUEUE)
                .attributes(attributes).build();
        sqsClient.createQueue(createQueueRequest2);
    }

}
