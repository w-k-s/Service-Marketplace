CREATE SEQUENCE customer_external_id;

CREATE TABLE IF NOT EXISTS customers(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE address_external_id;

CREATE TABLE IF NOT EXISTS addresses(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	customer_external_id BIGSERIAL NOT NULL REFERENCES customers(external_id) ON UPDATE cascade ON DELETE cascade,
	name VARCHAR(50) NOT NULL check (length(name) >= 2),
	line_1 VARCHAR(100) NOT NULL check (length(line_1) >= 2),
	line_2 VARCHAR(100),
	city VARCHAR(60) NOT NULL check(length(city) >= 2),
	country_code VARCHAR(2) NOT NULL check(length(country_code) = 2),
	latitude NUMERIC(9,5) NOT NULL check(latitude >= -90.0 AND latitude <= 90.0),
    longitude NUMERIC(9,5) NOT NULL check(latitude >= -180.0 AND latitude <= 180.0),
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

create or replace function audit_record()
returns trigger as $body$
  begin
    IF (TG_OP = 'UPDATE') THEN
        new.version = old.version + 1;
        new.last_modified_date = now() at time zone 'utc';
    ELSIF (TG_OP = 'INSERT') THEN
         new.created_date = now() at time zone 'utc';
    END IF;
    return new;
  end
$body$
language plpgsql;

create trigger audit_customers
BEFORE update on customers
for each row execute procedure audit_record();

create trigger audit_addresses
BEFORE update on addresses
for each row execute procedure audit_record();