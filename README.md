# ElasticSearch-index-Deletion-Java
Java Application to delete the index files of Elastic Search

In this project, it will connect to the elasticsearch and will look for the logs of specfic prefix. Once found, It will delete the logs of that index, that are older than two days. It has a CRON Schedule that runs after every 1 day. No need to use Curator or any other scritps. Just run this service in the docker container using JAR file and it will start working.