package Manager;

import Utils.Constant;
import Utils.ReviewJob;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.List;

public class LocalAppHandlerTask implements Runnable {
    private Database database;
    private SqsClient sqs;
    private String job_queue_name;
    private String local_app_name;
    private S3Client s3;

    public LocalAppHandlerTask(String job_queue_name, String local_app_name) {
        database = Database.getInstance();
        sqs = SqsClient.builder().region(Constant.region).build();
        s3 = S3Client.builder().region(Constant.region).build();
        this.job_queue_name = job_queue_name;
        this.local_app_name = local_app_name;

    }
    @Override
    public void run() {
        System.out.println("Hello i'm the local handler for local app: " + local_app_name);
        String jobQueueUrl = Sqs_handler.getQueueUrl(sqs, job_queue_name);
        do {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(jobQueueUrl)
                    .waitTimeSeconds(20)
                    .build();
            List<Message> messages = sqs.receiveMessage(receiveRequest).messages();
            for (Message m : messages) {
                if(!m.body().startsWith("job is done")) {
                    String[] messageArray = m.body().split(",");
                    database.localToBucketMap.computeIfAbsent(local_app_name, k -> messageArray[0]);
                    ReviewJob job = getFileFromBucket(s3, messageArray[0], messageArray[1]);
                    if (job != null) {
                        System.out.println("job in not null");
                        database.jobToLocalApp.put(job, local_app_name);
                        database.jobsQueue.add(job);
                    }
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(jobQueueUrl)
                            .receiptHandle(m.receiptHandle())
                            .build();
                    sqs.deleteMessage(deleteRequest);
                    System.out.println("We have new job, let's get working. I delete the message :)");
                }
            }
        } while (!database.terminateLocalThreads.get());
        database.localToThreadMap.remove(local_app_name);
    }

    private static ReviewJob getFileFromBucket(S3Client s3, String bucket, String file_name) {
        String new_file_name = "moshe" + System.currentTimeMillis();
        s3.getObject(GetObjectRequest.builder().bucket(bucket).key(file_name).build(),
                ResponseTransformer.toFile(Paths.get(new_file_name)));
        try {
            FileInputStream fileInputStream
                    = new FileInputStream(new_file_name);
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            ReviewJob p2 = (ReviewJob) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            File file = new File(new_file_name);
            file.delete();
            return p2;
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

}
