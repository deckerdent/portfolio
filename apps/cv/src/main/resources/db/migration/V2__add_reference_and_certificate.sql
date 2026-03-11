-- V2: Add reference and certificate tables

-- Reference
CREATE TABLE reference (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    description TEXT        NOT NULL,
    relation    VARCHAR(50),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Certificate (extends timeline entry pattern)
CREATE TABLE certificate (
    id                   UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title                VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255) NOT NULL,
    start_date           DATE        NOT NULL,
    end_date             DATE,
    location             VARCHAR(255),
    description          TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);
