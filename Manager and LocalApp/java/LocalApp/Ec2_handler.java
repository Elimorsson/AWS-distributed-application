package LocalApp;

import Utils.Constant;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public class Ec2_handler {

    static void terminateManager(Ec2Client ec2) {
        System.out.println("Terminate Manager");
        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    for (Tag tag : instance.tags())
                        if (tag.value().equals(Constant.MANAGER)) {
                            TerminateInstancesRequest terminateRequest = TerminateInstancesRequest.builder()
                                    .instanceIds(instance.instanceId()).build();

                            ec2.terminateInstances(terminateRequest);
                            break;
                        }
                }
            }
            nextToken = response.nextToken();


        } while (nextToken != null);
    }
}
