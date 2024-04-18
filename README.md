# Fetch

## Generic integration of Laces, Relatics and BIM Portal

## Structure

The project consists of 4 modules:

* add-on-webapp: This module contains the front-end code for the part that is integrated into Relatics
* admin-webapp: This module contains all functionality to define the configurations, this part is not integrated into Relatics
* authentication: This module contains the login page
* backend: The back-end module takes care of storing all configurations, fetching information from the SPARQL endpoints and loads the data into Relatics

## Development Environment

### MongoDB

For development a local MongoDB server works quite well. If you run it in a Docker container, you're up and running in a few minutes.

```bash
docker run -d --name mongo -v "$(pwd)/.local/mongo/data/:/data/db" -p 27017:27017 mongo:5.0
```

The `--name mongo` part is required if you want to run the application in a docker container. The name is also used in command below; if you change it, make sure to also change it when using later commands.
The `-v "$(pwd)/.local/mongo/data/:/data/db` part is to make sure your data gets persisted on your local drive, so you don't have to repeat the import every time you stop your mongo container.

#### Using mongo CLI

Once the mongo container is running, you can us the mongo command to interact with the database directly:

```bash
docker container run --rm -ti -v "$(pwd)/.local/mongo/:/home/mongodb/" --link mongo mongo:5.0 mongo --host mongo
``` 

This command also uses the mongo:5.0 Docker image so you do not have to have the Mongo client installed locally. After exiting the container will be removed automatically (`--rm`). The `--link` refers to the container name used for the actual MongoDB server above, this name is also used as the `--host` parameter for the `mongo` command.

#### Create credentials

To directly insert a use into the database, you can use the following command in mongo:

```mongo
use fetch
db.users.insert({"username" : "...", "password" : "..."})
```

First the shell switches to "fetch" database. The second command inserts a user into the users collection. The password should be encoded using [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt). An online tool for generating passwords is [https://www.browserling.com/tools/bcrypt](https://www.browserling.com/tools/bcrypt) using 12 rounds.

#### Importing and exporting data

To perform a full dump of the MongoDB database, you can execute the following command

```bash
docker container run --rm -v "$(pwd)/mongodb/exports/:/data/" --link mongo mongo:5.0 mongodump --host mongo -d fetch --archive=/data/fetch.agz --gzip
```

Conversely, in order to fully restore a previous dump archive, use

```bash
docker container run --rm -v "$(pwd)/mongodb/exports/:/data/" --link mongo mongo:5.0 mongorestore --host mongo --drop --archive=/data/fetch.agz --gzip
```

### Front-end applications

The front-end applications can be started using `yarn start`. The application will start and the application will be available at http://localhost:3000/ .
To access data from the backend, a proxy was added to package.json to translate the relative URL's to absolute URL's for the back-end.

In production, this construction is not required since the static files will be hosted from the same domain as the backend API. After packaging
the static content in the backend project, the url's would be http://\<host>:\<port>/admin/ and http://\<host>:\<port>/add-on/.

### Backend application

#### Local development (from IDE)

To start the container locally, run `com.semmtech.laces.fetch.configuration.LacesFetchBackendApplication` from your IDE (either run or debug mode).

#### Maven (`spring-boot:run`)

Run application using maven:

```
mvn package spring-boot:run -Dspring.profiles.active=https,httpslocal
```


## Disclaimer

The code within this project is offered as-is.
