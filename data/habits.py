import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin


class Habit(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'habits'
    id = sqlalchemy.Column(sqlalchemy.Integer,
                           primary_key=True, autoincrement=True)
    user_id = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey("users.id"))
   # start_date = sqlalchemy.Column(sqlalchemy)
    public = sqlalchemy.Column(sqlalchemy.Boolean, nullable=True)
    booting = sqlalchemy.Column(sqlalchemy.Boolean, nullable=True)  # "загрузочная" привычка или нет
    user = orm.relation('User')
