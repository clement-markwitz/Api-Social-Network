FROM python:3.9-slim AS base

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 5000

ENTRYPOINT ["python", "main.py"]