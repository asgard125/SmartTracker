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

app = Flask(__name__)
app.config['SECRET_KEY'] = 'megumin'
api = Api(app)
db_session.global_init("db/stdb.sqlite")


@app.route('/', methods=['GET'])
def check():
    return "Hello world"


@app.route('/register', methods=['POST'])
def register():
    parser = reqparse.RequestParser()
    parser.add_argument("login", required=True)
    parser.add_argument("password", required=True)
    parser.add_argument("name", required=True)
    args = parser.parse_args()
    session = db_session.create_session()
    check_user = session.query(User).filter(User.email == args['login'].lower().strip()).first()
    if check_user:
        return jsonify({'result': 'FAIL', 'message': 'user with this login already exists'})
    user = User(
        name=args['name'],
        email=args['login'].lower().strip(),
        password=generate_password_hash(args['password'])
    )
    session.add(user)
    session.commit()
    return jsonify({'result': 'OK'})


@app.route('/login', methods=['GET'])
def login():
    parser = reqparse.RequestParser()
    parser.add_argument("email", required=True)
    parser.add_argument("password", required=True)
    args = parser.parse_args()
    session = db_session.create_session()
    user = session.query(User).filter(User.email == args['email']).first()
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
    if habit.user_id == user.id:
        return jsonify({'result': 'FAIL', 'message': 'cant vote on self habit'})
    if habit.voted_users is not None:
        already_voted = habit.voted_users.split(', ')
        if str(user.id) in already_voted:
            return jsonify({'result': 'FAIL', 'message': 'already voted on this habit'})
        else:
            habit.voted_users += f', {user.id}'
    else:
        habit.voted_users = str(user.id)
    if args['vote_type'] == 'positive':
        habit.reputation += 1
    else:
        habit.reputation -= 1
    session.commit()
    return jsonify({'result': "OK"})


@app.route('/habit_completed/<int:habit_id>', methods=['PUT'])
def habit_completed(habit_id):
    parser = reqparse.RequestParser()
    parser.add_argument("api_key", required=True)


api.add_resource(user_api.UserListResource, '/api/v1/users')
api.add_resource(user_api.UserResource, '/api/v1/user/<int:id>')
api.add_resource(habit_api.HabitResource, '/api/v1/habit/<int:id>')
api.add_resource(habit_api.HabitListResource, '/api/v1/habits')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get("PORT", 5000)))
    # app.run(host='127.0.0.1', port=int(os.environ.get("PORT", 8080)), debug=True)
