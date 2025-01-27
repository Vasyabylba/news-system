INSERT INTO news (id, created_at, last_modified_at, title, text)
VALUES
('b14e5f84-9d14-4477-a2a0-0bc2618f0469', '1991-02-03 04:05:06', '1991-02-03 04:05:06', 'News 1 Title', 'Content of news 1'),
('ef6ca2a3-4535-4dda-9773-156e7e410afb', '1991-02-03 04:05:06', '1991-02-03 04:05:06', 'News 2 Title', 'Content of news 2'),
('fa3a3d92-787f-49e1-85bd-de1e39c46662', '1991-02-03 04:05:06', '1991-02-03 04:05:06', 'News 3 Title', 'Content of news 3');

INSERT INTO comments (id, created_at, last_modified_at, text, username, news_id)
VALUES
('f6eb2a7a-9a3f-49a0-b895-6019407b6a21', '1999-01-08 04:05:06', '1999-01-08 04:05:06', 'Comment number 1', 'AnnaGrace', '035cd8a9-9707-41c6-8810-b1566cc49c7b'),
('7b4c296b-40ff-4e43-841b-826d30c3566b', '1999-01-08 04:05:06', '1999-01-08 04:05:06', 'Comment number 2', 'UserMike88', '035cd8a9-9707-41c6-8810-b1566cc49c7b'),
('9d7a83ae-17a3-4ae6-83bd-977101fe6a2c', '1999-01-08 04:05:06', '1999-01-08 04:05:06', 'Comment number 3', 'UserJohn12', '035cd8a9-9707-41c6-8810-b1566cc49c7b');