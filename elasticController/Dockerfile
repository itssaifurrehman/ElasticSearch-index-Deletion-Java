#FROM java:openjdk-8-jdk
# Change the project_version value when artifact version changed.
#ENV project_version 0.0.1-SNAPSHOT
#ENV project_name elasticController
#COPY target/${project_name}-${project_version}-bin.tar.gz /opt/app/${project_name}-${project_version}-bin.tar.gz
#WORKDIR /opt/app
#RUN tar -zxvf ${project_name}-${project_version}-bin.tar.gz
#RUN ln -s /opt/app/${project_name}-${project_version} jetstreamapp
#WORKDIR /opt/app/jetstreamapp
#EXPOSE 9999 15590 15591 15592 15593 15594 15595 15596 15597 15598 15599
#ENTRYPOINT ./start.sh
FROM openjdk:8
ADD ./PATHTOFILE/elasticController.jar elasticController.jar 
EXPOSE 8096
ENTRYPOINT ["java", "-jar", "elasticController.jar"]