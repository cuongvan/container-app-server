import os

print()

params = {}
for env_key, env_val in os.environ.items():
    if not env_key.startswith('ckan.input'):
        continue


    if env_key.startswith('ckan.input.text'):
        value = env_val
    if env_key.startswith('ckan.input.number'):
        value = float(env_val)
    else:
        value = env_val
    
    print('{:30s}\t: {}\t{}'.format(env_key, type(value).__name__, value))
    param = env_key.rpartition('.')[2]
    params[param] = value


# for param, value in params:
#     print('{:30s}\t: {}\t{}'.format(param, type(value).__name__, value))

from pprint import pprint
pprint(params)