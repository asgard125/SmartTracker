import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin
from werkzeug.security import check_password_hash
import random
from data import db_session


def random_name():
    symbols = list('qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM')
    length = random.randint(5, 16)
    return ''.join(random.choices(symbols, k=length))


class User(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'users'
    id = sqlalchemy.Column(sqlalchemy.Integer,
                           primary_key=True, autoincrement=True)
    login = sqlalchemy.Column(sqlalchemy.String,
                              index=True, unique=True, nullable=False)
    password = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    name = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    rating = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    api_key = sqlalchemy.Column(sqlalchemy.String, nullable=True, unique=True)

    habits = orm.relation('Habit', back_populates='user', lazy='subquery')

    def check_password(self, password):
        return check_password_hash(self.password, password)

    def generate_api_key(self):
        session = db_session.create_session()
        user = session.query(User).filter(User.id == self.id).first()
        self.api_key = random_name() + '_' + str(self.id)
        user.api_key = self.api_key
        session.commit()
        return self.api_key

    def delete_api_key(self):
        session = db_session.create_session()
        user = session.query(User).filter(User.id == self.id).first()
        self.api_key = None
        user.api_key = self.api_key
        session.commit()

    @staticmethod
    def get_by_api(api_key):
        session = db_session.create_session()
        return session.query(User).get(api_key)

