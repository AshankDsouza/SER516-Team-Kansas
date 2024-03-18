# SER516-Team-Kansas
## Taiga API Integration
This project is a Java application for interacting with the Taiga API to perform various task and calculating metrics.
## Setting up the application
### 0) Install prerequisite software
Install the following on your system:
java
maven

### 1) Clone the repository



   ```bash
 git clone https://github.com/ser516asu/SER516-Team-Kansas.git
   cd SER516-Team-Kansas
   ```

### 2) Compile and Run the spring boot application
Go to the project root and compile the Maven project
to install dependencies run:
```bash
mvn clean install -U
   ```
To run the backend application run

```bash
mvn spring-boot:run
or
mvn compile spring:boot-run
   ```
### 3) Open and new terminal Install dependencies and start the NextJS client


   ```bash
cd .\client\
npm install
npm run dev
   ```

### NOTE
In case you don't have Maven installed, please refer to following tutorial
https://phoenixnap.com/kb/install-maven-windows

### 4) Open browser with the following url http://localhost:3000 and login with Taiga credentials

### 5) Dockerization steps 
```mvn clean package ## Clean and build jar application along with docker image```<br>
```mvn clean package -Ddockerfile.skip # Clean and build the jar application```<br>
```docker run -p 8080:8080 kansas/taigaapi:0.0.1-SNAPSHOT #Command to run application interactively```<br>
```docker run -p 8080:8080 -d kansas/taigaapi:0.0.1-SNAPSHOT #Command to run application in background```
