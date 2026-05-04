CREATE TABLE notifications (
    id           BIGSERIAL     PRIMARY KEY,
    title        VARCHAR(500)  NOT NULL,
    message      TEXT          NOT NULL,
    type         VARCHAR(50)   NOT NULL,
    status       VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    recipient_id BIGINT        NOT NULL REFERENCES app_users(id),
    channel      VARCHAR(100),
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    sent_at      TIMESTAMP,
    read_at      TIMESTAMP
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_status    ON notifications(status);
CREATE INDEX idx_notifications_created   ON notifications(created_at DESC);
