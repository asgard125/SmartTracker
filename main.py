from flask import Flask
from flask_restful import Api
from flask import render_template
from flask_login import LoginManager, logout_user, login_required, login_user, current_user
from data import db_session
from data.__all_models import *
import os
import user_api


app = Flask(__name__)
app.config['SECRET_KEY'] = 'megumin'
api = Api(app)

api.add_resource(user_api.UserListResource, '/api/users')
api.add_resource(user_api.UserResource, '/api/user/<int:id>')

if __name__ == '__main__':
    app.run(port=8080, host='127.0.0.1', debug=True)
