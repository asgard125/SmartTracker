from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.__all_models import Habit, User
import datetime


def check_api_key(api_key):
    session = db_session.create_session()
    user = session.query(User).filter(User.api_key == api_key).first()
    if user is None:
        abort(403, message="Invalid api key")
    return user


def abort_if_habit_not_found(id):
    session = db_session.create_session()
    items = session.query(User).get(id)
    if not items:
        abort(404, message=f"Habit not found")


class HabitResource(Resource):
    def get(self, id):
        parser = reqparse.RequestParser()
        parser.add_argument("info_type", required=False)
        parser.add_argument("api_key", required=False)
        args = parser.parse_args()
        abort_if_habit_not_found()
        session = db_session.create_session()
        user = session.query(User).get(id)
        return jsonify({'user': user.to_dict(
            only=('id', 'name', 'rating'))})


class UserListResource(Resource):
    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'email', 'api_key', 'rating')) for user in users]})

