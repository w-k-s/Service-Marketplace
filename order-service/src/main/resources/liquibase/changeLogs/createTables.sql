CREATE SEQUENCE IF NOT EXISTS server_order_id;
CREATE TABLE IF NOT EXISTS service_order (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE NOT NULL,
    customer_uuid VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    order_date_time timestamp with time zone NOT NULL,
    service_code VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    final_quote_currency VARCHAR(3),
    final_quote_amount_minor_units DECIMAL(19,0),
    reject_reason VARCHAR(255),
    scheduled_service_provider_id BIGINT,
    address_city VARCHAR(255) NOT NULL,
    address_country VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    address_latitude numeric(9,5) NOT NULL,
    address_longitude numeric(9,5) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp with time zone,
    version BIGINT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS quote_id;
CREATE TABLE IF NOT EXISTS quote (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE NOT NULL,
    order_id BIGSERIAL NOT NULL,
    company_id BIGSERIAL NOT NULL,
    note VARCHAR(255),
    currency VARCHAR(3) NOT NULL,
    amount_minor_units DECIMAL(18,0) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp with time zone,
    version BIGINT NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES service_order(id)
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

DROP TRIGGER IF EXISTS audit_service_order ON public.service_order;
create trigger audit_service_order
BEFORE update on service_order
for each row execute procedure audit_record();

DROP TRIGGER IF EXISTS audit_quote ON public.quote;
create trigger audit_quote
BEFORE update on quote
for each row execute procedure audit_record();