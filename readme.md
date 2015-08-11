# Net4Care XDS Connector

## Build
 - Clone project from Bitbucket:  
   `git clone https://bitbucket.org/4s/net4care-xds-connector.git`
 - Compile and run tests:  
   `mvn install`

## Deploy
Only maintainers of the repository should do this, provided here for reference

 - Add artifactory credentials to *$HOME/.m2/settings.xml*
 - Verify that the pom.xml has the proper version number and that changes.md is updated
 - Execute: `mvn deploy`
