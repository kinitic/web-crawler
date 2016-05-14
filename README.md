Web Crawler
===========

##Introduction
The application that I have created is using Spring MVC stack running on Tomcat.

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

Also, need to add validation so that only domains are allowed to be entered.




