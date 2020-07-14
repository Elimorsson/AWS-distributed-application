package Manager;

import Utils.Constant;
import Utils.Review;
import Utils.ReviewJob;
import javafx.util.Pair;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class EndOfJobTask implements Runnable {
    private Database database;
    private String job_name;
    private HashMap<Integer, String> color_map;

    public EndOfJobTask(String job_name) {
        this.database = Database.getInstance();
        this.job_name = job_name;
        color_map = new HashMap<>();
        color_map.put(0, "darkred");
        color_map.put(1, "red");
        color_map.put(2, "black");
        color_map.put(3, "lightgreen");
        color_map.put(4, "darkgreen");
    }

    @Override
    public void run() {
        S3Client s3 = S3Client.builder().region(Constant.region).build();
        SqsClient sqsClient = SqsClient.builder().region(Constant.region).build();
        ReviewJob job = database.jobNameTojob.get(job_name);
        String local_name = database.jobToLocalApp.get(job);
        String bucket = database.localToBucketMap.get(local_name);
        String queue_name = database.localToJobQueueMap.get(local_name);
        String htmlString = CreateHtmlString(job);
        String file_name = job_name + ".html";
        System.out.println("Creating answer file: " + file_name);
        try {
            File file = new File(file_name);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(htmlString);
            s3.putObject(PutObjectRequest.builder().bucket(bucket).key(file_name).build(), Paths.get(file_name));
            fileOutputStream.close();
            file.delete();
            SendMessageRequest send_msg_request = SendMessageRequest.builder()
                    .queueUrl(Sqs_handler.getQueueUrl(sqsClient, queue_name))
                    .messageBody("job is done:" + file_name)
                    .delaySeconds(5)
                    .build();
            sqsClient.sendMessage(send_msg_request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Uploading file name:" + file_name + " to bucket:" + bucket);
        database.jobNameTojob.remove(job_name);
        database.jobToLocalApp.remove(job);
        database.reviewsToDo.set(database.reviewsToDo.get() - job.getReviews());
    }

    private String CreateHtmlString(ReviewJob job){
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(Constant.HTML_FILE_PREFIX);
        for(Map.Entry<String, HashMap<String, Review>> entry : job.getTitleReviews().entrySet()) {
            HashMap<String, Review> title_map = entry.getValue();
            for (Map.Entry<String, Review> child_entry : title_map.entrySet()) {
                Review r = child_entry.getValue();
                String font_color = color_map.get(r.getNlpAnalysis());
                htmlString.append("<tr>\n" +
                        "    <td><font color=" + font_color +">" + r.getId() + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + r.getLink() + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + r.getTitle() + "</font></td>\n" +
                        "    <td width=300><font color=" + font_color +">" + r.getText() + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + r.getAuthor() + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + r.getRating().toString() + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + r.getDate().toString() + "</font></td>\n" +
                        "    <td width=500><font color=" + font_color +">" + getEntitiesString(r) + "</font></td>\n" +
                        "    <td><font color=" + font_color +">" + isSarcasiam(r) + "</font></td>\n" +
                        "  </tr>\n");
            }
        }
        htmlString.append(Constant.HTML_FILE_SUFIX);
        return htmlString.toString();
    }

    private String getEntitiesString(Review r){
        String entities = String.join(",", r.getEntities());
        return entities.substring(0, entities.length()-1);
    }
    private String isSarcasiam(Review r){
        if(r.getNlpAnalysis().intValue() == r.getRating().intValue())
            return "False";
        return "True";
    }

}
