CREATE TABLE calorie_log(
    id TEXT NOT NULL PRIMARY KEY,
    time_logged TEXT NOT NULL,
    calories INTEGER NOT NULL,
    description TEXT NOT NULL,
    sweet INTEGER NOT NULL DEFAULT 1
);

INSERT INTO calorie_log (id, time_logged, calories, description, sweet) VALUES ('initsweetbudgetid', '2022-05-01 00:00:00', 0, 'Daily Budget', 1);
INSERT INTO calorie_log (id, time_logged, calories, description, sweet) VALUES ('initoverallbudgetid', '2022-05-01 00:00:00', 0, 'Daily Budget Full', 0);
