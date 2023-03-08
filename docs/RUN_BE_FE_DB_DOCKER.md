## How to run all system in docker? 

### Instructions to run project (Backend + FrontEnd + Database)

* 1.Install docker compose desktop: https://docs.docker.com/desktop/install/windows-install/
* 2.Build .jar to docker, so, go to BackEnd folder at ./code/jvm/battleship and run:
```
 ./gradlew bootJar   
```
* 3.Next go to code base folder at ./code
* 4.Start docker image with development time services and wait...
```
docker compose up --build --force-recreate 
```
* 6.Check in docker desktop if these containers are created:
```
be-batleship
fe-batleship
db-batteship
```
* 6.Backend server is available at http://localhost:8080
* 7.FrontEnd server is available at http://localhost:8000