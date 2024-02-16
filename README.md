> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
### Project Name: Server

### Team members:
- Caden Schroeder (cschroe4): Estimated ___ hours spent on project
- Jennifer Liao (jliao): Estimated ___ hours spent on project
### Link to Repo:
- https://github.com/cs0320-s24/server-cschroe4-jzliao

# Design Choices
### Dependency injection
- Classes implementing tactic:

### Strategy Pattern
- Classes implementing tactic:

# Errors/Bugs
- None known

# Tests

# How to


# Running TODOS:
- IMPORTANT: DISCONNECT CLIENT CONNECTION IN ALL SERVERCSV TESTS
- add headers boolean to loadcsv parameters
- make LocalCSVSource safer (maybe do the copy thing from lecture so dataset is not exposed)
- edit parser to trim the \
- discuss how to put dataset into map (all at once or for each row?)
- add identifier handling in SearchHandler