from flask import Flask, abort
from flask_restful import Api, reqparse
from flask import render_template
from flask_login import LoginManager, logout_user, login_required, login_user, current_user
from data import db_session
from data.__all_models import *
import os
import user_api
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
    parser.add_argument("email", required=True)
    parser.add_argument("password", required=True)
    parser.add_argument("name", required=True)
    args = parser.parse_args()
    session = db_session.create_session()
    check_user = session.query(User).filter(User.email == args['email']).first()
    if check_user:
        return jsonify({'result': 'Fail', 'message': 'user with this email already exists'})
    user = User(
        name=args['name'],
        email=args['email'],
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
        return jsonify({'result': 'OK', 'api_key': user.generate_api_key()})
    return jsonify({'result': 'Fail'})


api.add_resource(user_api.UserListResource, '/api/v1/users')
api.add_resource(user_api.UserResource, '/api/v1/user/<int:id>')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get("PORT", 5000)))
#  app.run(host='127.0.0.1', port=int(os.environ.get("PORT", 8080)), debug=True)
