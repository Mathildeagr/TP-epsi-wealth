# EPSI Wealth — API REST Spring Boot

Application de gestion de finances personnelles : utilisateurs, comptes, catégories et transactions.

## Lancement

```bash
docker-compose up --build
```

L'API est disponible sur `http://localhost:8080`.

La documentation Swagger est disponible sur `http://localhost:8080/swagger-ui/index.html`

---

## Utilisateurs — `/api/users`

### GET `/api/users`
Liste tous les utilisateurs.

### GET `/api/users/{id}`
Récupère un utilisateur par son ID.

### POST `/api/users`
Crée un nouvel utilisateur.
```json
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com"
}
```

### DELETE `/api/users/{id}`
Supprime un utilisateur et toutes ses données associées.
```
DELETE http://localhost:8080/api/users/1
```

### GET `/api/users/{id}/transactions?mois=6&annee=2026`
Transactions d'un utilisateur, filtrables par mois/année.
```
GET http://localhost:8080/api/users/1/transactions?mois=6&annee=2026
```

### GET `/api/users/{id}/dashboard`
Solde total, revenus, dépenses du mois en cours et projection annuelle.
```
GET http://localhost:8080/api/users/1/dashboard
```

### GET `/api/users/{id}/safety-buffer`
Calcul du matelas de sécurité (12 mois de dépenses).
```
GET http://localhost:8080/api/users/1/safety-buffer
```

### GET `/api/users/{id}/advisor`
Conseil patrimonial : statut du matelas, surplus et comptes d'épargne.
```
GET http://localhost:8080/api/users/1/advisor
```

---

## Comptes — `/api/accounts`

### GET `/api/accounts`
Liste tous les comptes.

### GET `/api/accounts/{id}`
Récupère un compte par son ID.

### POST `/api/accounts?userId={id}`
Crée un compte pour un utilisateur. `type` : `COURANT` ou `EPARGNE`.
```json
POST http://localhost:8080/api/accounts?userId=1
Content-Type: application/json

{
  "nom": "Compte courant BNP",
  "soldeActuel": 1500.00,
  "tauxInteret": 0.0,
  "type": "COURANT"
}
```

### PUT `/api/accounts/{id}`
Met à jour un compte existant.
```json
PUT http://localhost:8080/api/accounts/1
Content-Type: application/json

{
  "nom": "Livret A",
  "soldeActuel": 5000.00,
  "tauxInteret": 3.0,
  "type": "EPARGNE"
}
```

### GET `/api/accounts/{id}/transactions`
Liste les transactions d'un compte.
```
GET http://localhost:8080/api/accounts/1/transactions
```

---

## Catégories — `/api/categories`

### GET `/api/categories`
Liste toutes les catégories.

### GET `/api/categories/{id}`
Récupère une catégorie par son ID.

### POST `/api/categories?userId={id}`
Crée une catégorie pour un utilisateur.
```json
POST http://localhost:8080/api/categories?userId=1
Content-Type: application/json

{
  "nom": "Alimentation",
  "plafondMensuel": 400.00
}
```

### PUT `/api/categories/{id}`
Met à jour une catégorie.
```json
PUT http://localhost:8080/api/categories/1
Content-Type: application/json

{
  "nom": "Loisirs",
  "plafondMensuel": 200.00
}
```

---

## Transactions — `/api/transactions`

### GET `/api/transactions`
Liste toutes les transactions.

### GET `/api/transactions/{id}`
Récupère une transaction par son ID.

### POST `/api/transactions?accountId={id}&categoryId={id}`
Crée une transaction. `type` : `REVENU` ou `DEPENSE`.  
Retourne un `warning` si le plafond mensuel de la catégorie est dépassé.
```json
POST http://localhost:8080/api/transactions?accountId=1&categoryId=1
Content-Type: application/json

{
  "libelle": "Courses Carrefour",
  "montant": 85.50,
  "transactionDate": "2026-06-08",
  "type": "DEPENSE"
}
```

### DELETE `/api/transactions/{id}`
Supprime une transaction.
```
DELETE http://localhost:8080/api/transactions/1
```

---

## Modèles de données

| Entité      | Champs principaux                                                              |
|-------------|--------------------------------------------------------------------------------|
| User        | `nom`, `prenom`, `email`, `dateInscription`                                   |
| Account     | `nom`, `soldeActuel`, `tauxInteret`, `type` (COURANT / EPARGNE)               |
| Category    | `nom`, `plafondMensuel`                                                        |
| Transaction | `libelle`, `montant`, `transactionDate`, `type` (REVENU / DEPENSE)            |
