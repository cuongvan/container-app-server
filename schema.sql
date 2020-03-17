CREATE TABLE IF NOT EXISTS app_info (
    app_id TEXT PRIMARY KEY,
    app_name TEXT NOT NULL,
    ava_url TEXT,
    type TEXT,
    slug_name TEXT,
    code_path TEXT,
    image TEXT,
    image_id TEXT,
    owner TEXT,
    description TEXT,
    language TEXT,
    status TEXT
);

CREATE TABLE IF NOT EXISTS app_param (
    app_id TEXT REFERENCES app_info(app_id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    label TEXT NOT NULL,
    description TEXT,
    PRIMARY KEY(app_id, name)
);

CREATE TABLE IF NOT EXISTS app_call (
    call_id TEXT PRIMARY KEY,
    app_id TEXT NOT NULL,
    user_id TEXT,
    elapsed_seconds BIGINT,
    call_status TEXT,
    output TEXT
);

CREATE TABLE IF NOT EXISTS call_param (
    call_id TEXT REFERENCES app_call(call_id) ON DELETE CASCADE,
    param_name TEXT NOT NULL,
    text_value TEXT,
    file_path TEXT,
    PRIMARY KEY(call_id, param_name)
);

-- app_call.status: STARTED, SUCCESS, FAILED

-- DROP TABLE IF EXISTS app_info;
-- DROP TABLE IF EXISTS app_call;
-- GRANT ALL PRIVILEGES ON TABLE app_info TO ckan_default;
-- GRANT ALL PRIVILEGES ON TABLE app_call TO ckan_default;
