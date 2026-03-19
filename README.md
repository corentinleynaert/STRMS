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

This project follows a **layered architecture inspired by Clean Architecture and Domain-Driven Design (DDD)**.

```
app               → Application entry point (bootstrapping & dependency wiring)
application       → Use cases (business orchestration, e.g. AuthService)
domain            → Core business logic (entities, value objects, interfaces)
infrastructure    → Technical implementations (file access, repositories, security)
presentation      → JavaFX UI (controllers, view loaders)
```

### Key principles:

- **Separation of concerns** between layers
- **Domain is independent** from UI and infrastructure
- **Dependency inversion** (domain defines interfaces, infrastructure implements them)
- **Explicit use cases** through the application layer
- **Encapsulation of business rules** inside domain models
- **Clear and scalable structure**

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

--- 

## 📌 Learning Objectives

This project is designed to practice:
- Object-Oriented Programming (encapsulation, abstraction, polymorphism)
- Clean Architecture & Domain-Driven Design basics
- Separation between domain, application, and infrastructure layers
- File-based persistence
- Exception handling
- Use of Java collections (List, Map, Set, etc.)
- Writing maintainable, testable, and scalable code

---

👥 Team

Project developed as part of a university assignment.