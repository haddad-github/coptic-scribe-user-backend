# user-api/generate_env.py

import os

print("Create .env file for Spring Boot database connection:")
dbname = input("Enter DB name: ").strip()
dbuser = input("Enter DB username: ").strip()
dbpass = input("Enter DB password: ").strip()

env_content = f"""DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/{dbname}
DATABASE_USERNAME={dbuser}
DATABASE_PASSWORD={dbpass}
"""

with open(".env", "w") as f:
    f.write(env_content)

print("✅ .env file created successfully.")