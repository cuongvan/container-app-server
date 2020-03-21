import ckanapplib

from pprint import pprint
inputs = ckanapplib.get_input_params()
pprint(inputs)

ckanapplib.put_file('myfile', b'this is the file content')
ckanapplib.put_text('mytext', 'value of text param')
ckanapplib.put_list('mylist', [1, 2, 3])
ckanapplib.put_boolean('myboolean', True)
ckanapplib.put_number('mynumber1', 100)
ckanapplib.put_number('mynumber2', 999.999)