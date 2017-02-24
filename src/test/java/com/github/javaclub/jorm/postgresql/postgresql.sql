
CREATE TABLE t_user (
  id INTEGER PRIMARY KEY,
  name VARCHAR(50),
  sex VARCHAR(4),
  age INTEGER,
  career VARCHAR(50)
);

CREATE TABLE t_id_increment_auto_generated (
  id SERIAL PRIMARY KEY,
  name VARCHAR
);