CREATE TABLE IF NOT EXISTS config_props (
    id          UUID                        NOT NULL DEFAULT gen_random_uuid(),
    prop_key    VARCHAR(255)                NOT NULL,
    value       TEXT                        NOT NULL,
    CONSTRAINT  config_props_pk             PRIMARY KEY (id),
    CONSTRAINT  config_props_prop_key_uq    UNIQUE (prop_key)
);

CREATE INDEX IF NOT EXISTS idx_config_props_prop_key ON config_props (prop_key);
