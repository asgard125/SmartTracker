from requests import get, post
import json
from hashlib import md5

a = md5('123'.encode())
print(a.hexdigest())

shit = '''{"name": "genadiy",
                 "email": "asgard1@ori.ru",
                 "password": "123"}'''
shit1 = '''{"email": "asgard1@ori.ru",
                 "password": "123"}'''
print(get('https://smarttracker.herokuapp.com/api/users'))
# print(get('http://127.0.0.1:8080/api/login',
#            json=json.loads(shit1)).json())
