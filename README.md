# Online-BusTicket-booking-Application
# 🚌 Online Bus Ticket Booking Application

A full-stack **Online Bus Ticket Booking Application** developed using **Java, Spring Boot, Thymeleaf, MySQL, HTML, CSS, JavaScript, and AWS**. The application allows users to search buses, book tickets, verify bookings using OTP, and receive booking confirmation via email.
http://deepika2.us-east-1.elasticbeanstalk.com/
---

## 📌 Features

- 👤 User Registration & Login
- 🔍 Search Buses by Source, Destination & Date
- 🚌 View Available Bus Details
- 🎫 Book Bus Tickets
- 🔐 OTP Verification
- 📧 Email Ticket Confirmation
- 📜 View Booking History
- 👨‍💼 Admin Bus Management
- 📱 Responsive User Interface

---

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Maven

### Frontend
- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Bootstrap

### Database
- MySQL

### Deployment
- AWS

### Tools
- Spring Tool Suite (STS)
- Git
- GitHub
- Postman

---

## 📂 Project Structure

```
Online-BusTicket-booking-Application
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   ├── repository
│   │   │   ├── entity
│   │   │   ├── config
│   │   │   └── TicketManagementApplication.java
│   │   │
│   │   ├── resources
│   │   │   ├── templates
│   │   │   ├── static
│   │   │   └── application.properties
│
├── pom.xml
├── README.md
└── .gitignore
```

---

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/Deepika-20061103/Online-BusTicket-booking-Application.git
```

### Navigate to the Project

```bash
cd Online-BusTicket-booking-Application
```

### Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE ticketbooking;
```

Update your `application.properties` with your local database and email configuration.

### Run the Application

```bash
mvn spring-boot:run
```

Open your browser:

```
http://localhost:8080
```

---

## 📋 Application Workflow

1. Register or log in to the application.
2. Search buses using source, destination, and travel date.
3. Select a bus.
4. Enter passenger details.
5. Verify the booking using OTP.
6. Confirm the booking.
7. Receive the ticket confirmation via email.
8. View booked tickets from your account.

---

## 🗄️ Database

The application uses the following main tables:

- User
- Bus
- Ticket

---

## 🔮 Future Enhancements

- 💳 Online Payment Gateway
- 💺 Seat Selection
- 📍 Live Bus Tracking
- 📱 SMS Notifications
- 📄 PDF Ticket Download
- ❌ Ticket Cancellation & Refund
- ⭐ Bus Ratings & Reviews

---

## 📸 Screenshots

Add screenshots of the application here.

Example:

```
screenshots/
│── home.png
│── login.png
│── search.png
│── booking.png
│── confirmation.png
```

Then display them:

```md
### Home Page
![Home](screenshots/home.png)

### Bus Search
![Search](screenshots/search.png)

### Booking Confirmation
![Confirmation](screenshots/confirmation.png)
```

---

## 👩‍💻 Author

**Deepika Somisetty**

- GitHub: https://github.com/Deepika-20061103
- LinkedIn: *(Add your LinkedIn profile URL here)*

---

## 📄 License

This project is created for educational and learning purposes.
