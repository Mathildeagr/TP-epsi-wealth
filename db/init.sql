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
    dateInscription DATE NOT NULL DEFAULT (CURRENT_DATE)
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
    plafondMensuel DOUBLE,
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





