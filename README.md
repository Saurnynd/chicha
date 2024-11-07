# AbcTradeService

## Overview
The **AbcTradeService** is a Spring Boot service that provides an API to enrich trade data with product names. Given a CSV file containing trade data, the service enriches each trade record by translating `productId` into `productName` based on a static data file (`product.csv`). It also validates trade dates and handles large volumes of data efficiently.

## Features
- **Trade Data Enrichment**: Adds product names to trade records based on `productId`.
- **Data Validation**:
    - Validates dates in `yyyyMMdd` format.
    - Logs errors for invalid dates and missing products.
- **Handles Large Datasets**: Optimized to process millions of trade records and large product datasets (10,000+ products).
- **REST API**: Exposes a simple REST endpoint to receive trade data and return enriched results.

## Prerequisites
- **Java 17** or higher
- **Maven** for dependency management

## Project Structure
- **Controller**: Exposes the REST endpoint to upload and process CSV files.
- **Service**: Handles data enrichment and validation.
- **Model**: Defines the data structures for trade records.
- **Static Data**: `product.csv` - A file containing `productId` and `productName` mappings.

## Setup

### Clone the Repository:
```bash
git clone https://github.com/abc-bank-co/20241028_Oleksii_pchelintsev.git  
```
### Build the Project:
Run Maven to build the project:  
```maven
mvn clean install
```
### Run the Application:
Start the Spring Boot application:  
```maven
mvn spring-boot:run
```
### Configure Static Data:
Ensure that the `product.csv` file is located in `src/main/resources`. This file should contain mappings in the following format:
```csv
productId,productName  
1,Treasury Bills Domestic  
2,Corporate Bonds Domestic  
3,REPO Domestic
```
## Usage

### API Endpoint
- **POST** `/api/v1/enrich`: Uploads a trade data CSV file, enriches it with product names, and returns the enriched data.

#### Request:
- **Method**: POST
- **Endpoint**: `/api/v1/enrich`
- **Content-Type**: `multipart/form-data`
- **File**: CSV file containing trade data (`file` parameter).

#### Request Example:
```curl
curl --data-binary @trade.csv --header "Content-Type: text/csv" http://localhost:8080/api/v1/enrich
```
#### CSV File Format:
The trade data CSV file should have the following columns:
```csv
date,productId,currency,price  
20160101,1,EUR,10.0  
20160101,2,USD,20.5
```
#### Response Example:
```csv
date,productName,currency,price  
20160101,Treasury Bills Domestic,EUR,10.0  
20160101,Corporate Bonds Domestic,USD,20.5
```
### Error Handling:
- If a `productId` cannot be mapped, it returns "Missing Product Name" for `productName`.
- Invalid dates are skipped and logged.

## Testing
The service includes tests. Tests are located in the `src/test/java` directory.

### Run Tests
To run all tests:  
mvn test

### Test Coverage
- **Controller Tests**: Ensures the API endpoint works as expected and validates inputs.
- **Service Tests**: Validates business logic, including data enrichment, date validation, and error handling for missing data.
- **Parameterized Tests**: Covers different test scenarios (valid/invalid dates, missing products, large datasets).

## Potential Improvements
- **Caching**: Add caching for product names to improve performance if `product.csv` data changes frequently.
- **Enhanced Error Logging**: Implement a more robust error logging system to capture and store detailed validation errors.
- **Pagination**: Add pagination for large responses.
- **Database Integration**: Store `product.csv` data in a database and retrieve it dynamically.

## Limitations
- **Static Data Dependency**: The service relies on `product.csv` as a static data source.
- **File Size**: Although optimized, the service may still encounter performance limitations with extremely large files.
