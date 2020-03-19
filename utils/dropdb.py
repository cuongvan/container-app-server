#! /usr/bin/env python3

from sqlalchemy import create_engine
db = create_engine("postgres://ckan_default:ckan_default@localhost:5432/ckan_default")
db.execute('DROP TABLE IF EXISTS app_info, app_param, app_call, call_param')
