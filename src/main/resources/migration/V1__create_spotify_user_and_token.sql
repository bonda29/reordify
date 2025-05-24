CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE spotify_users
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    spotify_id    VARCHAR(255) UNIQUE      NOT NULL,
    display_name  VARCHAR(255),
    created_date  TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE spotify_tokens
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id       UUID UNIQUE              NOT NULL REFERENCES spotify_users (id) ON DELETE CASCADE,
    access_token  TEXT                     NOT NULL,
    refresh_token TEXT                     NOT NULL,
    expiry        TIMESTAMP WITH TIME ZONE NOT NULL,
    created_date  TIMESTAMP WITH TIME ZONE NOT NULL,
    modified_date TIMESTAMP WITH TIME ZONE NOT NULL
);