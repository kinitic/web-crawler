Web Crawler
===========

##Introduction
The Web crawler uses Spring MVC running on Tomcat.

A web page allows the user to enter a fully qualified domain that they would like crawled.

###Running the app


#### Remotely
I have deployed a working version of the application to heroku.
To hit the web application, use the following url:
`https://blooming-cove-55786.herokuapp.com/crawler`

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


### Assumptions

- Tomcat is installed
- Maven is installed
- Only domains that end in .com or .co.uk are allowed. (A trailing slash is optional)
- An internal link within the domain will only ever be crawled once, regardless of how many times it appears on different pages.




