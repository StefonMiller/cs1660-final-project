# cs1660-final-project
Final project option 2 for CS1660
## Steps to connect to GCP
1. [Create a GCP service account](https://console.cloud.google.com/projectselector/iam-admin/serviceaccounts/create?supportedpurview=project)
2. After creating a service account, select it in the resulting window
3. Open the 'Keys' tab for the service account and click 'Create new key'
4. Download the key as a JSON file and move it to the same directory as the Dockerfile
5. Create an environment variable GOOGLE_APPLICATION_CREDENTIALS in the Docker container pointing to the JSON file. This environment variable will allow gcloud to automatically      authenticate with GCP when a ``gcloud`` command is executed from my Java application using the ``Runtime.exec()`` function
## Build/run commands for local application
1. RUN commands in Dockerfile: 
   * ``RUN curl -sSL https://sdk.cloud.google.com | bash`` will be used to install gcloud on the Docker container
   * ``RUN javac LocalApp.java`` will be used to compile and execute my Java file
2. Docker build command to create the container from my Dockerfile:
   * ``docker build -t smm248/local-app``
3. Docker run command used to create and start a container from the smm248/local-app image:
   * ``docker run -it smm248/local-app``, note I will be using the -it flag in order to interact with the container from the local terminal
