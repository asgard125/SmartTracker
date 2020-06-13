from flask import Flask, abort
from flask_restful import Api, reqparse
from flask import render_template
from flask_login import LoginManager, logout_user, login_required, login_user, current_user
from data import db_session
from data.__all_models import *
import os
import user_api
import habit_api
from flask import jsonify
from werkzeug.security import check_password_hash, generate_password_hash
from rating_formulas import habit_rating
from settings import SECRET_KEY
from apscheduler.schedulers.background import BackgroundScheduler
from validations import password_validation, login_validation, name_validation

app = Flask(__name__)
app.config['SECRET_KEY'] = SECRET_KEY
api = Api(app)
db_session.global_init("db/stdb.db")


@app.route('/', methods=['GET'])
def check():
    return "Hello world"


@app.route('/check_secret_key/<string:key>', methods=['GET'])
def check_secret_key(key):
    if key == app.config['SECRET_KEY']:
        return jsonify({'result': 'OK'})
    else:
        return jsonify({'result': 'FAIL', 'message': 'This version of the app is outdated, please visit https://vk.com/greamteamdev for more information'})


@app.route('/register', methods=['POST'])
def register():
    parser = reqparse.RequestParser()
    parser.add_argument("login", required=True)
    parser.add_argument("password", required=True)
    parser.add_argument("name", required=True)
    args = parser.parse_args()
    login = args['login'].strip()
    password = args['password'].strip()
    name = args['name'].strip()
    session = db_session.create_session()
    check_user = session.query(User).filter(User.login == login).first()
    if check_user:
        return jsonify({'result': 'FAIL', 'message': 'user with this login already exists'})
    login_valid = login_validation(login)
    if login_valid['result'] != 'OK':
        return jsonify({'result': {login_valid['result']}, 'message': {login_valid['message']}})
    password_valid = password_validation(password)
    if password_valid['result'] != 'OK':
        return jsonify({'result': {password_valid['result']}, 'message': {password_valid['message']}})
    name_valid = name_validation(name)
    if name_valid['result'] != 'OK':
        return jsonify({'result': {name_valid['result']}, 'message': {name_valid['message']}})
    user = User(
        name=name,
        login=login,
        password=generate_password_hash(password)
    )
    session.add(user)
    session.commit()
    return jsonify({'result': 'OK'})


@app.route('/login', methods=['GET'])
def login():
    parser = reqparse.RequestParser()
    parser.add_argument("login", required=True)
    parser.add_argument("password", required=True)
    args = parser.parse_args()
    session = db_session.create_session()
    user = session.query(User).filter(User.login == args['login'].strip()).first()
    if user and user.check_password(args['password']):
        return jsonify({'result': 'OK', 'message': {'api_key': user.generate_api_key()}})
    return jsonify({'result': 'FAIL', 'message': 'invalid login or password'})


@app.route('/vote_for_habit/<int:habit_id>', methods=['PUT'])
def vote_for_habit(habit_id):
    parser = reqparse.RequestParser()
    parser.add_argument("vote_type", required=True, choices=("positive", "negative"))
    parser.add_argument("api_key", required=True)
    args = parser.parse_args()
    session = db_session.create_session()
    habit = session.query(Habit).get(habit_id)
    user = User.get_by_api(args['api_key'])
    if user is None:
        abort(403, message='Invalid api key')
   # if user.vote_limit <= 0:
      #  return jsonify({'result': 'FAIL', 'message': 'vote limit was reached on this week'})
    if habit.user_id == user.id:
        return jsonify({'result': 'FAIL', 'message': 'cant vote on self habit'})
    if habit.voted_users is not None:
        already_voted = [i.split(':') for i in habit.voted_users.split(', ')]
        if any(str(user.id) in i[0] for i in already_voted):
            return jsonify({'result': 'FAIL', 'message': 'already voted on this habit'})
    else:
        habit.voted_users = str(user.id)
    if args['vote_type'] == 'positive':
        habit.reputation += 1
    else:
        habit.reputation -= 1
    habit.votes += 1
    habit.voted_users += f', {user.id}:{args["vote_type"]}'
    # user.change_data(vote_limit = user.vote_limit - 1)
    session.commit()
    return jsonify({'result': "OK"})


@app.route('/habit_completed/<int:habit_id>', methods=['PUT'])
def habit_completed(habit_id):
    parser = reqparse.RequestParser()
    parser.add_argument("api_key", required=True)
    session = db_session.create_session()
    args = parser.parse_args()
    # weekday_today = str(datetime.datetime.today().weekday())
    habit = session.query(Habit).filter(Habit.id == habit_id).first()
    user = User.get_by_api(args['api_key'])
    if habit is None:
        abort(404, 'habit not found')
    if user is None:
        abort(403, 'Invalid api key')
    if habit.user.api_key != args['api_key']:
        abort(403, 'Invalid api key')
    # if weekday_today not in habit.weekdays:
    #     return jsonify({'result': 'FAIL', 'message': 'This habit is not scheduled for today'})
    if habit.booting:
        user.change_data(rating=user.rating + habit_rating(len(habit.weekdays.split(', ')), habit.votes, habit.reputation))
    return jsonify({'result': 'OK'})


@app.route('/email_verification')
def email_verification():
    pass


api.add_resource(user_api.UserListResource, '/api/v1/users')
api.add_resource(user_api.UserResource, '/api/v1/user/<int:id>')
api.add_resource(habit_api.HabitResource, '/api/v1/habit/<int:id>')
api.add_resource(habit_api.HabitListResource, '/api/v1/habits')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get("PORT", 5000)))
    #app.run(host='127.0.0.1', port=int(os.environ.get("PORT", 8080)), debug=True)
