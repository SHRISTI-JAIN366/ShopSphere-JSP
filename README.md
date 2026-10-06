# 🛍️ ShopSphere — Advanced Java E-Commerce Management System

> **RTU Advanced Java & Java Enterprise Capstone Project**  
> Developed using Core Java, OOP Architecture, Collections Framework, Custom Exception Handling, Dual Storage (Serialization / CSV / JDBC MySQL), and an Interactive CLI Menu System.

---

## 📋 13-Step Implementation Architecture

| Step | Topic | Implementation Details |
| :--- | :--- | :--- |
| **1** | **IntelliJ Project Setup** | `pom.xml`, `ShopSphere.iml`, `.idea/` project files ready for instant loading in IntelliJ IDEA. |
| **2** | **Package Hierarchy** | Structured under `com.shopsphere.{model, interfaces, exception, service, dao, storage, menu, app, test, util}`. |
| **3** | **Product Class Hierarchy** | Abstract base [`Product`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Product.java) with [`PhysicalProduct`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/PhysicalProduct.java) and [`DigitalProduct`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/DigitalProduct.java). |
| **4** | **Customer & User Hierarchy** | Abstract base [`User`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/User.java) with [`Customer`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Customer.java) and [`Admin`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Admin.java). |
| **5** | **Order & OrderItem** | [`Order`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Order.java), [`OrderItem`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/OrderItem.java), [`Coupon`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Coupon.java), [`Review`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Review.java), [`Address`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/model/Address.java). |
| **6** | **Constructors** | Default and parameterized constructors for all entities. |
| **7** | **Getters & Setters** | Encapsulated fields with strict boundary validation (discounts, prices, stock). |
| **8** | **Inheritance & Interfaces** | Implements [`Discountable`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/interfaces/Discountable.java), [`Payable`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/interfaces/Payable.java), [`Searchable`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/interfaces/Searchable.java), [`InventoryOperations`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/interfaces/InventoryOperations.java), [`Storable`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/interfaces/Storable.java), `Serializable`. |
| **9** | **Collections Framework** | `List<OrderItem>` (Cart), `Map<Integer, Product>` ($O(1)$ catalog lookup), `Map<String, User>`, Streams for sorting/filtering. |
| **10** | **Custom Exception Handling** | [`InsufficientStockException`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/exception/InsufficientStockException.java), [`InvalidCouponException`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/exception/InvalidCouponException.java), [`ProductNotFoundException`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/exception/ProductNotFoundException.java), [`AuthenticationException`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/exception/AuthenticationException.java). |
| **11** | **File & Database Storage** | Dual persistence via [`ObjectSerializationManager`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/storage/ObjectSerializationManager.java) (`.ser`), [`CsvStorageManager`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/storage/CsvStorageManager.java) (`.csv`), and MySQL JDBC [`shopsphere.sql`](file:///c:/Users/jains/Videos/java%20tutorial/project/database/shopsphere.sql). |
| **12** | **Interactive Menu System** | Rich CLI [`ConsoleMenu`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/menu/ConsoleMenu.java) featuring Customer Storefront, Cart, Checkout, and Admin Control Center. |
| **13** | **Automated Test Suite** | [`ShopSphereTest`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/test/ShopSphereTest.java) with 100% test pass rate across 30 unit/integration tests. |

---

## 🚀 How to Run

### 🌐 Option 1: Run as Web Application on `http://localhost:8080` (Browser)
Run the built-in HTTP Web Server (automatically opens your browser to `http://localhost:8080`):
```cmd
.\run_web.bat
```
*(Or in PowerShell: `.\run_web.ps1` or run [`ShopSphereWebApplication.java`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/app/ShopSphereWebApplication.java)).*

### 💻 Option 2: Run Console CLI Application (Terminal)
Double-click [`run_app.bat`](file:///c:/Users/jains/Videos/java%20tutorial/project/run_app.bat) or run:
```cmd
.\run_app.bat
```
*(Or run [`ShopSphereApplication.java`](file:///c:/Users/jains/Videos/java%20tutorial/project/src/com/shopsphere/app/ShopSphereApplication.java)).*

### 🧪 Option 3: Run Automated Tests
```cmd
.\run_tests.bat
```

### Option 3: Open in IntelliJ IDEA
1. Open **IntelliJ IDEA**.
2. Click **File -> Open...** and select `c:\Users\jains\Videos\java tutorial\project`.
3. IntelliJ will automatically detect the SDK and module configurations.
4. Navigate to `src/com/shopsphere/app/ShopSphereApplication.java` and click the **Run (Green Play)** button.

---

## 🔑 Default Credentials

- **Admin Account**: `admin@shopsphere.com` / `admin123`
- **Customer Account**: `rahul@example.com` / `user123`
- **Sample Coupons**: `WELCOME10` (10% off), `FESTIVE25` (25% off), `FLAT500` (15% off up to ₹500)
"# ShopSphere-JSP" 
