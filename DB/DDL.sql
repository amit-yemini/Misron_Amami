DROP SCHEMA IF EXISTS msa CASCADE;

CREATE SCHEMA IF NOT EXISTS msa;

CREATE TYPE msa.alert_category AS ENUM ('SECURITY', 'ENVIRONMENTAL', 'OTHER');
CREATE TYPE msa.alert_event AS ENUM ('MISSILE', 'TEST', 'OTHER');

CREATE TABLE msa.launch_country (
    id           INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    external_id  INTEGER
);

CREATE TABLE msa.missle_type (
    id           INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    external_id  INTEGER
);

CREATE TABLE msa.alert_type (
    id                  INTEGER PRIMARY KEY,
    name                TEXT NOT NULL,
    category            msa.alert_category NOT NULL,
    event               msa.alert_event NOT NULL,
    distribution_time   INTEGER
);

CREATE TABLE msa.alert_to_missle (
    alert_type_id   INTEGER NOT NULL,
    missle_type_id  INTEGER NOT NULL,
    
    PRIMARY KEY (alert_type_id, missle_type_id),

    FOREIGN KEY (alert_type_id)
        REFERENCES msa.alert_type(id)
        ON DELETE CASCADE,

    FOREIGN KEY (missle_type_id)
        REFERENCES msa.missle_type(id)
        ON DELETE CASCADE
);