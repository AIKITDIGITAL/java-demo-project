# Task description
* Implement the application, so it matches API Specification described below and all the integration tests in `UserControllerIT.java` class will pass.
* If you are missing some libraries, you can add them to the project as you want. 
* If you want, you can add your own integration tests or implement JUnit tests for your code.
* If you are finished, please push your changes to your own branch, create pull request and let as know.
* We have more integration tests prepared for final review. Be sure all scenarios are handled in your implementation.
* If you find any mistakes in description, tests or specification, please contact me: mikures@seznam.cz

# Hints
* For client which will be calling mocked rest API in tests use `baseUrl` property from `ClientConfigProperties`.
* Test data for mocked server are [here](src/test/resources/__files/test.json), please do not edit them.

# API Specification
## /api/users
Criteria matching should be always in order:
* validate that input parameters are valid, if not, return 400 Bad Request
* first get data from server/mock
* filter data by `search` param
* get sublist of data limited by `offset` and `limit`, if not preset use defaults (offset:0, limit:25)
* sort data by `sort` param
* return result (if criteria does not match any result, empty list is returned)
### Request parameters
#### search
* required = false
* if present not empty and matches criteria below
* could contain (userId, username, name, surname, salary, from, to)
* valid operators for string values are (: for equals, ~ for not equal)
* valid operators for number values are (: for equals, ~ for not equal, <, >, <=, =>)
* valid operators for date values are (: for equals, ~ for not equal, <, >, <=, =>)

#### offset
* required = false
* default = 0
* `offset` is number
* `offset` >= 0

#### limit
* required = false
* default = 25
* `limit` is number
* `limit` >= 0

#### order
* required = false
* if present not empty and matches criteria below
* `order` could be only `ASC` or `DESC` case-insensitive

### Model example

```json
[
  {
    "userId": 0, // non-null
    "username": "string", // non-null
    "name": "string", // non-null
    "surname": "string", // non-null
    "salary": 10000, // non-null
    "from": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", // non-null in format
    "to": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // null or in format
  }
]
```

## /api/users/{id}
Search user by `id` which is `userId`, Returns model. If not exits, 404 Not Found is returned.
### Query parameters
#### {id}
* not empty, number value, `id` >= 0

### Model example
```json
{
  "userId": 0, // non-null
  "username": "string", // non-null
  "name": "string", // non-null
  "surname": "string", // non-null
  "salary": 10000, // non-null
  "from": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", // non-null in format
  "to": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // null or in format
}
```