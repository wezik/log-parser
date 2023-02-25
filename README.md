# About
This app processes a large txt file containing JSON-formatted log creation events with timestamps. The app collects unsorted events, identifies those that exceed a specified time limit, and moves them to a database for storage.

## How to build
To build the project, navigate to the project folder in a console and run the command `gradlew build`. The resulting jar file should be located in the `build/libs/` directory.

## How to run
To run the application, follow these steps:

1. Build the project if not done previously
2. Run the java file with `java -jar {jar file} {txt file to parse}`  
example, `java -jar build/libs/app-0.0.1-SNAPSHOT.jar C://logs/testLogs.txt`.


## Requirements
The following are required to run this application:
- Java 8 or higher
- Gradle

## Configuraton
You can modify properties inside of [application.properties](https://github.com/wezik/log-parser/blob/main/src/main/resources/application.properties#L10-L21) file to improve performance or change the flag time.

## Additional information
Here are some additional details about this application
- It uses multithreading to process files faster
- The application displays live progress of processing the file, giving you real-time updates on how much of the file has been parsed.
- It can handle large files (tested up to 4GB)
- The txt file can be a one-liner, making it easy to provide input to the application.
- if the provided file doesn't exist or is of incorrect format the application will prompt you to provide a new one.
