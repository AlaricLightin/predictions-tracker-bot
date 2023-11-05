INSERT INTO predictions.questions (id, text, deadline, author_id, created_at, result)
VALUES 
    (10, 'Question 1', '2022-01-01 00:00:00', 123, '2021-01-01T00:00:00', true),
    (20, 'Question 2', '2022-01-02 00:00:00', 123, '2021-01-01T00:00:00', false),
    (30, 'Question 3', '2022-01-03 00:00:00', 225, '2021-01-01T00:00:00', NULL);

INSERT INTO predictions.predictions (id, question_id, user_id, created_at, probability)
VALUES
    (1, 10, 123, '2021-01-01T00:00:00', 70),
    (2, 20, 123, '2021-01-01T00:00:00', 30),
    (3, 30, 225, '2021-01-01T00:00:00', 50);