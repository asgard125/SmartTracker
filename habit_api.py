from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.__all_models import Habit, User
import datetime


def check_api_key(api_key):
    user = User.get_by_api(api_key)
    if user is None:
        abort(403, message="Invalid api key")
    return user


def check_is_habit_exist(id):
    session = db_session.create_session()
    items = session.query(Habit).get(id)
    if items is None:
        abort(404, message=f"Habit not found")
    return items


class HabitResource(Resource):
    def get(self, id):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        args = parser.parse_args()
        habit = check_is_habit_exist(id)
        if habit.user.api_key != args['api_key'] and habit.type == 'private':
            abort(403, message='Invalid api key')
        return jsonify({'habit': habit.to_dict(
            only=('id', 'start_date', 'description', 'pluses', 'minuses', 'type', 'booting', 'weekdays',
                  'notify_time', 'votes', 'reputation', 'muted'))})

    def put(self, id):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        parser.add_argument("name", required=False)
        parser.add_argument("description", required=False)
        parser.add_argument("pluses", required=False)
        parser.add_argument("minuses", required=False)
        parser.add_argument("type", required=False, choices=('public', 'private'))
        parser.add_argument("weekdays", required=False)
        parser.add_argument("notify_time", required=False)
        parser.add_argument("muted", required=False, type=bool)
        args = parser.parse_args()
        user = check_api_key(args['api_key'])
        habit = check_is_habit_exist(id)
        if habit.user_id == user.id:
            habit.change_data(name=args['name'], description=args['description'], pluses=args['pluses'],
                              minuses=args['minuses'],
                              type=args['type'], weekdays=args['weekdays'], notify_time=args['notify_time'],
                              muted=args['muted'])
        else:
            abort(403, message='Invalid user')
        return jsonify({'result': 'OK'})

    def delete(self, id):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        args = parser.parse_args()
        session = db_session.create_session()
        user = check_api_key(args['api_key'])
        habit = session.query(Habit).get(id)
        if habit is None:
            abort(410, message="Habit was already deleted")
        if habit.user_id != user.id:
            abort(403, message='Invalid user')
        user.change_data(habit_limit=user.habit_limit + 1)
        session.delete(habit)
        session.commit()
        return jsonify({'result': 'OK'})


class HabitListResource(Resource):
    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument("habit_type", required=True, choices=('public', 'private', 'all'))
        parser.add_argument("info_type", required=True, choices=('short', 'detail'))
        parser.add_argument("api_key", required=True)
        parser.add_argument("user_id", required=True, type=int)
        args = parser.parse_args()
        session = db_session.create_session()
        user_by_api = check_api_key(args['api_key'])
        if args['habit_type'] == 'private':
            if args['user_id'] != user_by_api.id:
                abort(403, message='Invalid api key')
            habits = session.query(Habit).filter(Habit.user_id == args['user_id'],
                                                 Habit.type == args['habit_type']).all()
        elif args['habit_type'] == 'all':
            if args['user_id'] != user_by_api.id:
                abort(403, message='Invalid api key')
            habits = session.query(Habit).filter(Habit.user_id == args['user_id']).all()
        else:
            habits = session.query(Habit).filter(Habit.user_id == args['user_id'],
                                                 Habit.type == args['habit_type']).all()
        # voted_list = []
        # for habit in habits:
        #     if habit.voted_users is not None:
        #         already_voted = [i.split(':') for i in habit.voted_users.split(', ')]
        #         if any(str(user_by_api.id) in i[0] for i in already_voted):
        #             voted_list.append({'voted': True, 'vote_type': [i[1] for i in already_voted if str(user_by_api.id) in i[0]]})
        #         else:
        #             voted_list.append({'voted': False, 'vote_type': None})
        #     else:
        #         voted_list.append({'voted': False, 'vote_type': None})
        if args['info_type'] == 'detail':
            return jsonify({'habits': [habit.to_dict(
                only=('id', 'name', 'start_date', 'description', 'pluses', 'minuses', 'type', 'booting', 'weekdays',
                      'notify_time', 'votes', 'reputation', 'muted')) for habit in habits]})
        else:
            return jsonify({'habits': [habit.to_dict(
                only=('id', 'name', 'type', 'booting', 'weekdays',
                      'notify_time', 'reputation', 'muted')) for habit in habits]})

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        parser.add_argument("name", required=True)
        parser.add_argument("description", required=False)
        parser.add_argument("pluses", required=False)
        parser.add_argument("minuses", required=False)
        parser.add_argument("type", required=True, choices=('public', 'private'))
        parser.add_argument("weekdays", required=True)
        parser.add_argument("notify_time", required=True)
        parser.add_argument("muted", required=True, type=bool)
        args = parser.parse_args()
        if args['type'] == 'public':
            booting = True
        else:
            booting = False
        start_date = datetime.datetime.today()
        session = db_session.create_session()
        user = check_api_key(args['api_key'])
        if user.habit_limit == 0:
            return jsonify({'result': 'FAIL', 'message': 'you have already reached the limit for adding habits'})
        habit = Habit()
        habit.name = args['name']
        habit.description = args['description']
        habit.pluses = args['pluses']
        habit.minuses = args['minuses']
        habit.type = args['type']
        habit.booting = booting
        habit.weekdays = args['weekdays']
        habit.notify_time = args['notify_time']
        habit.start_date = start_date
        habit.muted = args['muted']
        user.habit_limit -= 1
        user.habits.append(habit)
        session.merge(user)
        session.commit()
        habit_id_return = session.query(Habit).filter(Habit.user_id == user.id).all()[-1].id
        return jsonify({'result': 'OK', 'message': {'habit_id': f"{habit_id_return}"}})
