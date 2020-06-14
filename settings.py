import os

DATABASE_URL = os.environ.get('DATABASE_URL')
SECRET_KEY = os.environ.get('SECRET_KEY')
SMTP_LOGIN = os.environ.get('SMTP_LOGIN')
SMTP_PASSWORD = os.environ.get('SMTP_PASSWORD')
MONGODB_URL = os.environ.get('MONGODB_URL')