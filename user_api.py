from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.__all_models import User


def check_api_key(api_key):
    user = User.get_by_api(api_key)
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
                only=('id', 'login', 'name', 'rating', 'habit_limit'))})
        abort_if_user_not_found(id)
        session = db_session.create_session()
        user_by_id = session.query(User).get(id)
        if args['info_type'] == 'private':
            if user_by_id.api_key != args['api_key']:
                abort(403, message='Invalid api key')
            return jsonify({'user': user_by_id.to_dict(
                only=('id', 'login', 'name', 'rating', 'habit_limit'))})
        return jsonify({'user': user_by_id.to_dict(
            only=('id', 'name', 'rating'))})


class UserListResource(Resource):
    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument("api_key", required=True)
        parser.add_argument("limit", required=True, type=int)
        parser.add_argument("offset", required=True, type=int)
        args = parser.parse_args()
        user_by_api = check_api_key(args['api_key'])
        session = db_session.create_session()
        users = session.query(User).order_by(-User.rating).all()
        total_users_len = len(users)
        next_offset = min(total_users_len, args['offset'] + args['limit'])
        place = 0
        for i in users:
            if i.id == user_by_api.id:
                break
            place += 1
        users_to_return = users[args['offset']: next_offset]
        if next_offset == total_users_len:
            next_offset = 0
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'rating')) for user in users_to_return], 'nextOffset': f'{next_offset}', 'total': f'{total_users_len}', 'current_user_place': f'{place + 1}', 'current_user_rating': f'{user_by_api.rating}'})
