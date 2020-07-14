package LocalApp;

import Utils.Constant;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;

public class CreateEc2Instance {

    public static void create_instance(String name, String ami_id, Ec2Client ec2) {
        RunInstancesRequest run_request = RunInstancesRequest.builder()
                .instanceInitiatedShutdownBehavior("terminate")
                .keyName("gabay")
                .imageId(Constant.AMI_ID_WORKER)
                .userData(Base64.getEncoder().encodeToString(Constant.USER_DATA_MANAGER.getBytes()))
                .iamInstanceProfile(IamInstanceProfileSpecification.builder().arn(Constant.WORKER_ARN).build())
                .instanceType(InstanceType.T2_MEDIUM)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(run_request);

        String instance_id = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("Name")
                .value(name)
                .build();

        CreateTagsRequest tag_request = CreateTagsRequest.builder()
                .resources(instance_id)
                .tags(tag)
                .build();

        try {
            ec2.createTags(tag_request);

            System.out.printf(
                    "Successfully started EC2 instance %s based on AMI %s",
                    instance_id, ami_id);
        }
        catch (Ec2Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done!");

    }
}
