# Maritime Vessel Info App

## Reference Documentation

The Maritime Vessel Info App is a web application that provides information about maritime vessels.
It demonstrates the use of Spring Web Flux  and Reactive Streams to handle non-blocking I/O operations
over a Cassandra Database. 


## Getting Started
To get started with the Maritime Vessel Info App, follow these steps:

1. You need to have Java 21 or higher installed on your machine.
2. You need to have Gradle installed on your machine.
3. You need to have Cassandra installed on your machine (you can run one via docker compose).

### Cassandra Setup
To set up Cassandra, you can use the following Docker Compose file:

```yaml
# docker-compose.yml
version: '3.8'

services:
  cassandra:
    image: cassandra:latest
    ports:
      - "9042:9042"
    environment:
      - CASSANDRA_CLUSTER_NAME=maritime_cluster
      - CASSANDRA_DC=dc1
    volumes:
      - cassandra_data:/var/lib/cassandra

volumes:
  cassandra_data:
```

To start Cassandra, run the following command in the directory where the `docker-compose.yml` file is located:

```bash
docker-compose up -d
```

### Database Setup
Once Cassandra is running, you need to create the keyspace and table for the application. You can do this by running the following CQL commands:

```cql
CREATE KEYSPACE maritime WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
```

There is no reason to create the table manually, as the application will create it automatically when it starts.


###  Running the Application

To run the application, navigate to the root directory of the project and run the following command:

```bash
./gradlew bootRun
```

This will start the application on port 8080. You can access the application swagger UI at `http://localhost:8080/swagger-ui/index.html`
where all the requested endpoints are located.

