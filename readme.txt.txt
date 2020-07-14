SUMMARY
================================================================================
The application is composed of a local application and instances running on the Amazon cloud.
The application will get as an input a text file containing a list of URLs of PDF files with an operation to perform on them.
Then, instances will be launched in AWS (workers). Each worker will download PDF files, perform the requested operation, and display the result of the operation on a webpage.

================================================================================
How to run the code:
1. Create maven project named "Worker" and replace java directory and pom.xml file with those we supplied.
2. Create maven project named "Manager" and replace java directory and pom.xml file with those we supplied.
3. Create Worker and Manager jars (in Manager project chose Manager as main).
4. Create bucket in S3 called "the-gabay-jar" and upload Worker.jar and Manager.jar to it. We comment out the code to upload jars in localApp because it will take long time but it's there.
5. Delete Manager.jar from out directory in Manager project and create new jar for localApp (same as Manager but chose localApp as main, then change the name to localApp.jar).
6. Run cmd with $java -jar /path_to_jar/localApp.jar <file_1_path> <file_2_path> ... <file_n_path> N terminate
**if you don't want to upload the jars to s3 by yourself, put the Manager and Worker jars in the same directory as localApp.jar and in Manager project in file localApp.java uncomment the line S3_hanlder.upload_jars_to_s3();
We use this setup to our run:
Manager instance type: T2.Medium + ami-076515f20540e6e0b (Image we create).
Morker instance type: T2.Small + ami-076515f20540e6e0b. 
This run take ~6 min (N=100) or ~7 min (N=150)


