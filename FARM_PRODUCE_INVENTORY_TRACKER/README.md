# FarmStock - Farmer's Produce Inventory Tracker

Spring Boot + Spring Data JPA + MySQL project based on **Question 22: FarmStock — Farmer's Produce Inventory Tracker**.

## Features

1. Log a harvest batch with crop, quantity and harvest date.
2. Record a sale against a crop.
3. View current stock for a crop.
4. Reject a sale when requested quantity is greater than available stock.
5. Calculate total revenue for a crop over a date range.
6. Bean validation and a global exception handler with clear JSON errors.
7. Simple browser dashboard at `/`.

## Project structure

- `model/Crop.java` - crop master data
- `model/HarvestBatch.java` - harvested quantity
- `model/Sale.java` - sold quantity and price
- `repository/` - JPA repositories and aggregate queries
- `service/InventoryService.java` - business rules
- `controller/` - REST API endpoints
- `exception/` - clear error responses
- `static/` - simple frontend dashboard

## Database setup

Install MySQL and create the database:

```sql
CREATE DATABASE farm_inventory;
```

Default connection is:

- URL: `jdbc:mysql://localhost:3306/farm_inventory`
- Username: `root`
- Password: `root`

If your MySQL password is different, set it before running:

Windows CMD:

```bat
set DB_PASSWORD=your_password
```

PowerShell:

```powershell
$env:DB_PASSWORD="your_password"
```

## Run

From the `farmproduceinventory` folder:

```bat
mvn spring-boot:run
```

or, if Maven Wrapper is present:

```bat
mvnw.cmd spring-boot:run
```

Open:

`http://localhost:8080/`

Swagger UI API documentation:

`http://localhost:8080/swagger-ui/index.html`

## REST endpoints

### Crops

- **Create**: `POST /api/crops`
  ```json
  {
    "name": "Tomato",
    "category": "Vegetable",
    "unit": "kg"
  }
  ```
- **List All**: `GET /api/crops`
- **Get Single**: `GET /api/crops/{cropId}`
- **Update**: `PUT /api/crops/{cropId}`
  ```json
  {
    "name": "Roma Tomato",
    "category": "Vegetable",
    "unit": "kg"
  }
  ```
- **Delete**: `DELETE /api/crops/{cropId}` (deletes the crop along with associated harvest and sale records)

### Harvests

- **Add harvest**: `POST /api/crops/{cropId}/harvests`
  ```json
  {
    "quantity": 100,
    "harvestDate": "2026-09-28"
  }
  ```
- **List harvests**: `GET /api/crops/{cropId}/harvests`
- **Update harvest**: `PUT /api/crops/{cropId}/harvests/{harvestId}`
  ```json
  {
    "quantity": 120,
    "harvestDate": "2026-09-28"
  }
  ```
- **Delete harvest**: `DELETE /api/crops/{cropId}/harvests/{harvestId}`

### Sales

- **Record sale**: `POST /api/crops/{cropId}/sales`
  ```json
  {
    "quantity": 25,
    "pricePerUnit": 40,
    "saleDate": "2026-09-28"
  }
  ```
- **List sales**: `GET /api/crops/{cropId}/sales`
- **Update sale**: `PUT /api/crops/{cropId}/sales/{saleId}`
  ```json
  {
    "quantity": 30,
    "pricePerUnit": 42,
    "saleDate": "2026-09-28"
  }
  ```
- **Delete sale**: `DELETE /api/crops/{cropId}/sales/{saleId}`

If a sale or sale update requires more quantity than available stock, the API returns HTTP 400 and rejects the operation.

### Current stock

`GET /api/crops/{cropId}/stock`

Stock formula:

`total harvested quantity - total sold quantity`

### Revenue

`GET /api/crops/{cropId}/revenue?from=2026-09-01&to=2026-09-30`

## Suggested demo flow

1. Add Tomato, unit `kg`.
2. Add a 100 kg harvest.
3. Record a 25 kg sale at ₹40/kg.
4. Stock becomes 75 kg.
5. Try selling 80 kg. The request is rejected with a clear message.
6. Open the revenue report for the date range.
7. Try updating or deleting crops, harvests, or sales with `PUT` / `DELETE` via Swagger UI.
