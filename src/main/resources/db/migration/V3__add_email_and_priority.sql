-- V3__add_email_to_users_and_priority_to_tasks.sql
-- Ajout du champ email dans users et priority dans tasks

ALTER TABLE users
    ADD COLUMN email VARCHAR(100) NOT NULL DEFAULT '' AFTER username,
    ADD CONSTRAINT uq_users_email UNIQUE (email);

ALTER TABLE tasks
    ADD COLUMN priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' AFTER status;
