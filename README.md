File Storage requirements
-------------------------

Implement REST web-service. Functionality:
* Upload file and save it in the file system (it must return file ID)
* Keep file: user can download the file using file ID
* Get a list of files available for downloading (name + ID)
* Create new user: login/password
* Files uploaded by specific user are available only for him
* Get a list of files available for a specific user
* Share file with a specific user

You are free to choose any tools/libraries you like.
For example you can use: 
* Spring Boot: https://spring.io/projects/spring-boot
* Hibernate ORM: http://hibernate.org
* Spring-Swagger integration for REST: http://springfox.github.io/springfox

Installation
------------
* You should configure **file_store.path.root** (last line) into the [application.yml](src/main/resources/application.yml) file. 
This path should point to the directory in the local file system, 
where you are planning to store uploaded file (this folder should have appropriate permissions).
* To run the application, please use [run.sh](run.sh) script (It works only in Linux bases scripts, for a start on Windows you can use command from this script directly)
* Every time when the system restarts user's files are lost (used in-memory DataBase) 
* Login page: http://localhost:8080/login  
* System has two users [data.sql](src/main/resources/data.sql):

| **username** | **password** | 
|--------------|--------------|
| user1        | user_1       |
| user2        | user_2       |

Software testing tasks
----------------------
* Analyze the requirements and describe all test cases
* Check which cases are already covered by automatic tests
* Add missing automatic tests
* Report all bugs/gaps you can find (steps to reproduce... etc.)

Please sign in (or register https://gitlab.com/users/sign_in#register-pane) on gitlab 
and put all bugs/gaps as issues https://gitlab.com/stden/file-storage-qa/issues

Put all you code changes into new branch.

Good luck ;)