FROM openjdk:8
COPY LocalApp.java /usr/src/myapp
WORKDIR /usr/src/myapp
ENV GOOGLE_APPLICATION_CREDENTIALS="cs1660-final-project-330314-d75d36291df2.json"
ENV DISPLAY=172.26.32.1:0.0
# RUN curl -sSL https://sdk.cloud.google.com | bash
RUN apt-get update && apt-get install -y \
    libxrender1 \
    libxtst6 \
    libxi6
RUN javac LocalApp.java
CMD ["java", "LocalApp"]