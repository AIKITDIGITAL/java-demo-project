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
Criteria matching should be always in order below:
* validate that input parameters are valid, if not, return 400 Bad Request
* first get data from server/mock
* filter data by `search` param, `,` is logic operator `AND`, `|` is logic operator `OR` and `()` are used to change the priority.
* get sublist of data limited by `offset` and `limit`, if not preset use defaults (offset:0, limit:25)
* sort data by `sort` param
* return result (if criteria does not match any result, empty list is returned)
### Request parameters
#### search
* required = false
* if present not empty and matches criteria below
* valid logic operators `,` for `AND`, `|` for `OR` and `()` to change priority
* valid fields (userId, username, name, surname, salary, from, to) case-sensitive
* value is case-sensitive
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

* userId: non-null number
* username: non-null string
* name: non-null string 
* surname: non-null string
* salary: non-null number
* from: non-null string in format `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`
* to: null or string in format `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`

```json
[
  {
    "userId": 0,
    "username": "string",
    "name": "string",
    "surname": "string",
    "salary": 10000,
    "from": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    "to": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  }
]
```

## /api/users/{id}
Search user by `id` (`userId`) returns model with response code 200. If not found, response code 404 Not Found is returned.
### Query parameters
#### {id}
* not empty, number value, `id` >= 0

### Model example

* userId: non-null number
* username: non-null string
* name: non-null string
* surname: non-null string
* salary: non-null number
* from: non-null string in format `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`
* to: null or string in format `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`

```json
{
  "userId": 0,
  "username": "string",
  "name": "string",
  "surname": "string",
  "salary": 10000,
  "from": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
  "to": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
}
```