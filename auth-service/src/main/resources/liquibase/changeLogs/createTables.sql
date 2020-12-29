-- TABLES

-- Events

CREATE TABLE IF NOT EXISTS events(
    event_uuid VARCHAR(64) PRIMARY KEY NOT NULL,
	event_type VARCHAR(1000) NOT NULL,
	event_body JSON NOT NULL,
	entity_id VARCHAR(64) NOT NULL,
	entity_type VARCHAR(1000) NOT NULL,
	idempotency_id VARCHAR(64) UNIQUE,
	published BOOLEAN NOT NULL DEFAULT false,
	publish_after timestamp with time zone
);