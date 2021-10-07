# Scheduling-Service

## About

This project implements an HTTP server that mainatins a database of users and appointments.

********************************************************************************
## Development and test

The project was developed with spring boot version 2.5.5, and tested on Windows 11 (Home Insider Preview) and IntelliJ IDEA 2021.2.2 .

In order to test the service, open the main folder with IntelliJ IDEA, run the project, import "Testing Schedule Service.postman_collection.json" in Postman, and run the test HTTP requests in order.

********************************************************************************

## Rest API endpoints

Note: in all requests, "AcceptVersion=v1" should be added to the header.


/users [GET] **(get all users)**

/users/{id} [GET] **(get user(id)**

/users/newuser" [POST] **(create a new user)**

/users/{id} [PUT] **(update user(id))**
    
/users/{id} [DELETE] **(delete user(id))**
    
/appointments [GET] **(get all appointments)**

/users/{ruid}/appointments [GET] **(get all appointments, where user(ruid) is a participant)**

/users/{ruid}/appointments/newappointment [POST] **(user(ruid) creates an appointment)**
 
/users/{ruid}/appointments/{appid} [PUT] **(user(ruid) updates appointment (appid))**

/users/{ruid}/appointments/{appid} [DELETE] **(user(ruid) deletes appointment(appid))**

/users/{ruid}/appointments/{appid}/users/{uid} [PUT] **(user(ruid) adds user(uid) to appointment(appid) as a participant)**

/users/{ruid}/appointments/{appid}/users/{uid} [DELETE] **(user(ruid) deletes user(uid) from appointment(appid))**
