CREATE TABLE users (
  id BIGSERIAL primary key NOT NULL,
  username varchar(255) DEFAULT NULL,
  avatar varchar(255) DEFAULT NULL,
  public_key VARCHAR(255) NOT NULL
);

CREATE TABLE user_verification (
  id BIGSERIAL primary key NOT NULL,
  verification_code varchar(255) DEFAULT NULL,
  phone_number varchar(255) DEFAULT NULL,
  expiration_at timestamp DEFAULT NULL,
  challenge varchar(255) DEFAULT NULL,
  phone_verified boolean DEFAULT FALSE,
  user_id BIGINT DEFAULT NULL
);