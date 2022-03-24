CREATE TABLE users (
  id BIGSERIAL primary key NOT NULL,
  username varchar(255) DEFAULT NULL,
  avatar bytea DEFAULT NULL,
  public_key bytea NOT NULL
);

CREATE TABLE user_verification (
  id BIGSERIAL primary key NOT NULL,
  verification_code varchar(255) DEFAULT NULL,
  phone_number bytea DEFAULT NULL,
  expiration_at timestamp with time zone DEFAULT NULL,
  challenge varchar(255) DEFAULT NULL,
  phone_verified boolean DEFAULT FALSE,
  user_id BIGINT DEFAULT NULL
);