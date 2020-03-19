import os, json

print()

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
    else:
        value = env_val
    
    print('{:30s}\t: {}\t{}'.format(env_key, type(value).__name__, value))
    param = env_key.rpartition('.')[2]
    params[param] = value


# for param, value in params:
#     print('{:30s}\t: {}\t{}'.format(param, type(value).__name__, value))

from pprint import pprint
pprint(params)