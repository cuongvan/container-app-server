import os
import json


# input
_input_params = json.loads(os.getenv('CKANAPP_DATA', '{"params": []}'))

def get_input_param(field_name):
    params = _input_params.get('params')
    for param in params:
        if param['name'] == field_name:
            return param['value']
    return None

## output
# param: {type, name, value: str}
_output_params = {
    'params': []
}


def _put_output_field(type, name, value: str):
    _output_params['params'].append({
        'type': type,
        'name': name,
        'value': value,
    })

    with open('/outputs/output.json', 'w') as f:
        json.dump(_output_params, f, indent=4, ensure_ascii=False)


# public
class InvalidOutputParamType(Exception):
    pass
    
def _ensure_type(obj, type_):
    if not isinstance(obj, type_):
        raise InvalidOutputParamType('Invalid type: expected: {}, actual: {}'.format(type_.__name__, obj.__class__.__name__))

def add_text_output(name, value):
    _ensure_type(value, str)
    _put_output_field('TEXT', name , value)


def add_list_output(name, value):
    _ensure_type(value, list)
    _put_output_field('LIST', name, json.dumps(value, ensure_ascii=False))


def add_boolean_output(name, value):
    _ensure_type(value, bool)
    _put_output_field('BOOLEAN', name, json.dumps(value))


def add_integer_output(name, value):
    _ensure_type(value, int)
    _put_output_field('INTEGER', name, str(value))


def add_double_output(name, value):
    _ensure_type(value, float)
    _put_output_field('DOUBLE', name, str(value))


def add_file_output_as_bytes(file_name, value):
    _ensure_type(value, bytes)
    with open('/outputs/files/' + file_name, 'wb') as f:
        f.write(value)
    

def add_file_output_as_path(file_name, value):
    _ensure_type(value, str)
    if not os.path.isfile(value):
        raise ValueError('File not exists')
    os.link(value, '/outputs/files/' + file_name)
