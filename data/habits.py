import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin
import datetime


class Habit(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'habits'
    id = sqlalchemy.Column(sqlalchemy.Integer,
                           primary_key=True, autoincrement=True)
    user_id = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey("users.id"))
    start_date = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True, default=datetime.datetime.today())
    description = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    pluses = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    minuses = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    public = sqlalchemy.Column(sqlalchemy.Boolean, nullable=True)
    booting = sqlalchemy.Column(sqlalchemy.Boolean, default=True)  # "загрузочная" привычка или нет
    votes = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    reputation = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    user = orm.relation('User')
