# PetStore API Testing Project

## 🚀 Overview

This project is a comprehensive testing suite for the **PetStore API** based on the Swagger PetStore specification. The tests are designed using **TestNG** and **RestAssured**.

---

## 📋 Project Structure

```
📂 src
 └── 📂 main
     └── 📂 java
         └── dev.jiricekm.petstore
             ├── Config.java
             ├── common
             │   ├── StoreApiClient.java
             │   └── StoreApiTestBase.java
             ├── dto
             │   ├── DTOFactory.java
             │   └── OrderDTO.java
             ├── enums
             │   └── OrderStatus.java
             ├── helpers
             │   └── StoreOrderHelper.java
             └── listeners
                 └── TestListener.java

 📂 test
  └── 📂 java
      └── dev.jiricekm.petstore
          ├── FunctionalTests
          │   ├── OrderPlacementTests.java
          │   ├── GetOrderTests.java
          │   ├── GetInventoryTests.java
          │   └── DeleteOrderTests.java
          ├── PerformanceTests
          │   └── PerformanceTests.java
          └── SecurityTests
              └── SecurityTests.java

 📄 testng.xml
 📄 pom.xml
```

---

## ⚙️ Setup Instructions

### Prerequisites

- Java 17 or later
- Maven 3.8+
- TestNG 7.11+
- RestAssured 5.5.1

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/jiricekmartin/petstore-api-tests.git
   cd petstore-api-tests
   ```
2. **Install Dependencies**
   ```bash
   mvn clean install
   ```
3. **Configure Environment**
    - Configuration files are located in `/resources`:
        - `config-dev.properties`
        - `config-test.properties`
        - `config-prod.properties`

---

## 🧪 Running Tests

```bash
mvn test -Ptest
```
---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

