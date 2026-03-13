// Set env vars before any import
process.env.NEXTAUTH_PROVIDER = "oidc"
process.env.NEXTAUTH_ISSUER = "https://login.example.com/tenant/v2.0"
process.env.NEXTAUTH_CLIENT_ID = "test-client-id"
process.env.NEXTAUTH_CLIENT_SECRET = "test-client-secret"
process.env.NEXTAUTH_SCOPE = "openid profile email offline_access"
process.env.NEXTAUTH_AUDIENCE = "api://test"

const DISCOVERY_URL = "https://login.example.com/tenant/v2.0/.well-known/openid-configuration"
const TOKEN_ENDPOINT = "https://login.example.com/tenant/oauth2/v2.0/token"

const {authOptions} = require("../../../app/api/auth/authOptions")

function mockTokenResponse(overrides = {}) {
    return {
        ok: true,
        json: async () => ({
            access_token: "new-access-token",
            expires_in: 3600,
            refresh_token: "new-refresh-token",
            ...overrides,
        }),
    }
}

describe("authOptions callbacks", () => {
    const originalFetch = global.fetch
    const originalDateNow = Date.now

    beforeEach(() => {
        global.fetch = jest.fn()
        jest.spyOn(console, "error").mockImplementation(() => {})
    })

    afterEach(() => {
        global.fetch = originalFetch
        Date.now = originalDateNow
        console.error.mockRestore?.()
    })

    // ------------------------------------------------------------------ //
    // JWT callback
    // ------------------------------------------------------------------ //

    describe("jwt callback", () => {

        it("saves tokens on initial login (account present)", async () => {
            const result = await authOptions.callbacks.jwt({
                token: {sub: "user1"},
                account: {
                    access_token: "at-123",
                    refresh_token: "rt-456",
                    expires_at: 1700000000,
                },
            })

            expect(result).toMatchObject({
                sub: "user1",
                accessToken: "at-123",
                refreshToken: "rt-456",
                expiresAt: 1700000000,
            })
            expect(global.fetch).not.toHaveBeenCalled()
        })

        it("returns token unchanged when not expired", async () => {
            const token = {
                accessToken: "at-existing",
                refreshToken: "rt-existing",
                expiresAt: 9999999999, // far future
            }
            Date.now = jest.fn(() => 1000) // well before expiry

            const result = await authOptions.callbacks.jwt({token, account: null})

            expect(result).toBe(token)
            expect(global.fetch).not.toHaveBeenCalled()
        })

        it("refreshes an expired token successfully", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000) // past any reasonable expiresAt

            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ({token_endpoint: TOKEN_ENDPOINT}),
                })
                .mockResolvedValueOnce(mockTokenResponse())

            const token = {
                accessToken: "old-at",
                refreshToken: "old-rt",
                expiresAt: 1_000_000,
            }

            const result = await authOptions.callbacks.jwt({token, account: null})

            expect(result.accessToken).toBe("new-access-token")
            expect(result.refreshToken).toBe("new-refresh-token")
            expect(result.expiresAt).toBe(Math.floor(2_000_000_000_000 / 1000) + 3600)
            expect(result.error).toBeUndefined()

            // Verify discovery call
            expect(global.fetch).toHaveBeenCalledWith(DISCOVERY_URL)

            // Verify token endpoint call
            const [url, opts] = global.fetch.mock.calls[1]
            expect(url).toBe(TOKEN_ENDPOINT)
            expect(opts.method).toBe("POST")
            const body = new URLSearchParams(opts.body)
            expect(body.get("grant_type")).toBe("refresh_token")
            expect(body.get("refresh_token")).toBe("old-rt")
            expect(body.get("client_id")).toBe("test-client-id")
            expect(body.get("client_secret")).toBe("test-client-secret")
            expect(body.get("scope")).toBe("openid profile email offline_access")
        })

        it("uses rotated refresh_token from response", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000)

            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ({token_endpoint: TOKEN_ENDPOINT}),
                })
                .mockResolvedValueOnce(mockTokenResponse({refresh_token: "rotated-rt"}))

            const result = await authOptions.callbacks.jwt({
                token: {accessToken: "old", refreshToken: "old-rt", expiresAt: 1},
                account: null,
            })

            expect(result.refreshToken).toBe("rotated-rt")
        })

        it("preserves old refreshToken when response omits refresh_token", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000)

            // Response deliberately omits refresh_token
            const responseWithoutRT = {
                ok: true,
                json: async () => ({
                    access_token: "new-at",
                    expires_in: 3600,
                    // no refresh_token
                }),
            }

            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ({token_endpoint: TOKEN_ENDPOINT}),
                })
                .mockResolvedValueOnce(responseWithoutRT)

            const result = await authOptions.callbacks.jwt({
                token: {accessToken: "old", refreshToken: "keep-me", expiresAt: 1},
                account: null,
            })

            expect(result.refreshToken).toBe("keep-me")
            expect(result.accessToken).toBe("new-at")
        })

        it("returns RefreshTokenError on HTTP error from token endpoint", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000)

            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ({token_endpoint: TOKEN_ENDPOINT}),
                })
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    json: async () => ({error: "invalid_grant"}),
                })

            const token = {accessToken: "old", refreshToken: "old-rt", expiresAt: 1}
            const result = await authOptions.callbacks.jwt({token, account: null})

            expect(result.error).toBe("RefreshTokenError")
            expect(console.error).toHaveBeenCalled()
        })

        it("returns RefreshTokenError when no refresh token is available", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000)

            const token = {accessToken: "old", expiresAt: 1}
            const result = await authOptions.callbacks.jwt({token, account: null})

            expect(result.error).toBe("RefreshTokenError")
            expect(console.error).toHaveBeenCalled()
            expect(global.fetch).not.toHaveBeenCalled()
        })

        it("returns RefreshTokenError on network failure", async () => {
            Date.now = jest.fn(() => 2_000_000_000_000)

            global.fetch.mockRejectedValueOnce(new Error("network down"))

            const token = {accessToken: "old", refreshToken: "old-rt", expiresAt: 1}
            const result = await authOptions.callbacks.jwt({token, account: null})

            expect(result.error).toBe("RefreshTokenError")
            expect(console.error).toHaveBeenCalled()
        })
    })

    // ------------------------------------------------------------------ //
    // Session callback
    // ------------------------------------------------------------------ //

    describe("session callback", () => {

        it("passes accessToken, refreshToken, and error to session", async () => {
            const session = {}
            const token = {accessToken: "x", refreshToken: "rt-x", error: "RefreshTokenError"}

            const result = await authOptions.callbacks.session({session, token})

            expect(result.accessToken).toBe("x")
            expect(result.refreshToken).toBe("rt-x")
            expect(result.error).toBe("RefreshTokenError")
        })

        it("leaves error undefined when token has no error", async () => {
            const session = {}
            const token = {accessToken: "y", refreshToken: "rt-y"}

            const result = await authOptions.callbacks.session({session, token})

            expect(result.accessToken).toBe("y")
            expect(result.refreshToken).toBe("rt-y")
            expect(result.error).toBeUndefined()
        })
    })
})
