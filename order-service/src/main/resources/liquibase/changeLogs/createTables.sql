CREATE TABLE service_order (
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