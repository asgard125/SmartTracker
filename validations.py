import sys


def password_validation(password):
    if len(password) < 6:
        return {'result': 'FAIL', 'message': 'Password must be at least 6 characters long'}
    if len(set(password)) <= 2:
        return {'result': 'FAIL', 'message': 'Password is too simple'}
    return {'result': 'OK'}


def login_validation(login):
    if len(login) < 3:
        return {'result': 'FAIL', 'message': 'Login must be at least 3 characters long'}
    if not login.isalnum():
        return {'result': 'FAIL', 'message': 'Login can only consist of letters and symbols'}
    return {'result': 'OK'}


def name_validation(name):
    if len(name) < 3:
        return {'result': 'FAIL', 'message': 'Name must be at least 3 characters long'}
    if not name.isalnum():
        return {'result': 'FAIL', 'message': 'Name can only consist of letters and symbols'}
    return {'result': 'OK'}


def email_validation(email):
    pass
