-- TABLES

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

CREATE TABLE IF NOT EXISTS saga(
    -- Uniquely identifies the instance of the operation being performed e.g. movie-ticket-booking-123
    transaction_id VARCHAR(64) PRIMARY KEY NOT NULL,
    -- The name of the operation being performed e.g. PurchaseMovieTickets
    saga_name VARCHAR(100) NOT NULL,
    -- Uniquely identifies the aggregate root involved in the operation e.g. bookingId: 123
    aggregate_id VARCHAR(64) NOT NULL,
    -- The name of the aggregate root involved in the operation e.g. Booking
    aggregate_type VARCHAR(1000) NOT NULL,
    -- the current state of the transaction e.g. RESERVING_SEATS
    state VARCHAR(255) NOT NULL,
    -- the maximum time that this transaction can remain in this state (optional) e.g. RESERVING_SEATS (max: 1 min)
    deadline TIMESTAMP WITH TIME ZONE,
    -- when deadline has passed, this event will be sent in the body of the deadline event.
    -- e.g. If seats not reserved in  1 minute, cancel booking.
    event_id VARCHAR(64) REFERENCES events(event_uuid)
);