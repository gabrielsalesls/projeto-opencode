ALTER TABLE notifications ADD COLUMN event_id VARCHAR NOT NULL DEFAULT '';

UPDATE notifications SET event_id = id::VARCHAR WHERE event_id = '';

ALTER TABLE notifications ADD CONSTRAINT uk_notifications_event_id UNIQUE (event_id);
