# Net4Care XDS Connector

The XDS Connector is a standalone component for interfacing *Cross Enterprise Document Sharing* (XDS) sources, 
which follows the Danish profiling of XDS metadata version 0.95.

For more information see the [wiki page]([http://wiki.4s-online.dk/doku.php?id=net4care:xds-connector:overview).

## Governance
The project is governed by 4S, with source control on [Bitbucket](https://bitbucket.org/4s/net4care-xds-connector)
and issue tracking in [JIRA](https://issuetracker4s.atlassian.net/browse/NXC).

## Build
 - Clone project from Bitbucket:  
   `git clone https://bitbucket.org/4s/net4care-xds-connector.git`

 - Compile and run tests:  
   `mvn install`

## Develop

The library is a [Spring boot](http://projects.spring.io/spring-boot) application, which offers two Spring beans
*xdsRegistryConnector* and *xdsRepositoryConnector*, that expose methods for interacting with XDS.

The registry connector offers the following method:  
  - *queryRegistry* for querying the registry for documents for the given patient.

The repository connector offers the following methods:  
  - *retrieveDocumentSet* for retrieving a document from XDS with the given document id.  
  - *provideAndRegisterCDADocument* for providing and registering a CDA document to XDS. 
 The method picks the XDS metadata from the given CDA document.

The library can either be built and installed on the local system, or picked from the 4S maven repository: [http://artifactory.4s-online.dk/artifactory/net4care](http://artifactory.4s-online.dk/artifactory/net4care).

### Java/Maven
  - In *pom.xml* add:
```
#!xml

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>  
      <groupId>org.net4care</groupId>  
      <artifactId>xds-connector</artifactId>  
    </dependency>

    ...

    <repository>
      <id>4S-artifactory</id>
      <name>4S Artifactory</name>
      <url>http://artifactory.4s-online.dk/artifactory/net4care</url>
    </repository>
```

### Grails
  - In *BuildConfig.groovy* add the library as a dependency:  

```
#!groovy

    repositories {
      ...
      mavenRepo name: "4S", root: "http://artifactory.4s-online.dk/artifactory/net4care"
    }

    dependencies {
      ...
      compile("org.net4care:xds-connector:0.0.9")
    }
```
  - In *Config.groovy* add the library as a Spring bean package:
```
#!groovy

    grails.spring.bean.packages = ["org.net4care.xdsconnector", ...]
```

### Implementation Details

The component generates classes for the interaction with XDS using the
[Maven JAXB2 plugin](https://java.net/projects/maven-jaxb2-plugin/pages/Home)
based on the WSDL and XML schema files.

### Test

There is some unit testing of the *SubmitObjectsRequestHelper* to compare the output to the Danish XDS Metadata profile.
Besides that, there is a *TestApplication* which allows for mocking up tests in the console, and *TestXds* unit tests,
which allows for mocking up unit tests for interacting with XDS.

The properties for configuring the XDS connections for test are in the test resources.

## Deploy
Only maintainers of the repository should do this, provided here for reference

 - Add artifactory credentials to *$HOME/.m2/settings.xml*
 - Verify that the pom.xml has the proper version number
 - Execute: `mvn deploy`