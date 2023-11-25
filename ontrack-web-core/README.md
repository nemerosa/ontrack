Ontrack Web Core (Next UI)
==========================

## Configuration

### Environment variables

| Variable                   | Default value           | Description                                                                                                                                             |
|----------------------------|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| NEXT_PUBLIC_LOCAL          | `false`                 | By passing the default authentication process, to directly using `admin` / `admin` as credentials when connecting to the backend. Used for development. |
| NEXT_PUBLIC_ONTRACK_URL    | `http://localhost:8080` | URL of the backend (from a user point of view)                                                                                                          |
| NEXT_PUBLIC_ONTRACK_UI_URL | `http://localhost:3000` | URL of the frontend (from a user point of view)                                                                                                         |


## Architecture Decisions Records

See [ADR](ADR.md).

## Local development

Run the main application, this starts the API on http://localhost:8080
and the legacy UI remains available.

> The middleware must first be made available by running
> `./gradlew devStart`

Run the UI locally:

```bash
cd ontrack-web-core
npm run dev
```

This script runs NextJS on http://localhost:3000.

> Behind the scene, the UI connects to the http://localhost:8080 API.
> `admin/admin` credentials are always used.
>
> This behaviour is driven by the values in the `.env.development` file.
