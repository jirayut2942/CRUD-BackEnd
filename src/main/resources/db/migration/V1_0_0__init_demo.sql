CREATE TABLE IF NOT EXISTS user_track (
    id serial PRIMARY KEY,
	username VARCHAR(50)  NOT NULL,
	password VARCHAR(50) NOT NULL,
	email VARCHAR(255)  NOT NULL
)