Web Crawler
===========

##Introduction
The Web crawler used Spring MVC running on Tomcat.

A webpage is presented that allows to user to enter a fully qualified domain that they would like crawled.

###Running the app


#### Remotely
I have deployed a working version of the application to heroku.
To hit the web application, use the following url:
`https://intense-peak-58864.herokuapp.com/company/`

- Then enter the domain to be tested. This may take a few minutes.

#### Locally
The web application can be run locally. I generally use IntelliJ for my deploy and tomcat runners but you can also create the war locally using maven.

- Run `mvn package` in the project's root folder.

- Then run
  `java -jar target/dependency/webapp-runner.jar target/*.war`

- Once tomcat starts up, hit the url:
  `http://localhost:8080/crawler`

- Then enter the domain to be tested. This may take a few minutes.


### Testing

#### Unit tests
The location of the unit tests are in the expected `test/java` folder.

These will also have been run from the `mvn package command`.

They can also be run from within the IDE.

Also, need to add validation so that only pure domains are allowed to be entered.
eg. http://www.kinitic.com shpuld be accepted; but http://www.kinitic.com/info should NOT

### Assumptions

- Tomcat is installed
- Maven is installed

- An internal link within the domain will only ever be crawled once, regardless of how many times it appears on different pages.




