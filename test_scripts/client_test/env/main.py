import os

print()
for env_key, env_val in os.environ.items():
    if not env_key.startswith('ckan.input'):
        continue

    if env_key.startswith('ckan.input.text'):
        value = env_val
    if env_key.startswith('ckan.input.number'):
        value = float(env_val)
    else:
        value = env_val

    print('{}: ({}) {}'.format(env_key, type(value), value))