CREATE SEQUENCE IF NOT EXISTS customer_external_id;

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

CREATE SEQUENCE IF NOT EXISTS address_external_id;

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

CREATE TABLE IF NOT EXISTS events(
    event_uuid VARCHAR(64) PRIMARY KEY NOT NULL,
	event_type VARCHAR(1000) NOT NULL,
	event_body JSON NOT NULL,
	entity_id VARCHAR(64) NOT NULL,
	entity_type VARCHAR(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox(
    message_uuid VARCHAR(64) PRIMARY KEY NOT NULL,
    message_type VARCHAR(64) NOT NULL,
	payload JSON NOT NULL,
	correlation_id VARCHAR(64),
	destination_exchange VARCHAR(255) NOT NULL,
	destination_routing_key VARCHAR(255),
	destination_queue VARCHAR(255),
	reply_exchange VARCHAR(255),
	reply_routing_key VARCHAR(255),
	reply_queue VARCHAR(255),
	dead_letter_exchange VARCHAR(255),
	dead_letter_routing_key VARCHAR(255),
	dead_letter_queue VARCHAR(255),
	published BOOLEAN NOT NULL
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

DROP TRIGGER IF EXISTS audit_customers ON public.customers;
create trigger audit_customers
BEFORE update on customers
for each row execute procedure audit_record();

DROP TRIGGER IF EXISTS audit_addresses ON public.addresses;
create trigger audit_addresses
BEFORE update on addresses
for each row execute procedure audit_record();