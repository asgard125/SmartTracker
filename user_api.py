from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.__all_models import User


def check_api_key(api_key):
    user = User.get_by_api(api_key)
    print(user)
    if user is None:
        abort(403, message="Invalid api key")
    return user


def abort_if_user_not_found(id):
    session = db_session.create_session()
    items = session.query(User).get(id)
    if not items:
        abort(404, message=f"User with id {id} not found")


class UserResource(Resource):
    def get(self, id):
        parser = reqparse.RequestParser()
        parser.add_argument("info_type", required=True, choices=('private', 'public'))  # public/private
        parser.add_argument("api_key", required=True)
        args = parser.parse_args()
        user_by_api = check_api_key(args['api_key'])
        if id == 0:
            return jsonify({'user': user_by_api.to_dict(
                only=('id', 'login', 'name', 'rating'))})
        abort_if_user_not_found(id)
        session = db_session.create_session()
        user_by_id = session.query(User).get(id)
        if args['info_type'] == 'private':
            if user_by_id.api_key != args['api_key']:
                abort(403, message='Invalid api key')
            return jsonify({'user': user_by_id.to_dict(
                only=('id', 'login', 'name', 'rating'))})
        return jsonify({'user': user_by_id.to_dict(
            only=('id', 'name', 'rating'))})


class UserListResource(Resource):
    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        args = parser.parse_args()
        check_api_key(args['api_key'])
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'rating')) for user in users]})
