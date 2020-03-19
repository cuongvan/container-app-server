#! /usr/bin/env python3
import requests, sys, shutil, tempfile, json

if len(sys.argv) == 1:
    r = requests.get('http://localhost:5001/call')
else:
    r = requests.get('http://localhost:5001/call/' + sys.argv[1])

print(json.dumps(r.json(), indent=4))