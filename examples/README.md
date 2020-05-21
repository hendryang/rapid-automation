## Setup
- First download and install **nodejs** in your machine: https://nodejs.org/en/download/
- Open cmd and cd to `./examples/todo-app` folder
- Run the following command in cmd:
    ```sh 
    node server.js
    ```
- This wil bring up the webserver that hosting simple todo application

## Accessing Todo Application
| Description | URL |
| ------ | ------ |
|Todo app | http://localhost:3000 |
| Swagger Doc | http://localhost:3000/api-docs |

## Running Sample API Cucumber Test Cases
- cd to `./examples/todo-app-api-test` folder
- Run the following command in cmd:
  ```sh 
    mvn clean test -am
  ```