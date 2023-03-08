# BattleShip Backend API (G05)

### How to run? (Internal debug without docker)

## Instructions to run project (API Spring Boot) 

* 1.Install docker compose desktop: https://docs.docker.com/desktop/install/windows-install/
* 2.Start docker image with development time services
```
docker compose up --build --force-recreate 
```
* 3.Start shell on postgres container
```
docker exec -ti db-tests-battleship bash
```
* 4.Start `psql` inside postgres container
```
psql -U postgres -d battleship
```
* 5.Example of `psql` commands to check database state
```
  \h - show help. 
  \d <table> - show table.
  SELECT * FROM dbo.users;
  select ... ; - execute query.
  \q - quit psql.
```
* 6.Use DBeaver to check database content (easy way)
  * 6.1.Install DBeaver database manager: https://dbeaver.io/
  * 6.2.Create new database connection to localhost:5432
  * 6.3.Use username and password specified in /docker-compose.yml file

* 7.Start SpingBoot project with tomcat
  * 7.1 Start app with IDE RUN "BattleshipApplication.kt"
  * 7.2 Test HTTP paths with "Postman" app or equivalent
    * http://localhost:8080/api/public/
      * Result should be the public page of web api