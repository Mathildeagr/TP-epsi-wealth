# EPSI Wealth — API REST Spring Boot

Application de gestion de finances personnelles : utilisateurs, comptes, catégories et transactions.

## Lancement

```bash
docker-compose up --build
```

L'API est disponible sur `http://localhost:8080`.

La documentation Swagger est disponible sur `http://localhost:8080/swagger-ui/index.html`

---

## Authentification — `/api/auth`

Ces endpoints sont publics (aucun token requis).

### POST `/api/auth/register`
Inscription : crée un compte utilisateur avec le mot de passe hashé (BCrypt).
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "password": "monMotDePasse"
}
```

### POST `/api/auth/login`
Connexion : retourne un token JWT valable 24h.
```json
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "jean.dupont@example.com",
  "password": "monMotDePasse"
}
```
Réponse :
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "jean.dupont@example.com",
  "expires": "2026-06-09T10:00:00"
}
```

---

## Sécurité

Tous les endpoints ci-dessous (hors `/api/auth/**` et Swagger) requièrent un token JWT dans le header :
```
Authorization: Bearer <token>
```
- Sans token → `401 Unauthorized`
- Avec un token valide mais accès aux données d'un autre utilisateur → `403 Forbidden`

---

## Utilisateurs — `/api/users`

### GET `/api/users`
Liste tous les utilisateurs.
```
GET http://localhost:8080/api/users
Authorization: Bearer <token>
```

### GET `/api/users/{id}`
Récupère un utilisateur par son ID (propriétaire uniquement).
```
GET http://localhost:8080/api/users/1
Authorization: Bearer <token>
```

### DELETE `/api/users/{id}`
Supprime un utilisateur et toutes ses données associées (propriétaire uniquement).
```
DELETE http://localhost:8080/api/users/1
Authorization: Bearer <token>
```

### GET `/api/users/{id}/transactions?mois=6&annee=2026`
Transactions d'un utilisateur, filtrables par mois/année (propriétaire uniquement).
```
GET http://localhost:8080/api/users/1/transactions?mois=6&annee=2026
Authorization: Bearer <token>
```

### GET `/api/users/{id}/dashboard`
Solde total, revenus, dépenses du mois en cours et projection annuelle (propriétaire uniquement).
```
GET http://localhost:8080/api/users/1/dashboard
Authorization: Bearer <token>
```

### GET `/api/users/{id}/safety-buffer`
Calcul du matelas de sécurité (12 mois de dépenses) (propriétaire uniquement).
```
GET http://localhost:8080/api/users/1/safety-buffer
Authorization: Bearer <token>
```

### GET `/api/users/{id}/advisor`
Conseil patrimonial : statut du matelas, surplus et comptes d'épargne (propriétaire uniquement).
```
GET http://localhost:8080/api/users/1/advisor
Authorization: Bearer <token>
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
Authorization: Bearer <token>

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
Authorization: Bearer <token>

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
Authorization: Bearer <token>
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
Authorization: Bearer <token>

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
Authorization: Bearer <token>

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
Retourne la transaction et un `warning` si le plafond mensuel de la catégorie est dépassé.
```json
POST http://localhost:8080/api/transactions?accountId=1&categoryId=1
Content-Type: application/json
Authorization: Bearer <token>

{
  "libelle": "Courses Carrefour",
  "montant": 85.50,
  "transactionDate": "2026-06-08",
  "type": "DEPENSE"
}
```
Réponse :
```json
{
  "transaction": { ... },
  "warning": "Plafond dépassé pour la catégorie 'Alimentation'. Budget mensuel : 400.00€, Total après opération : 450.00€."
}
```
> `warning` est `null` si le plafond n'est pas dépassé.

### DELETE `/api/transactions/{id}`
Supprime une transaction.
```
DELETE http://localhost:8080/api/transactions/1
Authorization: Bearer <token>
```

---

## Modèles de données

| Entité      | Champs principaux                                                                        |
|-------------|------------------------------------------------------------------------------------------|
| User        | `nom`, `prenom`, `email`, `password` (jamais exposé en JSON), `dateInscription`         |
| Account     | `nom`, `soldeActuel`, `tauxInteret`, `type` (COURANT / EPARGNE)                         |
| Category    | `nom`, `plafondMensuel`                                                                  |
| Transaction | `libelle`, `montant`, `transactionDate`, `type` (REVENU / DEPENSE)                      |

