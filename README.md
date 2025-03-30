# Java Lending Application Development

# System Architecture
[View Diagram](https://excalidraw.com/#json=kqRJupkeEfFbNBdhT_0Qa,67BoWTcblsYWIXL3WsiIMA)

# Loan Product Service (Loan Product Configuration)

## Overview
The Loan Product Service is responsible for **managing loan product configurations**, including tenure types, fees, and application rules.

## Features
- **Loan Product Management**
    - Create, update, and deactivate loan products
    - Retrieve loan products with advanced filtering

- **Fee Configuration & Management**
    - Add and update loan product fees

## Technology Stack
- **Java 21**
- **Spring Boot 3.3.4**
- **MySQL**

## Database Schema

### `loan_products` – Stores loan product details
| Column Name     | Type                      | Description                              |  
|---------------|--------------------------|----------------------------------|  
| `id`          | `BIGINT`                  | Primary key, auto-generated   |  
| `name`        | `VARCHAR(255)`            | Loan product name             |  
| `description` | `VARCHAR(255)`            | Loan product description      |  
| `active`      | `INT`                      | Active status                 |  
| `date_created` | `DATETIME(6)`            | Creation timestamp            |  
| `date_modified` | `DATETIME(6)`           | Last modification timestamp   |  
| `tenure_type` | `ENUM('FIXED', 'VARIABLE')` | Tenure type (Fixed/Variable) |
| `tenure_unit` | `ENUM('DAYS', 'MONTHS')` | Tenure unit (Days/Months)     |
| `tenure_value` | `INT`                    | Tenure value                  |

### `fees` – Stores fees applicable to loan products
| Column Name     | Type      | Description                              |  
|---------------|---------|----------------------------------|  
| `id`          | `BIGINT`  | Primary key, auto-generated   |  
| `product_id`  | `BIGINT`  | Foreign key (loan product ID) |  
| `type`        | `VARCHAR` | Fee type (`SERVICE_FEE`, `LATE_FEE`, etc.) |  
| `value`       | `DECIMAL` | Fee amount                     |  
| `application_rule` | `VARCHAR` | Defines fee application logic  |  

## API Endpoints

### Loan Product Management
- **Create Loan Product**
    - `POST /loan-product`
    - **Request Body:** `LoanProductRequest`
    - **Response:** Returns the created loan product details

- **Retrieve All Loan Products (with Filters)**
    - `GET /loan-product`
    - **Query Params:** Filters from `LoanProductFilterRequest`
    - **Response:** List of loan products

- **Deactivate Loan Product**
    - `PUT /loan-product/{loanId}/deactivate`
    - **Path Param:** `loanId` (Loan product ID)
    - **Response:** Success message on deactivation

### Fee Configuration
- **Add or Update Fees for a Loan Product**
    - `POST /loan-product/{loanId}/fee`
    - **Path Param:** `loanId` (Loan product ID)
    - **Request Body:** List of `LoanProductFeeRequest`
    - **Response:** Updated fee details

### Sample Request & Response

#### Create Loan Product Request
```json
{
  "loanProductName": "Personal Loan",
  "description": "A short-term personal loan with flexible repayment options.",
  "tenureType": "Fixed",
  "tenureValue": 14,
  "tenureUnit": "Months"
}

```

#### Create Loan Product Response
```json
{
  "success": true,
  "message": "Loan created successfully",
  "data": {
        "id": 2,
        "name": "Mortgage Loan",
        "description": "A long-term mortgage loan.",
        "tenureType": "Fixed",
        "tenureValue": 14,
        "tenureUnit": "Months",
        "active": 1,
        "dateModified": "2025-03-28T00:01:48.83534",
        "dateCreated": "2025-03-28T00:01:48.835326"
      }
}

```


#### Fetch Loan Product Response
```json
{
  "success":true,
  "message":"Loans retrieved successfully",
  "data":{
    "success":true,
    "message":"Successfully fetched loan products",
    "data":{
      "content":[
        {
          "id":1,
          "name":"Personal Loan",
          "description":"A short-term personal loan with flexible repayment options.",
          "tenureType":"FIXED",
          "tenureValue":14,
          "tenureUnit":"MONTHS",
          "active":1,
          "fees":[
            {
              "feeType":"LATE_FEE",
              "calculationType":"PERCENTAGE",
              "amount":5.00,
              "active":1,
              "daysAfterDue":7
            },
            {
              "feeType":"SERVICE_FEE",
              "calculationType":"FIXED",
              "amount":100.00,
              "active":1,
              "daysAfterDue":0
            }
          ]}
      ],
      "pageNo":0,
      "pageSize":10,
      "totalElements":1,
      "totalPages":1,
      "last":true
    }
  }
}
```


#### Add fee to loan product request
```json
[
  {
    "feeType": "LATE_FEE",
    "calculationType": "PERCENTAGE",
    "amount": 5.0,
    "daysAfterDue": 7
  },
  {
    "feeType": "SERVICE_FEE",
    "calculationType": "FIXED",
    "amount": 100.0,
    "daysAfterDue": 0
  }
]

```



#### Add fee to loan product response
```json
{
    "success": true,
    "message": "Fees updated successfully",
    "data": {
        "loanId": 1,
        "loanName": "Personal Loan",
        "fees": [
            {
                "feeType": "LATE_FEE",
                "calculationType": "PERCENTAGE",
                "amount": 5.0,
                "active": 1,
                "daysAfterDue": 7
            },
            {
                "feeType": "SERVICE_FEE",
                "calculationType": "FIXED",
                "amount": 100.0,
                "active": 1,
                "daysAfterDue": 0
            }
        ]
    }
}
```

### Running the Application

Ensure Java 21 is installed.

Run the application using:

`mvn spring-boot:run`

The service will start on `http://localhost:8083`.

### Docker Setup

To containerize the application, follow these steps:

Build the JAR file:

mvn clean package -DskipTests

Create a `DockerFile` in the project root:
````
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/loan-product-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
````
Build and run the Docker container:


``docker build -t loan-product-api .``

``
docker run -p 8080:8080 loan-product-api
``


