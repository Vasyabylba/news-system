-- liquibase formatted sql

-- changeset Vasiliy:create-table-news
CREATE TABLE news (
  id UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE,
   last_modified_at TIMESTAMP WITHOUT TIME ZONE,
   title VARCHAR NOT NULL,
   text TEXT,
   CONSTRAINT pk_news PRIMARY KEY (id)
);
