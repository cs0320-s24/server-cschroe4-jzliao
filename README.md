> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
### Project Name: Server

### Team members:
- Caden Schroeder (cschroe4): Estimated 20 hours spent on project
- Jennifer Liao (jliao): Estimated 20 hours spent on project
### Link to Repo:
- https://github.com/cs0320-s24/server-cschroe4-jzliao

# Design Choices
### Dependency injection
- Classes implementing tactic: ACSHandler, CacheBroadbandDatasource, Server, Parser
- **ACSHandler** takes in a class that implements ACSDatasource. As such the developer
can present any type of data to the server and can decide where they wish for their
data comes from as long as their data source correctly implements out ACSDatasource class.
The ACSDatasource interface simply includes a sendRequest method that ACSHandler can 
call upon to fetch any data that will be presented to our API.
- **CacheBroadbandDatasource** wraps a ACSDatasource<Broadband> allowing it to work with a
multitude of basic datasource that deal will with Broadband data. For example, one
can take advantage of this design in testing to pass in a mock datasource while
using the same class in their real implementation to pass in a real ACS datasource.
- **Server** uses a parameter for CSVDatasource to represent the csvState. In the case
that one wishes to use a different CSV source than local, they can pass in their own
CSVDatasource that matches it. Ex: Revieving CSV data from a string, a stream, or an online
source. In each of the cases they must be able to get CSVData when it has been parsed.
- **Parser** takes in a class that implements CreatorFromRow which will handle the
  creation of the desired row type. As such, a developer can choose exactly how the row 
will be created;

### Strategy Pattern
- Classes/interfaces implementing tactic: ACSHandler, ACSDatasource, Parser
- **ACSHandler** is designed for the developer to be able to define the
  type of data that they wish to display on the server with handle. By
  entering a type/class in the place of T 
- **ACSDatasource** interface requires a type for T to be defined before it is instantiated,
allowing for any data type to be returned by a class that implements it. This leaves the interface
generic until its specified implementation and as such allows the ACSHandler to present anytype of data.
- **Parser** is left as generic as possible for future developers
  and when instantiated can be told what type to output the parsed rows should be
through passing in a type/class for T.

### Polymorphism
- Interfaces using tactic: ACSDatasource, CSVDatasource
- Both CacheBroadbandDatasource and BroadbandDatasource implement ACSDatasource
allowing for them to be interchangeable in the ACSHandler class. 
- Similarly, CSVDatasource allows the classes that implement it to be interchangeable in
the SearchHandler class.

# Errors/Bugs
- None known

# Tests
## Server
### ACS functionalities
- ACS functionalities are tested in the TestServerACS class.
- The majority of these tests use a mock datasource to avoid hitting the API every time
  - Before each test we create a mock datasource, ACSHandler, and set the broadband endpoint. Also we create adapters
- After each test we make sure to teardown the endpoints and disconnect any
HttpURLConnections.
- We test for:
  - basic broadband search queries
  - exceptions being thrown by improper inputs
  - caching (entering, exiting and bringing data from)
  
### CSV functionalities
-  CSV functionalities are tested in the TestServerACS class
- We test for:
  - basic loadcsv, viewvcsv, and searchcsv API queries
  - exceptions being thrown by improper inputs
  - viewing or searching before loading
  - loading multiple times
  - searching for multiple rows
  - searching multiple times

# How to
- Run the tests by pressing the green play button next to the method
or at the top of the testing classes to run all

- To build the program run `mvn package`
- To run the program use `./run` or press the green play button
- Once the server has started follow the printed link

Running CSV endpoints:
- Enter the desired endpoints with parameters: 
  - loadcsv with filename and hasHeader
    - The CSV file must be within the /data/ directory
    - Enter `true` or `false` or `yes` or `no` for if a header is present
  - viewcsv: after hitting the loadcsv endpoint
  - searchcsv with searchTerm and the identifier
    - identifier must be a column number or a header name (if header was included)
    - identifier can be specified as * to search all columns
Running ACS endpoint (broadband)
- Enter the broadband endpoint, specifing the stateName and countyName parameters
  - These parameters cannot be left blank