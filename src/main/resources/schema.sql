

CREATE TABLE IF NOT EXISTS users (
user_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email VARCHAR(200) NOT NULL,
login VARCHAR(50) NOT NULL,
name VARCHAR(200) NOT NULL,
birthday DATE NOT NULL,
CONSTRAINT users_pk PRIMARY KEY (user_id),
CONSTRAINT user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS friends (
user_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
friend_id INTEGER NOT NULL,
status BOOLEAN NOT NULL,
CONSTRAINT friends_user_id_fk FOREIGN KEY (user_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS films (
film_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(150) NOT NULL,
description VARCHAR(200) NOT NULL,
duration INTEGER NOT NULL,
release_date DATE NOT NULL,
rate INTEGER,
mpa_id INTEGER NOT NULL,
CONSTRAINT film_pk PRIMARY KEY (film_id)
);

CREATE TABLE IF NOT EXISTS genres (
genre_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(50),
CONSTRAINT genre_id PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
fg_id INTEGER NOT NULL GENERATED  BY DEFAULT AS IDENTITY PRIMARY KEY,
film_id INTEGER NOT NULL,
genre_id INTEGER NOT NULL,
CONSTRAINT film_genres_film_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id),
CONSTRAINT film_genres_genre_id_fk FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
film_id INTEGER NOT NULL,
user_id INTEGER NOT NULL,
CONSTRAINT likes_film_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id),
CONSTRAINT likes_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS mpa (
mpa_id INTEGER NOT NULL  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(10) NOT NULL,
description VARCHAR(200) NOT NULL,
CONSTRAINT mpa_pk PRIMARY KEY (mpa_id)
);





