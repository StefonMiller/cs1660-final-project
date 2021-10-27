FROM openjdk:8
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
ENV GOOGLE_APPLICATION_CREDENTIALS="cs1660-final-project-330314-d75d36291df2.json"
RUN curl -sSL https://sdk.cloud.google.com | bash
RUN javac LocalApp.java
CMD ["java", "LocalApp"]