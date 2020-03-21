import os, json

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

from pprint import pprint
pprint(get_input_params())