#! /usr/bin/env python3

from sqlalchemy import create_engine
db = create_engine("postgres://ckan_default:ckan_default@localhost:5432/ckan_default")

def main():
    drop()
    create()

def drop():
    db.execute('DROP TABLE IF EXISTS app_info, app_param, app_call, call_param')

def create():
    db.execute('''
        CREATE TABLE app_info (
            app_id TEXT PRIMARY KEY,
            app_name TEXT NOT NULL,
            avatar_path TEXT,
            type TEXT,
            slug_name TEXT,
            code_path TEXT,
            image TEXT,
            image_id TEXT,
            owner TEXT,
            description TEXT,
            language TEXT,
            app_status TEXT
        );
    ''')

    db.execute('''
        CREATE TABLE app_param (
            app_id TEXT REFERENCES app_info(app_id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            type TEXT NOT NULL,
            label TEXT NOT NULL,
            description TEXT,
            PRIMARY KEY(app_id, name)
        );
    ''')

    db.execute('''
        CREATE TABLE app_call (
            call_id TEXT PRIMARY KEY,
            app_id TEXT NOT NULL,
            user_id TEXT,
            elapsed_seconds BIGINT,
            call_status TEXT,
            output TEXT
        );
    ''')

    db.execute('''
        CREATE TABLE call_param (
            call_id TEXT REFERENCES app_call(call_id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            type TEXT NOT NULL,
            value TEXT,
            PRIMARY KEY(call_id, name)
        );
    ''')

main()
