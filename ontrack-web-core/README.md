Ontrack Web Core (Next UI)
==========================

## Configuration

### Environment variables

| Variable                       | Default value                                      | Description                                                                       |
|--------------------------------|----------------------------------------------------|-----------------------------------------------------------------------------------|
| ONTRACK_URL                    | `http://localhost:8080`                            | URL of the backend                                                                |
| YONTRACK_UI_MANAGE_ACCOUNT_URL | _None_                                             | URL used to redirect the user to the management of their profile in the IdP       |
| YONTRACK_UI_AUTH_SIGNIN_CUSTOM | `false`                                            | If set, activates a custom signin page                                            |
| NEXTAUTH_PROVIDER              | _None_                                             | If `oidc`, activates direct OIDC authentication. Otherwise, uses the Keycloak IdP |
| NEXTAUTH_PROVIDER_NAME         | `OIDC` if using OIDC, `Yontrack` if using Keycloak | Display name of the IdP to show on the signin page                                |
| NEXTAUTH_ISSUER                | _Required_                                         | OIDC issuer URL                                                                   |
| NEXTAUTH_CLIENT_ID             | _Required_                                         | OIDC client ID                                                                    |
| NEXTAUTH_CLIENT_SECRET         | _Required_                                         | OIDC client secret                                                                |
| NEXTAUTH_AUDIENCE              | _None_                                             | Audience URI used for OIDC (optional)                                             |

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
