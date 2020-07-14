package LocalApp;

import Utils.Constant;
import Utils.ReviewJob;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;

public class S3_handler {
    static String upload_job_to_s3(S3Client s3, ReviewJob job, String bucket) {
        System.out.println("Uploading file to S3");
        createBucket(bucket, s3);
        String file_name = "file" + System.currentTimeMillis();
        try {
            File file = new File(file_name);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(job);
            s3.putObject(PutObjectRequest.builder().bucket(bucket).key(file_name).build(), Paths.get(file_name));
            fileOutputStream.close();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Uploading file name:" + file_name + " to bucket:" + bucket);

        return file_name;
    }

    static void upload_jars_to_s3(S3Client s3) {
        createBucket("the-gabay-jars", s3);
        s3.putObject(PutObjectRequest.builder().bucket("the-gabay-jars").key("Worker").build(), Paths.get(Constant.WORKER_JAR));
        s3.putObject(PutObjectRequest.builder().bucket("the-gabay-jars").key("Manager").build(), Paths.get(Constant.MANAGER_JAR));
    }

    private static void createBucket(String bucket, S3Client s3) {
        s3.createBucket(CreateBucketRequest
                .builder()
                .bucket(bucket)
                .build());

        System.out.println(bucket);
    }

    static void emptyAndDeleteBucket(S3Client s3, String bucket) {
        System.out.println("Emptying and deleting bucket ");
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket).build();
        ListObjectsV2Response listObjectsV2Response;
        do {
            listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(s3Object.key()).build());
            }

            listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket)
                    .continuationToken(listObjectsV2Response.nextContinuationToken())
                    .build();

        } while (listObjectsV2Response.isTruncated());
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
        s3.deleteBucket(deleteBucketRequest);
    }
}
