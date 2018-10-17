FROM frolvlad/alpine-oraclejdk8
# Create directory to hold the application and all its contents in the Docker image.
RUN mkdir /application
# Copy all the static contents to be included in the Docker image.
ADD /target/springboot-webapp.jar springboot-webapp.jar
ENTRYPOINT ["java","-jar","/springboot-webapp.jar"]