SET MODE MySQL;
INSERT INTO USERS (id, username, password)
VALUES (1, 'user1', '{noop}user_1'),
       (2, 'user2', '{noop}user_2') ON DUPLICATE KEY
UPDATE username = username, password = password;