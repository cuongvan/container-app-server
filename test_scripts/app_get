#! /usr/bin/env python3
import requests, sys, shutil, tempfile, json

if len(sys.argv) == 1:
    r = requests.get('http://localhost:5001/app')
else:
    r = requests.get('http://localhost:5001/app/' + sys.argv[1])

print(json.dumps(r.json(), indent=4))