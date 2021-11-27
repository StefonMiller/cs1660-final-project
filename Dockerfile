FROM openjdk:17-jdk-oraclelinux7
COPY LocalApp.jar /usr/src/
COPY cs1660-final-project-330314-9fdcd0afe32b.json /usr/src/
WORKDIR /usr/src
ENV GOOGLE_APPLICATION_CREDENTIALS="cs1660-final-project-330314-9fdcd0afe32b.json"
ENV DISPLAY=172.26.32.1:0.0
RUN yum install -y \
    libXext.x86_64 \
    libXrender.x86_64 \
    libXtst.x86_64
CMD ["java", "-jar", "--enable-preview", "LocalApp.jar"]