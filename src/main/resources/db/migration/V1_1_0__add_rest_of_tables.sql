CREATE TABLE IF NOT EXISTS events
(
    id                INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title             VARCHAR(255),
    description       TEXT,
    location          VARCHAR(255),
    price             INT,
    tickets_available INT,
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    image_url         VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tickets
(
    id                 INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_id           INT NOT NULL,
    user_id            INT NOT NULL,
    reservation_number VARCHAR(255),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);