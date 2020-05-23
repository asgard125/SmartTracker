from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.__all_models import User
from werkzeug.security import generate_password_hash

db_session.global_init("db/smarttrackerdb.sqlite")

parser = reqparse.RequestParser()
parser.add_argument("email", required=True)
parser.add_argument("name", required=True)
parser.add_argument("password", required=True)


def abort_if_user_not_found(id):
    session = db_session.create_session()
    items = session.query(User).get(id)
    if not items:
        abort(404, message=f"User with id {id} not found")


class UserResource(Resource):
    def get(self, id):
        abort_if_user_not_found(id)
        session = db_session.create_session()
        user = session.query(User).get(id)
        return jsonify({'user': user.to_dict(
            only=('id', 'name', 'email'))})

    def delete(self, user_id):
        abort_if_user_not_found(user_id)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        session.delete(user)
        session.commit()
        return jsonify({'result': 'OK'})


class UserListResource(Resource):
    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'user': [user.to_dict(
            only=('id', 'name', 'email')) for user in users]})

    def post(self):
        args = parser.parse_args()
        session = db_session.create_session()
        check_user = session.query(User).filter(User.email == args['email']).first()
        if check_user:
            abort(405, message='User with this email already exists')
        user = User(
            name=args['name'],
            email=args['email'],
            password=generate_password_hash(args['password'])
        )
        session.add(user)
        session.commit()
        return jsonify({'result': 'OK'})
