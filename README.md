# About
This application job is to read a txt file that contains timestamps for logs creation, flag ones that extend the provided time and move them into the database.

## How to build
In project folder issue a console command `gradlew build` the result jar file should be located in `build/libs/` directory

## How to run
- Build the project if not done previously
- Run the java file with `java -jar {jar file} {txt file to parse}`  
for ex `java -jar build/libs/app-0.0.1-SNAPSHOT.jar C://logs/testLogs.txt`


## Requirements
- Java 8
- Gradle

## Configuraton
You can modify properties inside of [application.properties](https://github.com/wezik/log-parser/blob/main/src/main/resources/application.properties#L10-L21) file to improve performance or change the flag time.

## Additional info
- application works with large files (tested with maximum of 4GB)
- txt file can be a one liner
- if the provided file doesn't exist or is of incorrect format the application will loop arround asking you for new one.
