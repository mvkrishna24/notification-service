CREATE TABLE notification_preferences (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT    NOT NULL UNIQUE REFERENCES app_users(id),
    email_enabled  BOOLEAN   NOT NULL DEFAULT TRUE,
    push_enabled   BOOLEAN   NOT NULL DEFAULT TRUE,
    sms_enabled    BOOLEAN   NOT NULL DEFAULT FALSE,
    in_app_enabled BOOLEAN   NOT NULL DEFAULT TRUE
);
