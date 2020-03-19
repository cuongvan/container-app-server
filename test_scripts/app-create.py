#! /usr/bin/env python3
import requests, sys, shutil, tempfile, json

app_info = {
    "app_name": "Anonymize data file",
    "language": "PYTHON",
    "params": [
        {
            "name": "algorithm",
            "type": "KEY_VALUE",
            "label": "Algorithm",
            "description": "Algorithm to anonymize dataset"
        },
        {
            "name": "file",
            "type": "FILE",
            "label": "File to anonymize",
            "description": "A csv data file"
        }
    ]
}

code_dir = '../example_apps/python/hello-world'

temp_name = tempfile.mkstemp()[1]
zip_file = shutil.make_archive(temp_name, 'zip', root_dir=code_dir, base_dir='./')

open(zip_file, 'rb')

r = requests.post('http://localhost:5001/app/create', files={
    'app_info': (None, json.dumps(app_info)),
    'code_file': ('code.zip', open(zip_file, 'rb'))
})

print(json.dumps(r.json(), indent=4))