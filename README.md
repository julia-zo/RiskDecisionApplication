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

_Under construction_

## Assumptions
* Data persistence was not required, in-memory storage is being used;
* Transactions with value higher than 10 are automatically denied;
* The credit limit for a client is 100.

## Improvements
* Data persistence could be added to improve reliability;
* The POST call can be updated to use the email as a path parameter, like GET call.

## Support
If you have any question, please send an [email](mailto:juliazottis@hotmail.com).