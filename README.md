Implementation of web searcher based on requirements specified on page below
https://s3.amazonaws.com/fieldlens-public/Website+Searcher.html

# I. Overview
Application flows as follow (Thread Pool related calls are omitted for brevity) 
```
Main -> load file with links
     -> extract Urls 
     -> create callback object (PageContentSearcher class) 
     -> creates Controller with injected callback  
     Controller -> create tasks and queue in thread pool
                -> uses HttpConnectionManager to create URL connection for generated links (HTTP protocol)
                -> uses UrlReader to load contents of the link
                -> uses callback (PageContentSearcher class) to search loaded contents for provided search terms 
                -> awaits for all tasks to be completed 
     -> uses ReportWriter to create and write results to results file (see VII for file format )       
     -> exits the program
```
# II Building and running with installed JDK and Maven
(See item III. in case if java is not installed on this machine )    
Prerequisites 
- Java 8 
- Maven 3.3+

### Command to build
```
mvn clean package
```
### Commands to run application
#### a) change directory from application root to target 
```
cd target
```
#### b) execute java command
```
java -Xmx1g -jar website-searcher-1.0-SNAPSHOT.jar app.WebContentsSearcher
```
### Commands to run tests
#### a) change directory from application root to target 
```
cd target
```
#### b) execute java command to run all tests
```
java -cp test-classes;lib/takari-cpsuite-1.2.7.jar;lib/hamcrest-core-1.3.jar;lib/junit-4.12.jar;website-searcher-1.0-SNAPSHOT.jar org.junit.runner.JUnitCore RunAllTests 
```
# III. Building and running without pre-installed JDK   
Distribution of Java 8 must be located on PC
Note: I was unable to add JDK distribution to the project since github rejects any files > 100 mb 

Execute batch scripts 
### Command to build 
```
build
```
EX: build [Full path to JDK distribution location on PC]
### Commands to run application
```
run
```     
EX: run [Full path to JDK distribution location on PC]

**Note: If [Full path to JDK distribution location on PC] contains spaces wrap path in double quotes

# IV. App configuration
All parameters used in app.WebContentsSearcher configured in the 
file website-searcher\src\main\resources\config.properties

## Configurable properties used:
```
1) To control log level
     logger.level=FINE

2) To specify a link to load Urls for processing 
     web.link.to.urls=https://s3.amazonaws.com/fieldlens-public/urls.txt

3) To specify a thread pool size
     pool.size=20

4) To specify a thread pool queue's capacity
     pool.queue.capacity=50

5) Sleep time of worker thread
     pool.worker.thread.sleep.time=10

6) Time to wait for thread pool termination
     pool.await.termination.sleep.time=60000

7) Terms to be used to search on downloaded pages  
     web.page.content.search.terms=Moved,document,Region,scrollbar
```
# V. Results output
All matching results will be saved in the file: results_{timestamp}.txt 
```
Ex: results_2018-09-18T10-15-30.150.txt
```
Links that failed to load will have details of exception  

# VI. Logging   
Log file will be generated and saved in logs folder in the file websearcher_log_{timestamp}.txt with current timestamp
```
Ex: websearcher_log_2018-09-18T10-15-30.150.txt
```

# VII. Sequence diagrams
Refer to directory ./diagrams to see various parts of an application flow  
