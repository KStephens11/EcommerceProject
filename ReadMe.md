# Ecommerce Product Catalog

A Spring Boot + jQuery e-commerce application with JWT authentication, product management, shopping cart, and order tracking.

---

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 17+
- Maven
- MySQL (running locally on port `3306`)
- A web browser

---

## Database Setup

The application connects to a MySQL database called `ecomdb`. It will be created automatically on first run if it doesn't exist, but your MySQL user must have the necessary privileges.

Make sure MySQL is running, then verify your credentials match what's in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecomdb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

If your MySQL credentials differ, update those values before starting the app.

---

## Configuration

### Database credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### JWT secret

A default JWT secret is provided in `application.properties`. For any non-development environment, replace it with your own 64-character secret:

```properties
jwt.secret=YOUR_64_CHARACTER_SECRET_HERE
jwt.expiration=86400000   # token lifetime in milliseconds (default: 24 hours)
```

### Bootstrap credentials

Default admin and customer accounts are defined in `src/main/resources/application.yml`.

```yaml
app:
  bootstrap:
    admin:
      username: admin
      password: admin
    customer:
      username: customer
      password: customer
```

---

## Running the Application

Clone the repository, then build and start the server:

```bash
# Clone
git clone https://github.com/KStephens11/EcommerceProject.git
cd EcommerceProject

# Build and run
mvn spring-boot:run
```

The application will start on **http://localhost:8080**.

---

## First Launch

On startup, `DataInitializer` automatically:

1. Creates `ADMIN` and `CUSTOMER` roles
2. Creates the default admin and customer users (from `application.yml`)
3. Seeds 5 sample products (Electronics, Audio, Home categories)

> **Note:** The database schema is set to `create` mode (`spring.jpa.hibernate.ddl-auto=create`), which drops and recreates all tables on every restart. Switch to `update` or `validate` once you want data to persist between restarts.

---

## Default Accounts

| Role     | Username   | Password   |
|----------|------------|------------|
| Admin    | `admin`    | `admin`    |
| Customer | `customer` | `customer` |

---

## Application Walkthrough

Navigate to **http://localhost:8080**, you'll be redirected to the login page.

### Customer access
After logging in as a customer, you can:
- Browse and search the product catalog
- View product details
- Add items to the shopping cart
- Place orders (checkout)

### Admin access
After logging in as an admin, you additionally have access to:
- **Product Management:** create, edit, delete products
- **Orders:** view all customer orders and order statistics, including revenue by category and low-stock alerts

---

## Project Structure

```
src/
├── main/
│   ├── java/com/tus/ecom/
│   │   └── config/
│   │       ├── DataInitializer.java      # Seeds roles, users, and products
│   │       ├── JwtAuthenticationFilter.java
│   │       ├── PageConfig.java
│   │       ├── SecurityConfig.java       # JWT + role-based access rules
│   │       └── WebConfig.java            # CORS + static resource handling
│   └── resources/
│       ├── static/
│       │   ├── js/
│       │   │   ├── app.js                # Entry point
│       │   │   └── modules/
│       │   │       ├── products.js
│       │   │       ├── cart.js
│       │   │       ├── navbar.js
│       │   │       ├── orders-panel.js
│       │   │       └── product-management.js
│       │   ├── index.html
│       │   ├── login.html
│       │   └── register.html
│       ├── application.properties        # DB, JWT, multipart config
│       └── application.yml               # Bootstrap user credentials
```

---

## API Endpoints (Summary)

| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/auth/login` | Public |
| POST | `/api/auth/logout` | Public |
| POST | `/api/users/register` | Public |
| GET | `/api/users/me` | Authenticated |
| GET | `/api/products` | Customer, Admin |
| GET | `/api/products/name` | Customer, Admin |
| GET | `/api/products/low-stock` | Admin |
| POST | `/api/products` | Admin |
| PUT | `/api/products/{id}` | Admin |
| DELETE | `/api/products/{id}` | Admin |
| POST | `/api/products/upload-image` | Admin |
| GET | `/api/orders` | Admin |
| POST | `/api/orders` | Customer |
| GET | `/api/orders/stats/sales-by-category` | Admin |

---

## Notes

- Product images are stored under `src/main/resources/static/uploads/` and served at `/uploads/**`.
- The cart is persisted in **localStorage**, keyed by username, so cart contents survive page refreshes.
- File upload size is capped at **10MB** per file/request (configurable in `application.properties`).