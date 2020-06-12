```postgresql
CREATE DATABASE account;
CREATE EXTENSION postgis;

CREATE SEQUENCE customer_external_id;

CREATE TABLE IF NOT EXISTS customers(
	id BIGSERIAL PRIMARY KEY NOT NULL,
    external_id BIGSERIAL NOT NULL UNIQUE,
    uuid VARCHAR(64) NOT NULL UNIQUE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	created_date timestamp with time zone NOT NULL,
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp with time zone,
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE address_external_id;

CREATE TABLE IF NOT EXISTS addresses(
	id BIGSERIAL PRIMARY KEY NOT NULL,
    external_id BIGSERIAL NOT NULL UNIQUE,
	customer_external_id BIGSERIAL NOT NULL REFERENCES customers(external_id) ON UPDATE cascade ON DELETE cascade,
	name VARCHAR(50) NOT NULL check (length(name) >= 2),
	line_1 VARCHAR(100) NOT NULL check (length(line_1) >= 2),
	line_2 VARCHAR(100),
    city VARCHAR(60) NOT NULL check(length(city) >= 2),
    country_code VARCHAR(2) NOT NULL check(length(country_code) = 2),
    location geography NOT NULL,
	created_date timestamp with time zone NOT NULL,
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp with time zone,
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

create or replace function increment_version()
returns trigger as $body$
  begin
    new.version = old.version + 1;
    return new;
  end
$body$
language plpgsql;

create trigger update_customers_version 
BEFORE update on customers
for each row execute procedure increment_version();

create trigger update_addresses_version 
BEFORE update on addresses
for each row execute procedure increment_version();
```
TODO: createdDate, lastModifiedBy using trigger (using utc)

- https://stackoverflow.com/a/47396542