package Utils;

import software.amazon.awssdk.regions.Region;

public class Constant {
    public static final  String AMI_ID_WORKER = "ami-076515f20540e6e0b";
    public static final  String MANAGER = "manager";
    public static final  String TERMINATE = "terminate";
    public static final Region region = Region.US_EAST_1;
    public static final String REGISTRATION_QUEUE = "RegistrationQueue";
    public static final String TO_DO_QUEUE = "to_do_queue";
    public static final String RESULTS_QUEUE = "results_queue";
    public static final String WORKER_ARN = "arn:aws:iam::332193352153:instance-profile/EC2_gabay";
    public static final String HTML_FILE_SUFIX = "</table>\n" +
            "\n" +
            "</body>\n" +
            "</html>";
    public static final String HTML_FILE_PREFIX = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            "table, th, td {\n" +
            "\tborder: 1px solid black;\n" +
            "  \tborder-collapse: collapse;\n" +
            "}\n" +
            "th, td {\n" +
            "  \tpadding: 5px;\n" +
            "    text-align: left;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<h2>Reviews Summry</h2>\n" +
            "<p>Elimor cohen: 301727657 Or gabay: 301123436</p>\n" +
            "\n" +
            "<table style=\"width:250%\">\n" +
            "  <caption>Reviews</caption>\n" +
            "  <tr bgcolor = \"red\">\n" +
            "    <th>Id</th>\n" +
            "    <th>Link</th>\n" +
            "    <th>Title</th>\n" +
            "    <th>Text</th>\n" +
            "    <th>Author</th>\n" +
            "    <th>Rating</th>\n" +
            "    <th>Date</th>\n" +
            "    <th>Entities</th>\n" +
            "    <th>IsSarcastic</th>\n" +
            "  </tr>\n";
    public static final String USER_DATA_WORKER = "#!/bin/bash\n" +
            "sudo mkdir /home/ass/\n" +
            "sudo aws s3 cp s3://the-gabay-jar/Worker.jar /home/ass/\n" +
            "sudo /usr/bin/java -jar -Xmx1g /home/ass/Worker.jar\n" +
            "shutdown -h now";
    public static final String WORKER_JAR = "C:\\Users\\orgab\\IdeaProjects\\AWS_ASS1\\Worker\\out\\artifacts\\Worker_jar\\Worker.jar";
    public static final String MANAGER_JAR = "C:\\Users\\orgab\\IdeaProjects\\AWS_ASS1\\Manager\\out\\artifacts\\Manager_jar\\Manager.jar";

    public static final String USER_DATA_MANAGER = "#!/bin/bash\n" +
            "sudo mkdir /home/ass/\n" +
            "sudo aws s3 cp s3://the-gabay-jar/Manager.jar /home/ass/\n" +
            "sudo /usr/bin/java -jar /home/ass/Manager.jar\n" +
            "shutdown -h now";

}
