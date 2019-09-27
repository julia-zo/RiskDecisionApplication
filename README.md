# Risk Decision Application
REST Application to assess if a certain client has enough credit to make a financial transaction

_Author: Júlia Zottis Moreira_

## Available Endpoints

| Operation | Path | Description |
|-----------|------|-------------|
| POST | \<host>/v1/decision | Accepts or Rejects a given transaction, save given transaction to the client's history |
| GET | \<host>/v1/history/{email} | Gets the transaction history for a given client, identified via email |

### Requirements
* Payload must be on JSON format

## Prerequisites
* Java JDK 1.8
* Gradle 3.2.1

## Examples

### POST \<host>/v1/decision

Case: Transaction is accepted

```
REQUEST
POST /v1/decision HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
   "email":"john.doe@email.com",
   "purchase_amount":10,
   "first_name":"john",
   "last_name":"doe"
}
```

Fields:
* email (Required)
* purchase_amount (Required)
* first_name (Optional)
* last_name (Optional)

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
   "accepted":true,
   "reason":"ok"
}
```

Fields:
* accepted (Required)
* reason (Required)

Case: Transaction is rejected as it exceeds the maximum amount allowed

```
REQUEST
POST /v1/decision HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
   "email":"john.doe@email.com",
   "purchase_amount":20,
   "first_name":"john",
   "last_name":"doe"
}
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
   "accepted":false,
   "reason":"amount"
}
```

Case: Transaction is rejected as it exceeds the client's credit limit

```
REQUEST
POST /v1/decision HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
   "email":"john.doe@email.com",
   "purchase_amount":5,
   "first_name":"john",
   "last_name":"doe"
}
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
   "accepted":false,
   "reason":"debt"
}
```

### GET \<host>/v1/history/{email}

Case: Get transaction history for a non-existent client

```
REQUEST
GET /v1/history/jane.doe@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
   "email":"jane.doe@email.com",
   "history":[]
}
```

Fields:
* email (Required)
* history (Required)

Case: Get Transaction history for an existent client

```
REQUEST
GET /v1/history/john.doe@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```
```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
   "email":"john.doe@email.com",
   "history":[
      {
         "email":"john.doe@email.com",
         "amount":10,
         "decision":{
            "accepted":true,
            "reason":"ok"
         }
      },
      {
         "email":"john.doe@email.com",
         "amount":20,
         "decision":{
            "accepted":false,
            "reason":"amount"
         }
      },
      {
         "email":"john.doe@email.com",
         "amount":5,
         "decision":{
            "accepted":false,
            "reason":"debt"
         }
      }
   ]
}
```

### GET \<host>/v1/history/{email}?decision={reason}

Case: Get the transaction history of a given client, filtering by the decision made about the transactions
```
REQUEST
GET /v1/history/john.doe@email.com?decision=debt HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```
```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
   "email":"john.doe@email.com",
   "history":[
      {
         "email":"john.doe@email.com",
         "amount":5,
         "decision":{
            "accepted":false,
            "reason":"debt"
         }
      }
   ]
}
```

## Assumptions
* Data persistence was not required, in-memory storage is being used;
* Transactions with value higher than 10 are automatically denied;
* The credit limit for a client is 100.

## Improvements
* Data persistence could be added to improve reliability;
* The POST call can be updated to use the email as a path parameter, like GET call.

## Support
If you have any question, please send an [email](mailto:juliazottis@hotmail.com).
