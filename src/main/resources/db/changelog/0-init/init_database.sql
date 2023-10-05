CREATE SCHEMA IF NOT EXISTS predictions;

CREATE TABLE IF NOT EXISTS predictions.questions (
    id SERIAL NOT NULL PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    author_id BIGINT NOT NULL,
    result BOOLEAN
);

CREATE TABLE IF NOT EXISTS predictions.predictions (
    id SERIAL NOT NULL PRIMARY KEY,
    question_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY (question_id) REFERENCES predictions.questions(id)
);