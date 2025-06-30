### Introduction
This is a demo REST API project to read and add posts by users. This project uses core Java primarily along with jersey2 for REST APIs adn gson for JSON parsing. 
For simulating backend, the project uses GoREST APIs.

### API Documentation
API documention is available through Swagger at the following location. 
[API Documentation](http://localhost:8080/awarex/swagger/)

### Unit Testing
Further `src/test/resources/Awarex-API.postman_collection.json` contains all the postman test cases for unit testing

### Performance Testing
Classes `TestCreateUserPost` and `TestGetUserPostDetails` under `src/test/java` package are used to test the performance of the APIs

### Assumptions
- The GoREST backend APIs are paginated by nature. But in this example, I have assumed they are not for simplicity's sake. If we need to get all data at once, we need to iteratively invoke the same API multiple times which wouldn't be ideal. This would impact stats like posts without users or users without posts.
