# Blood Bank Management System (Advanced Java)

Servlets + JSP/JSTL + JDBC + MySQL, MVC architecture with the DAO pattern.

## Requirements
- JDK 17
- Apache Tomcat 10.1 (it uses `jakarta.*` packages, so Tomcat 9 will not work)
- MySQL 8
- Eclipse (Enterprise Java edition) or Maven

## How to Run (Local)
1. In MySQL, create an empty database: `CREATE DATABASE bloodbank;` (the tables are created automatically on startup).
2. Put your MySQL username and password in `src/main/java/com/bloodbank/util/DBConnection.java`, or set the `DB_URL`, `DB_USER` and `DB_PASS` environment variables.
3. Eclipse: **File → Import → Existing Maven Project**, select this folder, then **Run As → Run on Server → Tomcat 10.1**.
   Maven: run `mvn package`, then copy `target/bloodbank.war` into Tomcat's `webapps/` folder.
4. Open `http://localhost:8080/bloodbank/` (in Eclipse the URL may be `/BloodBankManagement/`).
5. Log in as admin with **admin / admin123**. This account is created automatically on startup. Donors and hospitals register themselves.

## Deploy Free (Render + Aiven MySQL)
1. **Database:** sign up at aiven.io and create a **MySQL** service on the **Free** plan. From its Overview page, copy the Host, Port, User (`avnadmin`) and Password.
2. **GitHub:** create a new repository, then **Add file → Upload files**. Drag in everything inside this folder (`src`, `pom.xml`, `Dockerfile`, `README.md`) and commit.
3. **Render:** sign up at render.com with GitHub, then **New → Web Service** and pick the repository. Render detects the Dockerfile. Choose the **Free** instance type.
4. Under **Environment Variables**, add:
   - `DB_URL` = `jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED`
   - `DB_USER` = `avnadmin`
   - `DB_PASS` = `<your Aiven password>`
5. Click **Deploy**. The first build takes about 5 minutes. Your site will be at `https://<name>.onrender.com/`.

Note: the free Render service sleeps after a while with no visitors, so the first page load can take up to about a minute. Open it once before your demo.

## Demo Flow
1. Register a **Donor** and a **Hospital**.
2. As **Admin**, click "Add" next to the donor to record a donation. The eligibility check runs and stock goes up.
3. As **Hospital**, search a blood group to see compatible groups, then raise a request.
4. As **Admin**, click **Approve & Issue** to reduce stock and mark the request ISSUED. If stock is short, the change is rolled back.
5. As **Donor**, view your profile, eligibility and donation history.

## Where Each Concept Is Used
| Concept | File(s) |
|---|---|
| JDBC (Connection, PreparedStatement, ResultSet) | `dao/*.java`, `util/DBConnection.java` |
| Transactions (commit / rollback) | `StockDAO.recordDonation()`, `RequestDAO.approveAndIssue()` |
| try-with-resources | all DAO methods |
| Servlets (`doGet` / `doPost`, `@WebServlet`) | `servlet/*.java` |
| JSP + JSTL (no scriptlets) | `webapp/WEB-INF/views/*.jsp` |
| Session tracking (HttpSession) | `LoginServlet`, `LogoutServlet`, `AuthFilter` |
| Filter (login + role check) | `filter/AuthFilter.java` |
| Listener (startup work) | `listener/AppListener.java` |
| MVC + DAO | Model = `model/` + `dao/`, View = JSP, Controller = Servlets |
| JavaBeans | `model/*.java` |
| Salted password hashing | `util/PasswordUtil.java` |
| Blood group compatibility map | `util/BloodGroup.java` |
| Validation (client + server) | `js/validate.js`, `util/Validator.java` |
| Expiry tracking (35-day shelf life) | `StockDAO` |
