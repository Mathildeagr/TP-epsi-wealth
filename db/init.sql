--  ESPI Wealth — Schéma de base de données
--  Ce fichier est exécuté automatiquement au premier démarrage
--  du conteneur MySQL (dossier /docker-entrypoint-initdb.d/).
--  Ce fichier est pris en compte QUE si le volume mysql_data est vide.
--  Pour forcer une réinitialisation :
--        docker compose down -v
--        docker compose up --build

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_inscription DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255),
    solde_actuel DOUBLE NOT NULL,
    taux_interet DOUBLE,
    type ENUM('COURANT', 'EPARGNE') NOT NULL,
    user_id BIGINT NOT NULL,
    
    CONSTRAINT fk_account_user
        FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255),
    plafond_mensuel DOUBLE,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_category_user
        FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255),
    montant DOUBLE NOT NULL CHECK (montant > 0),
    transaction_date DATE NOT NULL,
    type ENUM('REVENU', 'DEPENSE') NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT,

    CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)  REFERENCES account(id),

    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id) REFERENCES category(id)
);

-- =============================================================
-- DATASET DE BASE
-- =============================================================

-- -------------------------------------------------------------
-- Utilisateurs
--   Mot de passe commun (démo) : Password1!
--   1. Marie Dupont   — inscrite Jan 2024  → 16 mois d'historique → MATELAS_OK
--   2. Thomas Martin  — inscrit Oct 2025   →  7 mois d'historique → MATELAS_INCOMPLET
--   3. Sophie Leclerc — inscrite Avr 2026  →  1 mois d'historique → MATELAS_INCOMPLET
-- -------------------------------------------------------------
-- -------------------------------------------------------------
-- Utilisateurs
-- Mot de passe de tous les comptes de démo : Password1!
-- Hash BCrypt (cost 10) de "Password1!" :
--   $2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W
-- -------------------------------------------------------------
INSERT INTO user (id, nom, prenom, email, password, date_inscription) VALUES
(1, 'Dupont',  'Marie',   'marie.dupont@email.com',   '$2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W', '2024-01-15'),
(2, 'Martin',  'Thomas',  'thomas.martin@email.com',  '$2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W', '2025-10-01'),
(3, 'Leclerc', 'Sophie',  'sophie.leclerc@email.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W', '2026-04-01'),
-- taux épargne < 1.5 %
(4, 'Bernard', 'Lucas',   'lucas.bernard@email.com',  '$2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W', '2024-06-01'),
(5, 'Morel',   'Camille', 'camille.morel@email.com',  '$2a$10$7EqJtq98hPqEX7fNZaFWoO9G7RXVMKyvUiyl.ZMUAPe38rBKIAC0W', '2025-03-15');

-- -------------------------------------------------------------
-- Comptes
-- -------------------------------------------------------------
INSERT INTO account (id, nom, solde_actuel, taux_interet, type, user_id) VALUES
-- Marie  → solde total 23 500 €
(1, 'Compte Courant', 8500.00,  NULL, 'COURANT', 1),
(2, 'Livret A',      15000.00,  2.5, 'EPARGNE', 1),
-- Thomas → solde total  7 800 €
(3, 'Compte Courant', 2800.00,  NULL, 'COURANT', 2),
(4, 'Livret A',       5000.00,  3.0, 'EPARGNE', 2),
-- Sophie → solde total  3 200 €
(5, 'Compte Courant', 3200.00,  NULL, 'COURANT', 3),
-- Lucas  → taux épargne 0.8 % (< 1.5 %)  solde total 18 200 €
(6, 'Compte Courant', 4200.00,  NULL, 'COURANT', 4),
(7, 'Livret Bancaire',14000.00, 0.8, 'EPARGNE', 4),
-- Camille → taux épargne 1.2 % (< 1.5 %) solde total 11 500 €
(8, 'Compte Courant', 3500.00,  NULL, 'COURANT', 5),
(9, 'Livret Bancaire', 8000.00, 1.2, 'EPARGNE', 5);

-- -------------------------------------------------------------
-- Catégories
-- -------------------------------------------------------------
INSERT INTO category (id, nom, plafond_mensuel, user_id) VALUES
-- Marie (user 1)
(1,  'Salaire',      NULL,  1),
(2,  'Loyer',        1000,  1),
(3,  'Alimentation',  500,  1),
(4,  'Transport',     200,  1),
(5,  'Loisirs',       300,  1),
(6,  'Santé',         100,  1),
-- Thomas (user 2)
(7,  'Salaire',      NULL,  2),
(8,  'Loyer',         800,  2),
(9,  'Alimentation',  400,  2),
(10, 'Transport',     150,  2),
(11, 'Loisirs',       200,  2),
(12, 'Divers',       NULL,  2),
-- Sophie (user 3)
(13, 'Salaire',      NULL,  3),
(14, 'Loyer',         900,  3),
(15, 'Alimentation',  450,  3),
(16, 'Transport',     150,  3),
(17, 'Loisirs',       200,  3),
-- Lucas (user 4)
(18, 'Salaire',      NULL,  4),
(19, 'Loyer',         850,  4),
(20, 'Alimentation',  450,  4),
(21, 'Transport',     180,  4),
(22, 'Loisirs',       250,  4),
(23, 'Santé',         120,  4),
-- Camille (user 5)
(24, 'Salaire',      NULL,  5),
(25, 'Loyer',         750,  5),
(26, 'Alimentation',  380,  5),
(27, 'Transport',     130,  5),
(28, 'Loisirs',       180,  5);

-- -------------------------------------------------------------
-- Transactions — Marie (account 1, cat 1-6)
-- Dépenses mensuelles ≈ 1 700 €  →  matelas 12 mois ≈ 20 400 €
-- Solde total 23 500 €  →  surplus +3 100 €  →  MATELAS_OK
-- -------------------------------------------------------------
INSERT INTO transaction (libelle, montant, transaction_date, type, account_id, category_id) VALUES
-- Jan 2024 — taux épargne < 0% (-12.5%) → ⚠️ Attention
('Salaire janvier 2024',    3200, '2024-01-20', 'REVENU',  1, 1),
('Loyer janvier 2024',       900, '2024-01-25', 'DEPENSE', 1, 2),
('Réparation voiture',      2700, '2024-01-28', 'DEPENSE', 1, 5),
-- Fév 2024 — taux épargne 0-10% (7.8%) → 📉 Épargne faible
('Salaire février 2024',    3200, '2024-02-01', 'REVENU',  1, 1),
('Loyer février 2024',       900, '2024-02-05', 'DEPENSE', 1, 2),
('Courses février 2024',     450, '2024-02-10', 'DEPENSE', 1, 3),
('Transport février 2024',   200, '2024-02-15', 'DEPENSE', 1, 4),
('Vacances ski',            1400, '2024-02-20', 'DEPENSE', 1, 5),
-- Mar 2024 — taux épargne 10-20% (14.1%) → 📊 Correct
('Salaire mars 2024',       3200, '2024-03-01', 'REVENU',  1, 1),
('Loyer mars 2024',          900, '2024-03-05', 'DEPENSE', 1, 2),
('Courses mars 2024',        500, '2024-03-10', 'DEPENSE', 1, 3),
('Transport mars 2024',      200, '2024-03-15', 'DEPENSE', 1, 4),
('Équipement maison',       1150, '2024-03-20', 'DEPENSE', 1, 5),
-- Avr 2025
('Salaire avril',         3200, '2025-04-01', 'REVENU',  1, 1),
('Loyer avril',            900, '2025-04-05', 'DEPENSE', 1, 2),
('Courses semaine 1-2',    380, '2025-04-12', 'DEPENSE', 1, 3),
('Abonnement transport',   145, '2025-04-15', 'DEPENSE', 1, 4),
('Sortie cinéma / resto',  210, '2025-04-20', 'DEPENSE', 1, 5),
-- Mai 2025
('Salaire mai',           3200, '2025-05-01', 'REVENU',  1, 1),
('Loyer mai',              900, '2025-05-05', 'DEPENSE', 1, 2),
('Courses mai',            410, '2025-05-10', 'DEPENSE', 1, 3),
('Transport mai',          150, '2025-05-15', 'DEPENSE', 1, 4),
('Concert',                180, '2025-05-20', 'DEPENSE', 1, 5),
('Pharmacie',               65, '2025-05-25', 'DEPENSE', 1, 6),
-- Juin 2025
('Salaire juin',          3200, '2025-06-01', 'REVENU',  1, 1),
('Loyer juin',             900, '2025-06-05', 'DEPENSE', 1, 2),
('Courses juin',           395, '2025-06-10', 'DEPENSE', 1, 3),
('Transport juin',         150, '2025-06-15', 'DEPENSE', 1, 4),
('Festival',               270, '2025-06-22', 'DEPENSE', 1, 5),
-- Juil 2025
('Salaire juillet',       3200, '2025-07-01', 'REVENU',  1, 1),
('Loyer juillet',          900, '2025-07-05', 'DEPENSE', 1, 2),
('Courses juillet',        420, '2025-07-10', 'DEPENSE', 1, 3),
('Transport juillet',      150, '2025-07-15', 'DEPENSE', 1, 4),
('Vacances activités',     280, '2025-07-20', 'DEPENSE', 1, 5),
('Médecin généraliste',     55, '2025-07-25', 'DEPENSE', 1, 6),
-- Août 2025
('Salaire août',          3200, '2025-08-01', 'REVENU',  1, 1),
('Loyer août',             900, '2025-08-05', 'DEPENSE', 1, 2),
('Courses août',           370, '2025-08-10', 'DEPENSE', 1, 3),
('Transport août',         130, '2025-08-15', 'DEPENSE', 1, 4),
('Activités été',          250, '2025-08-20', 'DEPENSE', 1, 5),
-- Sept 2025
('Salaire septembre',     3200, '2025-09-01', 'REVENU',  1, 1),
('Loyer septembre',        900, '2025-09-05', 'DEPENSE', 1, 2),
('Courses septembre',      405, '2025-09-10', 'DEPENSE', 1, 3),
('Transport septembre',    150, '2025-09-15', 'DEPENSE', 1, 4),
('Loisirs septembre',      190, '2025-09-20', 'DEPENSE', 1, 5),
('Dentiste',                80, '2025-09-28', 'DEPENSE', 1, 6),
-- Oct 2025
('Salaire octobre',       3200, '2025-10-01', 'REVENU',  1, 1),
('Loyer octobre',          900, '2025-10-05', 'DEPENSE', 1, 2),
('Courses octobre',        430, '2025-10-10', 'DEPENSE', 1, 3),
('Transport octobre',      150, '2025-10-15', 'DEPENSE', 1, 4),
('Week-end city-trip',     220, '2025-10-18', 'DEPENSE', 1, 5),
-- Nov 2025
('Salaire novembre',      3200, '2025-11-01', 'REVENU',  1, 1),
('Loyer novembre',         900, '2025-11-05', 'DEPENSE', 1, 2),
('Courses novembre',       390, '2025-11-10', 'DEPENSE', 1, 3),
('Transport novembre',     150, '2025-11-15', 'DEPENSE', 1, 4),
('Cinéma / resto',         170, '2025-11-20', 'DEPENSE', 1, 5),
('Pharmacie',               45, '2025-11-25', 'DEPENSE', 1, 6),
-- Déc 2025
('Salaire décembre',      3200, '2025-12-01', 'REVENU',  1, 1),
('Prime fin d''année',     800, '2025-12-05', 'REVENU',  1, 1),
('Loyer décembre',         900, '2025-12-05', 'DEPENSE', 1, 2),
('Courses décembre',       480, '2025-12-10', 'DEPENSE', 1, 3),
('Transport décembre',     150, '2025-12-15', 'DEPENSE', 1, 4),
('Cadeaux / fêtes',        300, '2025-12-20', 'DEPENSE', 1, 5),
-- Jan 2026
('Salaire janvier',       3200, '2026-01-01', 'REVENU',  1, 1),
('Loyer janvier',          900, '2026-01-05', 'DEPENSE', 1, 2),
('Courses janvier',        360, '2026-01-10', 'DEPENSE', 1, 3),
('Transport janvier',      150, '2026-01-15', 'DEPENSE', 1, 4),
('Loisirs janvier',        140, '2026-01-22', 'DEPENSE', 1, 5),
-- Fév 2026
('Salaire février',       3200, '2026-02-01', 'REVENU',  1, 1),
('Loyer février',          900, '2026-02-05', 'DEPENSE', 1, 2),
('Courses février',        385, '2026-02-10', 'DEPENSE', 1, 3),
('Transport février',      150, '2026-02-15', 'DEPENSE', 1, 4),
('Saint-Valentin',         200, '2026-02-14', 'DEPENSE', 1, 5),
('Médecin',                 55, '2026-02-22', 'DEPENSE', 1, 6),
-- Mars 2026
('Salaire mars',          3200, '2026-03-01', 'REVENU',  1, 1),
('Loyer mars',             900, '2026-03-05', 'DEPENSE', 1, 2),
('Courses mars',           400, '2026-03-10', 'DEPENSE', 1, 3),
('Transport mars',         150, '2026-03-15', 'DEPENSE', 1, 4),
('Loisirs mars',           185, '2026-03-20', 'DEPENSE', 1, 5),
-- Avr 2026
('Salaire avril',         3200, '2026-04-01', 'REVENU',  1, 1),
('Loyer avril',            900, '2026-04-05', 'DEPENSE', 1, 2),
('Courses avril',          415, '2026-04-10', 'DEPENSE', 1, 3),
('Transport avril',        150, '2026-04-15', 'DEPENSE', 1, 4),
('Sorties avril',          230, '2026-04-20', 'DEPENSE', 1, 5),
('Pharmacie',               35, '2026-04-25', 'DEPENSE', 1, 6),
-- Mai 2026
('Salaire mai',           3200, '2026-05-01', 'REVENU',  1, 1),
('Loyer mai',              900, '2026-05-05', 'DEPENSE', 1, 2),
('Courses mai',            390, '2026-05-10', 'DEPENSE', 1, 3),
('Transport mai',          150, '2026-05-15', 'DEPENSE', 1, 4),
('Loisirs mai',            210, '2026-05-20', 'DEPENSE', 1, 5);

-- -------------------------------------------------------------
-- Transactions — Thomas (account 3, cat 7-12)
-- Dépenses mensuelles ≈ 1 380 €  →  matelas projeté ≈ 16 500 €
-- Solde total 7 800 €  →  MATELAS_INCOMPLET
-- -------------------------------------------------------------
INSERT INTO transaction (libelle, montant, transaction_date, type, account_id, category_id) VALUES
-- Oct 2025
('Salaire octobre',       2500, '2025-10-01', 'REVENU',  3, 7),
('Loyer octobre',          700, '2025-10-05', 'DEPENSE', 3, 8),
('Courses octobre',        340, '2025-10-10', 'DEPENSE', 3, 9),
('Navigo octobre',         120, '2025-10-15', 'DEPENSE', 3, 10),
('Sorties',                140, '2025-10-20', 'DEPENSE', 3, 11),
('Achat matériel',         250, '2025-10-28', 'DEPENSE', 3, 12),
-- Nov 2025
('Salaire novembre',      2500, '2025-11-01', 'REVENU',  3, 7),
('Loyer novembre',         700, '2025-11-05', 'DEPENSE', 3, 8),
('Courses novembre',       350, '2025-11-10', 'DEPENSE', 3, 9),
('Navigo novembre',        120, '2025-11-15', 'DEPENSE', 3, 10),
('Loisirs novembre',       130, '2025-11-20', 'DEPENSE', 3, 11),
-- Déc 2025
('Salaire décembre',      2500, '2025-12-01', 'REVENU',  3, 7),
('Loyer décembre',         700, '2025-12-05', 'DEPENSE', 3, 8),
('Courses décembre',       410, '2025-12-10', 'DEPENSE', 3, 9),
('Navigo décembre',        120, '2025-12-15', 'DEPENSE', 3, 10),
('Cadeaux',                200, '2025-12-20', 'DEPENSE', 3, 11),
('Réparation téléphone',   180, '2025-12-27', 'DEPENSE', 3, 12),
-- Jan 2026
('Salaire janvier',       2500, '2026-01-01', 'REVENU',  3, 7),
('Loyer janvier',          700, '2026-01-05', 'DEPENSE', 3, 8),
('Courses janvier',        310, '2026-01-10', 'DEPENSE', 3, 9),
('Navigo janvier',         120, '2026-01-15', 'DEPENSE', 3, 10),
('Sorties',                110, '2026-01-22', 'DEPENSE', 3, 11),
-- Fév 2026
('Salaire février',       2500, '2026-02-01', 'REVENU',  3, 7),
('Loyer février',          700, '2026-02-05', 'DEPENSE', 3, 8),
('Courses février',        330, '2026-02-10', 'DEPENSE', 3, 9),
('Navigo février',         120, '2026-02-15', 'DEPENSE', 3, 10),
('Loisirs',                150, '2026-02-20', 'DEPENSE', 3, 11),
-- Mars 2026
('Salaire mars',          2500, '2026-03-01', 'REVENU',  3, 7),
('Loyer mars',             700, '2026-03-05', 'DEPENSE', 3, 8),
('Courses mars',           355, '2026-03-10', 'DEPENSE', 3, 9),
('Navigo mars',            120, '2026-03-15', 'DEPENSE', 3, 10),
('Sorties mars',           140, '2026-03-22', 'DEPENSE', 3, 11),
('Divers mars',            190, '2026-03-28', 'DEPENSE', 3, 12),
-- Avr 2026
('Salaire avril',         2500, '2026-04-01', 'REVENU',  3, 7),
('Loyer avril',            700, '2026-04-05', 'DEPENSE', 3, 8),
('Courses avril',          365, '2026-04-10', 'DEPENSE', 3, 9),
('Navigo avril',           120, '2026-04-15', 'DEPENSE', 3, 10),
('Loisirs avril',          155, '2026-04-20', 'DEPENSE', 3, 11),
-- Mai 2026
('Salaire mai',           2500, '2026-05-01', 'REVENU',  3, 7),
('Loyer mai',              700, '2026-05-05', 'DEPENSE', 3, 8),
('Courses mai',            345, '2026-05-10', 'DEPENSE', 3, 9),
('Navigo mai',             120, '2026-05-15', 'DEPENSE', 3, 10),
('Sorties mai',            130, '2026-05-20', 'DEPENSE', 3, 11);

-- -------------------------------------------------------------
-- Transactions — Sophie (account 5, cat 13-17)
-- Dépenses ≈ 1 350 €/mois  →  matelas projeté ≈ 16 200 €
-- Solde total 3 200 €  →  MATELAS_INCOMPLET
-- -------------------------------------------------------------
INSERT INTO transaction (libelle, montant, transaction_date, type, account_id, category_id) VALUES
-- Avr 2026
('Salaire avril',         2800, '2026-04-01', 'REVENU',  5, 13),
('Loyer avril',            800, '2026-04-05', 'DEPENSE', 5, 14),
('Courses avril',          300, '2026-04-10', 'DEPENSE', 5, 15),
('Transport avril',        100, '2026-04-15', 'DEPENSE', 5, 16),
('Loisirs avril',          150, '2026-04-20', 'DEPENSE', 5, 17),
-- Mai 2026
('Salaire mai',           2800, '2026-05-01', 'REVENU',  5, 13),
('Loyer mai',              800, '2026-05-05', 'DEPENSE', 5, 14),
('Courses mai',            310, '2026-05-10', 'DEPENSE', 5, 15),
('Transport mai',          100, '2026-05-15', 'DEPENSE', 5, 16),
('Loisirs mai',            140, '2026-05-20', 'DEPENSE', 5, 17);

-- -------------------------------------------------------------
-- Transactions — Lucas (accounts 6/7, cat 18-23)
-- Taux épargne 0.8 %  |  Dépenses ≈ 1 680 €/mois
-- Matelas 12 mois ≈ 20 160 €  |  Solde 18 200 €  →  MATELAS_INCOMPLET
-- -------------------------------------------------------------
INSERT INTO transaction (libelle, montant, transaction_date, type, account_id, category_id) VALUES
-- Juil 2025
('Salaire juillet',       3100, '2025-07-01', 'REVENU',  6, 18),
('Loyer juillet',          850, '2025-07-05', 'DEPENSE', 6, 19),
('Courses juillet',        430, '2025-07-10', 'DEPENSE', 6, 20),
('Transport juillet',      170, '2025-07-15', 'DEPENSE', 6, 21),
('Loisirs juillet',        240, '2025-07-20', 'DEPENSE', 6, 22),
-- Août 2025
('Salaire août',          3100, '2025-08-01', 'REVENU',  6, 18),
('Loyer août',             850, '2025-08-05', 'DEPENSE', 6, 19),
('Courses août',           415, '2025-08-10', 'DEPENSE', 6, 20),
('Transport août',         155, '2025-08-15', 'DEPENSE', 6, 21),
('Loisirs été',            280, '2025-08-20', 'DEPENSE', 6, 22),
('Médecin',                 70, '2025-08-25', 'DEPENSE', 6, 23),
-- Sept 2025
('Salaire septembre',     3100, '2025-09-01', 'REVENU',  6, 18),
('Loyer septembre',        850, '2025-09-05', 'DEPENSE', 6, 19),
('Courses septembre',      445, '2025-09-10', 'DEPENSE', 6, 20),
('Transport septembre',    175, '2025-09-15', 'DEPENSE', 6, 21),
('Loisirs septembre',      225, '2025-09-20', 'DEPENSE', 6, 22),
-- Oct 2025
('Salaire octobre',       3100, '2025-10-01', 'REVENU',  6, 18),
('Loyer octobre',          850, '2025-10-05', 'DEPENSE', 6, 19),
('Courses octobre',        460, '2025-10-10', 'DEPENSE', 6, 20),
('Transport octobre',      180, '2025-10-15', 'DEPENSE', 6, 21),
('Loisirs octobre',        200, '2025-10-20', 'DEPENSE', 6, 22),
('Pharmacie',               55, '2025-10-28', 'DEPENSE', 6, 23),
-- Nov 2025
('Salaire novembre',      3100, '2025-11-01', 'REVENU',  6, 18),
('Loyer novembre',         850, '2025-11-05', 'DEPENSE', 6, 19),
('Courses novembre',       420, '2025-11-10', 'DEPENSE', 6, 20),
('Transport novembre',     170, '2025-11-15', 'DEPENSE', 6, 21),
('Loisirs novembre',       190, '2025-11-20', 'DEPENSE', 6, 22),
-- Déc 2025
('Salaire décembre',      3100, '2025-12-01', 'REVENU',  6, 18),
('Loyer décembre',         850, '2025-12-05', 'DEPENSE', 6, 19),
('Courses décembre',       500, '2025-12-10', 'DEPENSE', 6, 20),
('Transport décembre',     175, '2025-12-15', 'DEPENSE', 6, 21),
('Cadeaux / fêtes',        260, '2025-12-20', 'DEPENSE', 6, 22),
-- Jan 2026
('Salaire janvier',       3100, '2026-01-01', 'REVENU',  6, 18),
('Loyer janvier',          850, '2026-01-05', 'DEPENSE', 6, 19),
('Courses janvier',        390, '2026-01-10', 'DEPENSE', 6, 20),
('Transport janvier',      170, '2026-01-15', 'DEPENSE', 6, 21),
('Loisirs janvier',        165, '2026-01-22', 'DEPENSE', 6, 22),
-- Fév 2026
('Salaire février',       3100, '2026-02-01', 'REVENU',  6, 18),
('Loyer février',          850, '2026-02-05', 'DEPENSE', 6, 19),
('Courses février',        405, '2026-02-10', 'DEPENSE', 6, 20),
('Transport février',      170, '2026-02-15', 'DEPENSE', 6, 21),
('Loisirs février',        210, '2026-02-20', 'DEPENSE', 6, 22),
('Médecin',                 80, '2026-02-25', 'DEPENSE', 6, 23),
-- Mars 2026
('Salaire mars',          3100, '2026-03-01', 'REVENU',  6, 18),
('Loyer mars',             850, '2026-03-05', 'DEPENSE', 6, 19),
('Courses mars',           430, '2026-03-10', 'DEPENSE', 6, 20),
('Transport mars',         175, '2026-03-15', 'DEPENSE', 6, 21),
('Loisirs mars',           195, '2026-03-20', 'DEPENSE', 6, 22),
-- Avr 2026
('Salaire avril',         3100, '2026-04-01', 'REVENU',  6, 18),
('Loyer avril',            850, '2026-04-05', 'DEPENSE', 6, 19),
('Courses avril',          445, '2026-04-10', 'DEPENSE', 6, 20),
('Transport avril',        175, '2026-04-15', 'DEPENSE', 6, 21),
('Sorties avril',          230, '2026-04-20', 'DEPENSE', 6, 22),
('Pharmacie',               60, '2026-04-28', 'DEPENSE', 6, 23),
-- Mai 2026
('Salaire mai',           3100, '2026-05-01', 'REVENU',  6, 18),
('Loyer mai',              850, '2026-05-05', 'DEPENSE', 6, 19),
('Courses mai',            415, '2026-05-10', 'DEPENSE', 6, 20),
('Transport mai',          170, '2026-05-15', 'DEPENSE', 6, 21),
('Loisirs mai',            200, '2026-05-20', 'DEPENSE', 6, 22);

-- -------------------------------------------------------------
-- Transactions — Camille (accounts 8/9, cat 24-28)
-- Taux épargne 1.2 %  |  Dépenses ≈ 1 440 €/mois
-- Matelas 12 mois projeté (14 mois) ≈ 17 280 €  |  Solde 11 500 €  →  MATELAS_INCOMPLET
-- -------------------------------------------------------------
INSERT INTO transaction (libelle, montant, transaction_date, type, account_id, category_id) VALUES
-- Avr 2025
('Salaire avril',         2700, '2025-04-01', 'REVENU',  8, 24),
('Loyer avril',            750, '2025-04-05', 'DEPENSE', 8, 25),
('Courses avril',          370, '2025-04-10', 'DEPENSE', 8, 26),
('Transport avril',        125, '2025-04-15', 'DEPENSE', 8, 27),
('Loisirs avril',          170, '2025-04-20', 'DEPENSE', 8, 28),
-- Mai 2025
('Salaire mai',           2700, '2025-05-01', 'REVENU',  8, 24),
('Loyer mai',              750, '2025-05-05', 'DEPENSE', 8, 25),
('Courses mai',            385, '2025-05-10', 'DEPENSE', 8, 26),
('Transport mai',          130, '2025-05-15', 'DEPENSE', 8, 27),
('Loisirs mai',            155, '2025-05-20', 'DEPENSE', 8, 28),
-- Juin 2025
('Salaire juin',          2700, '2025-06-01', 'REVENU',  8, 24),
('Loyer juin',             750, '2025-06-05', 'DEPENSE', 8, 25),
('Courses juin',           360, '2025-06-10', 'DEPENSE', 8, 26),
('Transport juin',         130, '2025-06-15', 'DEPENSE', 8, 27),
('Loisirs juin',           190, '2025-06-20', 'DEPENSE', 8, 28),
-- Juil 2025
('Salaire juillet',       2700, '2025-07-01', 'REVENU',  8, 24),
('Loyer juillet',          750, '2025-07-05', 'DEPENSE', 8, 25),
('Courses juillet',        400, '2025-07-10', 'DEPENSE', 8, 26),
('Transport juillet',      125, '2025-07-15', 'DEPENSE', 8, 27),
('Loisirs juillet',        210, '2025-07-20', 'DEPENSE', 8, 28),
-- Août 2025
('Salaire août',          2700, '2025-08-01', 'REVENU',  8, 24),
('Loyer août',             750, '2025-08-05', 'DEPENSE', 8, 25),
('Courses août',           355, '2025-08-10', 'DEPENSE', 8, 26),
('Transport août',         120, '2025-08-15', 'DEPENSE', 8, 27),
('Loisirs été',            200, '2025-08-20', 'DEPENSE', 8, 28),
-- Sept 2025
('Salaire septembre',     2700, '2025-09-01', 'REVENU',  8, 24),
('Loyer septembre',        750, '2025-09-05', 'DEPENSE', 8, 25),
('Courses septembre',      375, '2025-09-10', 'DEPENSE', 8, 26),
('Transport septembre',    130, '2025-09-15', 'DEPENSE', 8, 27),
('Loisirs septembre',      165, '2025-09-20', 'DEPENSE', 8, 28),
-- Oct 2025
('Salaire octobre',       2700, '2025-10-01', 'REVENU',  8, 24),
('Loyer octobre',          750, '2025-10-05', 'DEPENSE', 8, 25),
('Courses octobre',        390, '2025-10-10', 'DEPENSE', 8, 26),
('Transport octobre',      130, '2025-10-15', 'DEPENSE', 8, 27),
('Loisirs octobre',        175, '2025-10-20', 'DEPENSE', 8, 28),
-- Nov 2025
('Salaire novembre',      2700, '2025-11-01', 'REVENU',  8, 24),
('Loyer novembre',         750, '2025-11-05', 'DEPENSE', 8, 25),
('Courses novembre',       365, '2025-11-10', 'DEPENSE', 8, 26),
('Transport novembre',     125, '2025-11-15', 'DEPENSE', 8, 27),
('Loisirs novembre',       160, '2025-11-20', 'DEPENSE', 8, 28),
-- Déc 2025
('Salaire décembre',      2700, '2025-12-01', 'REVENU',  8, 24),
('Loyer décembre',         750, '2025-12-05', 'DEPENSE', 8, 25),
('Courses décembre',       430, '2025-12-10', 'DEPENSE', 8, 26),
('Transport décembre',     130, '2025-12-15', 'DEPENSE', 8, 27),
('Cadeaux',                200, '2025-12-20', 'DEPENSE', 8, 28),
-- Jan 2026
('Salaire janvier',       2700, '2026-01-01', 'REVENU',  8, 24),
('Loyer janvier',          750, '2026-01-05', 'DEPENSE', 8, 25),
('Courses janvier',        345, '2026-01-10', 'DEPENSE', 8, 26),
('Transport janvier',      125, '2026-01-15', 'DEPENSE', 8, 27),
('Loisirs janvier',        140, '2026-01-22', 'DEPENSE', 8, 28),
-- Fév 2026
('Salaire février',       2700, '2026-02-01', 'REVENU',  8, 24),
('Loyer février',          750, '2026-02-05', 'DEPENSE', 8, 25),
('Courses février',        360, '2026-02-10', 'DEPENSE', 8, 26),
('Transport février',      130, '2026-02-15', 'DEPENSE', 8, 27),
('Loisirs février',        155, '2026-02-20', 'DEPENSE', 8, 28),
-- Mars 2026
('Salaire mars',          2700, '2026-03-01', 'REVENU',  8, 24),
('Loyer mars',             750, '2026-03-05', 'DEPENSE', 8, 25),
('Courses mars',           375, '2026-03-10', 'DEPENSE', 8, 26),
('Transport mars',         130, '2026-03-15', 'DEPENSE', 8, 27),
('Loisirs mars',           170, '2026-03-20', 'DEPENSE', 8, 28),
-- Avr 2026
('Salaire avril',         2700, '2026-04-01', 'REVENU',  8, 24),
('Loyer avril',            750, '2026-04-05', 'DEPENSE', 8, 25),
('Courses avril',          390, '2026-04-10', 'DEPENSE', 8, 26),
('Transport avril',        130, '2026-04-15', 'DEPENSE', 8, 27),
('Loisirs avril',          180, '2026-04-20', 'DEPENSE', 8, 28),
-- Mai 2026
('Salaire mai',           2700, '2026-05-01', 'REVENU',  8, 24),
('Loyer mai',              750, '2026-05-05', 'DEPENSE', 8, 25),
('Courses mai',            360, '2026-05-10', 'DEPENSE', 8, 26),
('Transport mai',          125, '2026-05-15', 'DEPENSE', 8, 27),
('Loisirs mai',            165, '2026-05-20', 'DEPENSE', 8, 28);
