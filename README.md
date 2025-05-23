# Coptic Scribe - User API Backend

This is the authentication and user management backend for the **Coptic Scribe** project.  
It is built using **Spring Boot** and uses **PostgreSQL** for persistent storage.  
All API endpoints are fully containerized for easy local development.

---

## ✨ Features

- ✅ Spring Boot + PostgreSQL backend
- ✅ User authentication with JWT
- ✅ Account creation, login, logout, password change/reset
- ✅ Bookmark system
- ✅ Dockerized backend setup
- ✅ `.env` generated interactively

---

## 📁 Project Structure

```
user-api/
├── src/
│   └── main/java/...     # Spring Boot source code
├── .env                  # Auto-generated env file for DB credentials
├── Dockerfile            # Dockerfile for the Spring Boot app
├── Makefile              # Dev setup automation
├── README.md             # You're reading this
```

---

## 🚀 Quick Start

### 1. Clone the repo

```bash
git clone https://github.com/xxxx
cd user-api
```

### 2. Generate `.env` file interactively

```bash
make generate-env
```

This will prompt you for:

- Database name
- Username
- Password

It generates a `.env` file like this:

```
DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/your_db_name
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

---

### 3. Build and run with Docker

```bash
make run
```

This will:

- Build the Spring Boot app
- Run it via Docker
- Expose it at `http://localhost:8081`

---

## 📚 Environment Variables

| Key                 | Description                  |
|---------------------|------------------------------|
| `DATABASE_URL`      | JDBC connection string       |
| `DATABASE_USERNAME` | Postgres username            |
| `DATABASE_PASSWORD` | Postgres password            |

---

## 📦 Makefile Commands

| Command             | Description                                |
|---------------------|--------------------------------------------|
| `make generate-env` | Interactive setup for `.env`               |
| `make run`          | Build & run the app with Docker            |
| `make clean`        | Remove running containers & volumes        |

---

## 🛠️ Requirements

- [Docker](https://www.docker.com/)
- [Make](https://www.gnu.org/software/make/)
- Java 17+ (for development outside Docker)

---

## 🧼 Cleanup

```bash
make clean
```

This will:

- Stop and remove the Docker container
- Prune related volumes

---

## 🤝 Contributing

PRs and contributions welcome. Fork the repo, make changes, and open a PR.

---

## 📜 License

MIT License © 2025 – Rafic Haddad
