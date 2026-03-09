-- V1__create_users_table.sql
-- Création de la table des utilisateurs avec rôle et timestamps

CREATE TABLE users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id)
);
