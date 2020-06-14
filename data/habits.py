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
    start_date = sqlalchemy.Column(sqlalchemy.DateTime, nullable=False, default=datetime.datetime.now())
    edit_date = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    description = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    pluses = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    minuses = sqlalchemy.Column(sqlalchemy.Text, nullable=True)
    type = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    booting = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    weekdays = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    notify_time = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    votes = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    reputation = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    voted_users = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    muted = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    done = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    done_time = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    user = orm.relation('User')

    def change_data(self, name=None, description=None, pluses=None, minuses=None, type=None, weekdays=None, notify_time=None, muted=None, done=None):
        session = db_session.create_session()
        habit = session.query(Habit).get(self.id)
        if name != habit.name or description != habit.description or weekdays != habit.weekdays:
            habit.edit_date = datetime.datetime.now()
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
            if type == 'private':
                habit.booting = False
        if weekdays:
            habit.weekdays = weekdays
        if notify_time:
            habit.notify_time = notify_time
        if muted is not None:
            habit.muted = muted
        if done is not None:
            habit.done = done
            if done is True:
                habit.done_time = datetime.datetime.now()
        session.commit()

    def check_user_vote(self, user_id):
        if self.voted_users is not None:
            already_voted = [i.split(':') for i in self.voted_users.split(', ')]
            for vote in already_voted:
                if str(user_id) in vote:
                    return {'voted': True, 'vote_type': vote[1]}
        return {'voted': False, 'vote_type': None}
