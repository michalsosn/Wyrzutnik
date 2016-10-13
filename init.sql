DROP TABLE dumpster;
DROP TABLE route;
DROP TABLE weather;
DROP TABLE account;
DROP TABLE authority;

-- SELECT InitSpatialMetaData();
CREATE TABLE dumpster (
    dumpster_id INTEGER PRIMARY KEY,
    intelligent BOOLEAN NOT NULL,
    capacity REAL,
    used REAL,
    collection_period_in_days INTEGER,
    last_collected DATE
);
SELECT AddGeometryColumn('dumpster', 'point', 4326, 'POINT', 'XY', 1);

CREATE TABLE route (
    route_id INTEGER PRIMARY KEY,
    length_in_meters INTEGER,
    travel_time_in_seconds INTEGER
);
SELECT AddGeometryColumn('route', 'line_string', 4326, 'LINESTRING', 'XY', 1);

CREATE TABLE weather (
    weather_id INTEGER PRIMARY KEY,
    comment TEXT NOT NULL
);
SELECT AddGeometryColumn('weather', 'polygon', 4326, 'POLYGON', 'XY', 1);

creATE TABLE account (
    account_id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE authority (
    authority_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    account_id INTEGER REFERENCES account(account_id)
);
