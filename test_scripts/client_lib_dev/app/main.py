import os, json, pathlib

print()


def get_input_params():
    params = {}
    for env_key, env_val in os.environ.items():
        if not env_key.startswith('ckan.input'):
            continue

        if env_key.startswith('ckan.input.text'):
            value = env_val
        elif env_key.startswith('ckan.input.textlist') or env_key.startswith('ckan.input.numberlist'):
            value = json.loads(env_val)
        elif env_key.startswith('ckan.input.number'):
            value = float(env_val)
        elif env_key.startswith('ckan.input.boolean'):
            value = True if env_val == 'true' else False
        elif env_key.startswith('ckan.input.file'):
            with open(env_val, 'rb') as f:
                value = f.read()
        param = env_key.rpartition('.')[2]
        params[param] = value

    return params

OUTPUT_ROOT = pathlib.Path('/output')
OUTPUT_INFO_FILE = OUTPUT_ROOT / "metadata"
OUTPUT_FILES_DIR = OUTPUT_ROOT / "files"

OUTPUT_ROOT.mkdir(exist_ok=True)
OUTPUT_FILES_DIR.mkdir(exist_ok=True)

def read_output_meta():
    with open(OUTPUT_INFO_FILE) as f:
        return json.load(f)

def write_output_meta(meta):
    with open(OUTPUT_INFO_FILE, 'w') as f:
        f.write(json.dumps(meta, indent=4))

write_output_meta({})


def put_key_value(key, value):
    meta = read_output_meta()
    write_output_meta({
        **read_output_meta(),
        key: value
    })

def put_text(text):
    put_key_value('TEXT', text)

def put_file(filename, data: bytes):
    with open(OUTPUT_FILES_DIR / filename, 'wb') as f:
        f.write(data)

from pprint import pprint
pprint(get_input_params())

# put_key_value('myoutput', 'Hello you')
# put_file('myfile', b'this is the file content')
put_text('''
    This is a multiline text,
    looks good!
''')