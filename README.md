# Rent a Friend

Rent a Friend is a full-stack emotional support platform where users can register, describe the kind of support they need, browse caring listener profiles, and request one-to-one support sessions with stronger safety and verification controls.

## Stack

- Backend: Java 17, Spring Boot, Spring Web, Spring Data JPA, PostgreSQL, Swagger/OpenAPI
- Frontend: React 18, Vite

## Features

- User registration with legal name, phone, emergency contact, ID hints, age gate, and location-aware onboarding
- Current-location capture from the browser with explicit storage consent
- Persistent PostgreSQL storage for user profile, verification, consent, and booking data
- Strong terms and conditions that explicitly forbid sexual services, nudity, escorting, and hidden physical commitments
- Listener discovery with category, city, and search filters
- Personalized dashboard showing verification status and stored compliance details
- Session booking with chat, audio, video, or in-person options
- Booking safeguards that block sexual or unsafe physical requests
- Seeded support categories and listener profiles

## Project structure

- `backend/` Spring Boot REST API
- `frontend/` React application

## Local setup

The local machine has now been prepared with:

- Java 17
- Node.js + npm
- PostgreSQL running on `localhost:5432`
- A repo-local Maven runtime in `.tools/apache-maven-3.9.16`

The backend local profile is configured in `backend/src/main/resources/application-local.yml` with:

- Database: `rent_a_friend`
- Username: `postgres`
- Password: `postgres`
- Profile: `local`

### Backend

```bash
./run-backend-local.ps1
```

The API starts at `http://localhost:8011`.

Swagger UI is available at `http://localhost:8011/swagger-ui/index.html`.

OpenAPI JSON is available at `http://localhost:8011/v3/api-docs`.

### Frontend

```bash
./run-frontend.ps1
```

The frontend starts at `http://localhost:5173`.

## Main API routes

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/meta/categories`
- `GET /api/meta/legal-policy`
- `GET /api/listeners`
- `GET /api/listeners/{id}`
- `GET /api/dashboard`
- `POST /api/bookings`
- `GET /api/bookings/me`
- `PATCH /api/bookings/{bookingId}/status`

## Persistence and safety notes

- User data is stored in PostgreSQL through `backend/src/main/resources/application-local.yml`.
- Registration now stores profile details, location consent, optional current coordinates, ID metadata, emergency contact details, and terms acceptance timestamps.
- New accounts are marked as `SUBMITTED` for verification review by default.
- In-person sessions are blocked until a user reaches `VERIFIED` status.
- Booking text is screened against prohibited sexual and unsafe-request terms before persistence.

## Validation completed locally

- Backend compiled successfully with Maven.
- Frontend dependencies were installed and the production build succeeded.
- Backend started successfully with the `local` profile.
- Swagger UI returned `200` from `http://localhost:8080/swagger-ui/index.html`.
- Frontend dev server returned `200` from `http://127.0.0.1:5173`.

## Production follow-up

- Replace demo token auth with Spring Security and stronger password/session handling.
- Move from H2 to PostgreSQL or MySQL for production deployments.
- Add real document verification, OTP-based phone/email verification, admin review tools, and audit logging before going live.
