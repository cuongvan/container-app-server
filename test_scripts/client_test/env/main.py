import os

env = os.environ
for var, value in env.items():
    if var.startswith('ckan.input'):
        print('{}: {}'.format(var, value))