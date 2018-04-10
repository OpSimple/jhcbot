# Java hack.chat Bot
A simple bot for https://hack.chat/ in java


## Download
Download the latest executable jar [JHCBot v1.0.2](https://github.com/OpSimple/jhcbot/releases/download/v1.0.2/jhcbot-latest.jar)

### Sources
You can download source code releases from [Github Releases](https://github.com/OpSimple/jhcbot/releases)

### Building
This project is created with [Apache Maven](https://maven.apache.org/) so download and install it if you don't have it already.
Build executable jar files using 

    mvn clean package

This will produce the two jars:
 - **JHCBot-1.0-SNAPSHOT.jar** This is the executable jar without the dependencies packed within the jar. You must have all dependencies in the classpath to run the bot.
 - **JHCBot-1.0-SNAPSHOT-jar-with-dependencies.jar** This executable jar has all the dependencies mentioned in `pom.xml` already packed within the jar. You can run the bot directly with it although it is slightly heavier.
 
 ## Running
 First, create/modify the config file for the bot `jhcbot.xml` or `jhcbot.properties` with all the required entries with their appropriate values respectively (See `jhcbot.xml` file for the explanations and suggestions on the config values).
 Now, place the config file in the current directory/folder and proceed towards executing the jar. 
 
 Run the bot by executing any of the jars but its better to execute JHCBot-1.0-SNAPSHOT-jar-with-dependencies.jar cause it has all the dependencies in it. Be sure, you include all the dependencies mentioned in `pom.xml` if you are going to execute JHCBot-1.0-SNAPSHOT.jar
 
 To execute the jar and run the bot
 
    java -jar target/JHCBot-1.0-SNAPSHOT-jar-with-dependencies.jar
    
 Or
 
    java -jar <path to jar file>
 
 You can also provide the config file with its path while executing the jar
 
    java -jar <path to jar file> -f <path to config file>
 
 # License
 This project is under [MIT License](https://github.com/OpSimple/jhcbot/blob/master/LICENSE)
 
