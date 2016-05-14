Web Crawler
===========

##Introduction
The application that I have created is using Spring MVC running on Tomcat.

A webpage is presented that allows to user to enter a fully qualified domain that they would like crawled.

I have to caveat that the application is not complete, there are some teething troubles which will be cleared up via writing more unit tests.

###Running the app

#### Locally
The web application can be run locally. I generally use IntelliJ for my deploy and tomcat runners but you can also create the war locally using maven.

- Run `mvn package` in the project's root folder.

- Then run
  `java -jar target/dependency/webapp-runner.jar target/*.war`

- Once tomcat starts up, hit the url:
  `http://localhost:8080/crawler`

- Then enter the domain to be tested.


### Testing

#### Unit tests
The location of the unit tests are in the expected `test/java` folder.

These will also have been run from the `mvn package command`.

They can also be run from within the IDE.

A lot more tests need to be written.

Also, need to add validation so that only pure domains are allowed to be entered.
eg. http://www.kinitic.com shpuld be accepted; but http://www.kinitic.com/info should NOT

### Assumptions

- Tomcat is installed
- Maven is installed

- An internal link within the domain will only ever be crawled once, regardless of how many times it appears on different pages.




