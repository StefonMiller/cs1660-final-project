FROM openjdk:8
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN javac LocalApp.java
CMD ["java", "LocalApp"]