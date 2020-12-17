CREATE TABLE service_order (
    order_uuid VARCHAR(255) PRIMARY KEY,
    customer_uuid VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    description VARCHAR(255),
    order_date_time timestamp without time zone,
    service_code VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    price VARCHAR(255),
    reject_reason VARCHAR(255),
    scheduled_service_provider_id BIGINT,
    created_by VARCHAR(255),
    created_date timestamp without time zone,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp without time zone,
    version BIGINT NOT NULL
);

CREATE TABLE address (
    order_uuid VARCHAR(255) REFERENCES service_order(order_uuid),
    city VARCHAR(255),
    country VARCHAR(255),
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    latitude numeric(9,5),
    longitude numeric(9,5),
    version BIGINT NOT NULL
);