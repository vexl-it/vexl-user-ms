CREATE TABLE users (
  id BIGSERIAL primary key NOT NULL,
  username varchar(255) NOT NULL,
  avatar varchar(255) DEFAULT NULL,
  public_key VARCHAR(255) NOT NULL
);

CREATE TABLE user_verification (
  id BIGSERIAL primary key NOT NULL,
  verification_code varchar(255) NOT NULL,
  expiration_at timestamp DEFAULT NULL
);