OUTPUT_ROOT = pathlib.Path('/outputs')
OUTPUT_INFO_FILE = OUTPUT_ROOT / "output.json"
OUTPUT_FILES_DIR = OUTPUT_ROOT / "files"

# init
OUTPUT_ROOT.mkdir(exist_ok=True)
OUTPUT_FILES_DIR.mkdir(exist_ok=True)
_write_output([])

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

def _write_output(meta):
    with open(OUTPUT_INFO_FILE, 'w') as f:
        f.write(json.dumps(meta, indent=4))

def put_key_value_(name, type, value: str):
    with open(OUTPUT_INFO_FILE) as f:
        outputs = json.load(f)
    outputs.append({
        'type': type,
        'name': name,
        'value': value,
    })

    _write_output(outputs)

def put_text(name, text: str):
    put_key_value_(name, 'TEXT', text)

def put_list(name, val: list):
    put_key_value_(name, 'LIST', str(val))

def put_boolean(name, val: bool):
    if val is True:
        text = 'true'
    else:
        text = 'false'

    put_key_value_(name, 'BOOLEAN', text)

def put_number(name, number):
    put_key_value_(name, 'NUMBER', str(number))

def put_file(filename, data: bytes):
    with open(OUTPUT_FILES_DIR / filename, 'wb') as f:
        f.write(data)