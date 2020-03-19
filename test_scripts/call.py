#! /usr/bin/env python3
import requests, sys, json

app_id = sys.argv[1]
r = requests.post('http://localhost:5001/app/{}/execute'.format(app_id), files={
    'algorithm': (None, 'Do anything you like'),
    'file': ('file', open('../README.md', 'rb'))
})

print(json.dumps(r.json(), indent=4))