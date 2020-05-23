from requests import get, post
import json

shit = '''{"name": "genadiy",
                 "email": "asgard1@ori.ru",
                 "password": "123"}'''


print(post('http://127.0.0.1:8080/api/users',
           json=json.loads(shit)).json())
print(get('http://127.0.0.1:8080/api/users').json())