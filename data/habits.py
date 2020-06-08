import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin
import datetime
from data import db_session


class Habit(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'habits'
    id = sqlalchemy.Column(sqlalchemy.Integer,
                           primary_key=True, autoincrement=True)
    user_id = sqlalchemy.Column(sqlalchemy.Integer, sqlalchemy.ForeignKey("users.id"))
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    start_date = sqlalchemy.Column(sqlalchemy.DateTime, nullable=False, default=datetime.datetime.today())
    description = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    pluses = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    minuses = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    type = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    booting = sqlalchemy.Column(sqlalchemy.Boolean, default=True)  # "загрузочная" привычка или нет
    weekdays = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    notify_time = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    votes = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    reputation = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    voted_users = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    user = orm.relation('User')

    def change_data(self, name, description, pluses, minuses, type, weekdays, notify_time):
        session = db_session.create_session()
        habit = session.query(Habit).get(self.id)
        if name:
            habit.name = name
        if description:
            habit.description = description
        if pluses:
            habit.pluses = pluses
        if minuses:
            habit.minuses = minuses
        if type:
            habit.type = type
        if weekdays:
            habit.weekdays = weekdays
        if notify_time:
            habit.notify_time = notify_time
        session.commit()
