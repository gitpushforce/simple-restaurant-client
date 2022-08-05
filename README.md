Author: Masaki Nakamura

# Simple Restaurant Client
* This app was developed using Java 11 as a gradle project.
* the ClientApp class has a variable called "domain", by default is pointing to the deployed restaurant-api in VPS server. If we want to make requests to localhost, then we should switch the domain variable to the localhost one by commenting out.
* The interface is a console screen.
* Supports Multi-threading requests.

## Build

At root directory

*Create jar file:  (client.jar)*

`    ./gradlew customFatJar
`

 (Since it's a gradle standalone project we need to build the dependencies using a fatJar instead of the normal "build")
    
*Execute jar file:*

`java -jar build/libs/client.jar
`

Other simple way to run the app is running the  ClienApp.java class using an IDE like IntelliJ IDEA.
(Updating gradle dependencies previously)

## Sample

Write the number of Threads (up to 10)

**1**

Select an action for thread 1

1. how all items in a table
2. Show specified order
3. create order
4. delete order

**3**


Select a table number

**2**

[Items available to order]

0001   0002   0003   0004  
0005   0006   0007   0008  
0009   0010   
Select an item id (e.g. 0003)

**0001**

[thread-1: http://153.121.71.32:8081/v1/add?table=2&item=0001]

The order was placed correctly


### Remark:
When multi-threading is used, the app will make all the calls at the same time after finishing to fill up all the questions for each thread.

The Api project repo is in the link below

`https://github.com/gitpushforce/simple-restaurant-api.git`