import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin


class Habit(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'verification_keys'
    id = sqlalchemy.Column(sqlalchemy.Integer,
                           primary_key=True, autoincrement=True)
    user_id = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey("users.id"))
    key_type = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    key = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    user = orm.relation('User')
