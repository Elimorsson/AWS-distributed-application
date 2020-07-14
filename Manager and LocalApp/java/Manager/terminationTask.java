package Manager;

import Utils.Constant;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.sqs.SqsClient;

public class terminationTask implements Runnable {
    @Override
    public void run() {
        Database database = Database.getInstance();
        Ec2Client ec2 = Ec2Client.builder().region(Constant.region).build();
        SqsClient sqsClient = SqsClient.builder().region(Constant.region).build();
        while(!database.jobNameTojob.isEmpty()){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //kill all worker
        for(String worker : database.workers){
            TerminateInstancesRequest terminateRequest = TerminateInstancesRequest.builder()
                    .instanceIds(worker).build();
            ec2.terminateInstances(terminateRequest);
        }
    }
}
