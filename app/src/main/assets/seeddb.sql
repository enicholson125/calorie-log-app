CREATE TABLE calorie_log(
    id TEXT NOT NULL PRIMARY KEY,
    time_logged TEXT NOT NULL,
    calories INTEGER NOT NULL,
    description TEXT NOT NULL
);

INSERT INTO calorie_log (id, time_logged, calories, description) VALUES ('2jcid', '2022-02-25 00:00:00', 0, 'Daily Budget');
