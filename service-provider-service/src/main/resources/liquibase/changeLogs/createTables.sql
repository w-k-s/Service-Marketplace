CREATE SEQUENCE service_provider_external_id;

CREATE TABLE IF NOT EXISTS company(
    id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(320) NOT NULL UNIQUE,
	phone VARCHAR(18) NOT NULL UNIQUE,
	logo_url VARCHAR(2048) NOT NULL,
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
)

CREATE TABLE IF NOT EXISTS employee(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(320) NOT NULL UNIQUE,
	phone VARCHAR(18) NOT NULL UNIQUE,
	is_admin BOOLEAN NOT NULL DEFAULT FALSE,
	company_external_id BIGSERIAL NOT NULL REFERENCES company(external_id) ON UPDATE cascade ON DELETE RESTRICT,
	created_date timestamp without time zone default (now() at time zone 'utc'),
	created_by VARCHAR(255) NOT NULL,
	last_modified_date timestamp without time zone default (now() at time zone 'utc'),
	last_modified_by VARCHAR(255),
	version INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE address_external_id;

CREATE TABLE IF NOT EXISTS address(
	id BIGSERIAL PRIMARY KEY NOT NULL,
	external_id BIGSERIAL NOT NULL UNIQUE,
	uuid VARCHAR(64) NOT NULL UNIQUE,
	company_external_id BIGSERIAL NOT NULL REFERENCES company(external_id) ON UPDATE cascade ON DELETE RESTRICT,
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

CREATE TABLE IF NOT EXISTS category(
    code VARCHAR(6) PRIMARY KEY NOT NULL,
    name VARCHAR(60) NOT NULL UNIQUE
)

CREATE TABLE IF NOT EXISTS company_category(
    company_external_id BIGSERIAL NOT NULL REFERENCES company(external_id),
    category_code NOT NULL VARCHAR(6) REFERENCES category(code)
)

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

create trigger audit_company
BEFORE update on company
for each row execute procedure audit_record();

create trigger audit_employee
BEFORE update on employee
for each row execute procedure audit_record();

create trigger audit_address
BEFORE update on address
for each row execute procedure audit_record();