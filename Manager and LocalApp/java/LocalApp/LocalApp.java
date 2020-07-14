package LocalApp;

import Utils.Constant;
import Utils.ReviewJob;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class LocalApp {
    public static String N;

    public static void main(String[] args) throws InterruptedException {
        int index;
        boolean terminate = false;
        if(args[args.length-1].equals(Constant.TERMINATE)) {
            terminate = true;
            index = args.length - 2;
            N = args[index];
        }
        else {
            index = args.length-1;
            N = args[index];
        }
        String app_name = "LocalApp" + System.currentTimeMillis();
        String job_queue_name = "JobQueue_" + app_name;
        final String bucket = "bucket" + System.currentTimeMillis();
        ReviewJob job = Job_handler.create_review_job(args, index);
        Ec2Client ec2 = Ec2Client.builder().region(Constant.region).build();
        S3Client s3 = S3Client.builder().region(Constant.region).build();
        SqsClient sqsClient = SqsClient.builder().region(Constant.region).build();
        //S3_handler.upload_jars_to_s3(s3);
        run_manager_if_not_running(ec2, sqsClient);
        Sqs_handler.createJobQueue(sqsClient, job_queue_name);
        String file_name = S3_handler.upload_job_to_s3(s3, job, bucket);
        Sqs_handler.sendRegistrationMessage(sqsClient, app_name, job_queue_name, N);
        Sqs_handler.sendJobMessage(sqsClient, bucket, job_queue_name, file_name);
        if(terminate) {
            System.out.println("Terminate system");
            Thread.sleep(30000);
            Sqs_handler.sendterminationMessage(sqsClient);
        }
        boolean get_ans = false;
        while(!get_ans){
            System.out.println("Waiting for result");
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(Sqs_handler.getQueueUrl(sqsClient, job_queue_name))
                    .waitTimeSeconds(20)
                    .visibilityTimeout(1)
                    .build();
            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            for (Message m : messages) {
                if(m.body().startsWith("job is done")){
                    get_ans = true;
                    String file_to_get = m.body().split(":")[1];
                    s3.getObject(GetObjectRequest.builder().bucket(bucket).key(file_to_get).build(),
                            ResponseTransformer.toFile(Paths.get(file_to_get)));

                }
            }

        }
        System.out.println("Finished. your grade is: 100");
    }

    private static void run_manager_if_not_running(Ec2Client ec2, SqsClient sqsClient) throws InterruptedException {
        System.out.println("Check if manager exist and create one if not");
        String nextToken = null;
        boolean manager_is_running = false;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    for(Tag tag : instance.tags())
                        if(tag.value().equals(Constant.MANAGER)) {
                            manager_is_running = true;
                            break;
                        }
                }
            }
            nextToken = response.nextToken();


        } while (nextToken != null);
        if(!manager_is_running) {
            System.out.println("Creating manager and registration queue");
            HashMap<QueueAttributeName, String> attributes = new HashMap<>();
            attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(Constant.REGISTRATION_QUEUE)
                    .attributes(attributes).build();
            sqsClient.createQueue(createQueueRequest);
            CreateEc2Instance.create_instance(Constant.MANAGER, Constant.AMI_ID_WORKER, ec2);
            Thread.sleep(10000);
        }
        System.out.println("Manager is running");
    }

}
