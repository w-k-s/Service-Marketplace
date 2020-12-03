CREATE TABLE service_order (
    order_id VARCHAR(255) PRIMARY KEY,
    address_external_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    title VARCHAR(255),
    description VARCHAR(255),
    order_date_time timestamp without time zone,
    service_category_id bigint NOT NULL,
    status VARCHAR(255),
    amount numeric(19,2),
    currency VARCHAR(255),
    reject_reason VARCHAR(255),
    scheduled_service_provider_id BIGINT,
    created_by VARCHAR(255),
    created_date timestamp without time zone,
    last_modified_by VARCHAR(255),
    last_modified_date timestamp without time zone,
    version BIGINT NOT NULL
);

CREATE TABLE address (
    external_id BIGSERIAL PRIMARY KEY,
    customer_external_id bigint NOT NULL,
    name VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    latitude numeric(9,5),
    longitude numeric(9,5),
    version BIGINT NOT NULL
);

-- AXON

CREATE TABLE token_entry (
    processor_name VARCHAR(255),
    segment integer,
    owner VARCHAR(255),
    timestamp VARCHAR(255) NOT NULL,
    token oid,
    token_type VARCHAR(255),
    CONSTRAINT token_entry_pkey PRIMARY KEY (processor_name, segment)
);

CREATE TABLE saga_entry (
    saga_id VARCHAR(255) PRIMARY KEY,
    revision VARCHAR(255),
    saga_type VARCHAR(255),
    serialized_saga oid
);

CREATE TABLE association_value_entry (
    id bigint PRIMARY KEY,
    association_key VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id VARCHAR(255) NOT NULL,
    saga_type VARCHAR(255)
);
CREATE INDEX idx_saga_type_asociate_key_association_value ON association_value_entry(saga_type,association_key text_ops,association_value text_ops);
CREATE INDEX idx_saga_id_saga_type ON association_value_entry(saga_id text_ops,saga_type text_ops);
