from requests import get, post, delete, put
import json
from hashlib import md5


def test(method, address, json_args):
    if method == 'get':
        print(get(address, json=json.loads(json_args)).json())
    elif method == 'post':
        print(post(address, json=json.loads(json_args)).json())
    elif method == 'put':
        print(put(address, json=json.loads(json_args)).json())
    elif method == 'delete':
        print(delete(address, json=json.loads(json_args)).json())


local_address = 'http://127.0.0.1:8080/'
server_address = 'https://smarttracker.herokuapp.com/'

shit = '''{"name": "genadiy",
                 "login": "asgard3@ori.ru",
                 "password": "123"}'''
shit1 = '''{"email": "asgard3@ori.ru",
                 "password": "123"}'''
shit2 = '''{"api_key": "gFBDVvJaOQ_1"}'''
shit3 = '''{"api_key": "gFBDVvJaOQ_1", "user_id": "1", "habit_type": "all", "info_type": "detail"}'''
habit_shit = '''{"description": "anal sex", "name": "chlen", "type": "public", "weekdays": "1, 0", "notify_time": "19:00", "api_key": "gFBDVvJaOQ_1"}'''
test('post', f'{server_address}/register', shit)
# test('get', f'{local_address}/login', shit1)
# test('get', f'{local_address}/api/v1/users', shit2)
# # test('get', f"{local_address}/api/v1/habit/1", shit2)
# test('get', f"{local_address}/api/v1/habit/1", shit2)
# test('post', f"{local_address}/api/v1/habits", habit_shit)
# test('get', f"{local_address}/api/v1/habits", shit3)


# from apscheduler.schedulers.background import BackgroundScheduler
#
# def update_server():
#     print('Update background task: ', get('https://smarttracker.herokuapp.com/'))
#
#
# scheduler = BackgroundScheduler()
# scheduler.add_job(func=update_server, trigger="interval", minutes=15)
# scheduler.start()
# import atexit
# atexit.register(lambda: scheduler.shutdown())


# from os import environ, listdir, getcwd
# from json import load
#
# if 'config.json' in listdir(getcwd()):
#     with open('config.json', 'r', encoding='UTF-8') as file:
#         json = load(file)
#         for key, value in json.items():
#             environ[key] = value