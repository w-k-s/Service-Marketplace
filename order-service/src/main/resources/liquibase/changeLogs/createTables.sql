CREATE TABLE IF NOT EXISTS service_order (
    order_uuid VARCHAR(255) PRIMARY KEY,
    customer_uuid VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    description VARCHAR(255),
    order_date_time timestamp with time zone,
    service_code VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    price VARCHAR(255),
    reject_reason VARCHAR(255),
    scheduled_service_provider_id BIGINT,
    address_city VARCHAR(255),
    address_country VARCHAR(255),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    address_latitude numeric(9,5),
    address_longitude numeric(9,5),
    created_by VARCHAR(255),
    created_date timestamp with time zone,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp with time zone,
    version BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS bid (
    bid_uuid VARCHAR(255) PRIMARY KEY,
    order_uuid VARCHAR(255) NOT NULL,
    company_uuid VARCHAR(255) NOT NULL,
    note VARCHAR(255),
    price VARCHAR(255),
    created_by VARCHAR(255),
    created_date timestamp with time zone,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp with time zone,
    version BIGINT NOT NULL
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

DROP TRIGGER IF EXISTS audit_bid ON public.bid;
create trigger audit_bid
BEFORE update on bid
for each row execute procedure audit_record();