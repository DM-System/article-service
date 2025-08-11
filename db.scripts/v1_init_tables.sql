CREATE SCHEMA if not exists document_metadata;

CREATE TABLE if not exists document_metadata.metadata (
    id UUID PRIMARY KEY,
    author_name VARCHAR(250),
    record_type VARCHAR(80) NOT NULL,
    record_state VARCHAR(80) NOT NULL,

    -- Auditable fields (Spring Data JPA standard)
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(250),
    last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(250)
);

-- Create record_state table for AWS Aurora DSQL (PostgreSQL)
CREATE TABLE if not exists document_metadata.record_state (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,

    -- Auditable fields (Spring Data JPA standard)
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(250),
    last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(250)
);


-- Create record_type table in the app_metadata schema
CREATE TABLE if not exists document_metadata.record_type (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,

    -- Auditable fields (Spring Data JPA standard)
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(250),
    last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(250)

);


-- Create users table in the app_metadata schema
CREATE table if not exists document_metadata.users (
    id UUID PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    f_name VARCHAR(60),
    l_name VARCHAR(60),
    linkedin VARCHAR(500),

    -- Auditable fields (Spring Data JPA standard)
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(250),
    last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(250)

);

-- Create assets table in the app_metadata schema
CREATE TABLE if not exists document_metadata.assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    metadata_id UUID NOT NULL,
    record_type VARCHAR(80) NOT NULL,
    path TEXT NOT NULL,

    -- Auditable fields (Spring Data JPA standard)
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(250),
    last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(250)
);
