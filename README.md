# 🚀 Application

A simple JavaFX application built as part of a **Smart Task & Resource Management System (STRMS)** project.

This project focuses on learning **object-oriented programming, clean architecture, and modular design** while building a real-world inspired system.

---

## 🧠 Project Overview

The goal of this project is to design and implement a system capable of:

- Managing users with different roles (Admin, Manager, Engineer)
- Creating, assigning, and tracking tasks
- Handling task dependencies and execution order
- Recording task history for traceability
- Managing priorities and task lifecycle

This system simulates a real-world engineering workflow and emphasizes strong software design principles.

---

## 🏗️ Architecture

This project follows a **layered architecture inspired by Clean Architecture / Domain-Driven Design (DDD)**.
```
app                → Application entry point
domain             → Core business logic (models, services)
infrastructure     → Data access (repositories, persistence)
view               → JavaFX UI (controllers + FXML)
utils              → Shared utilities (validation, hashing, etc.)
```

### Key principles:
- **Separation of concerns** between layers
- **Domain is independent** from UI and infrastructure
- **Clear responsibilities** for each component
- **Scalable and maintainable structure**

---

## 🛠️ Technologies

- Java
- JavaFX
- Maven
- BCrypt (password hashing)

---

## ▶️ Run the project

```bash
./mvnw javafx:run
```

## 📌 Learning Objectives

This project is designed to practice:
- Object-Oriented Programming (encapsulation, inheritance, polymorphism, abstraction)
- Clean architecture and project organization
- File handling and data persistence
- Exception handling
- Use of Java collections (List, Map, Set, etc.)
- Writing maintainable and testable code

---

## 👥 Team

Project developed as part of a university assignment.