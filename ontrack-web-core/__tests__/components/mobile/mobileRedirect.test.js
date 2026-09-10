import {decideMobileRedirect, resolveInterstitialTarget} from "@components/mobile/mobileRedirect"
import {DESKTOP_HOME, MOBILE_HOME, MOBILE_INTERSTITIAL} from "@components/mobile/mobileRoutes"

const PHONE = 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Mobile/15E148 Safari/604.1'
const DESKTOP = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15'

const decide = (overrides = {}) => decideMobileRedirect({
    pathname: '/',
    search: '',
    userAgent: PHONE,
    desktopOptOut: false,
    ...overrides,
})

describe('decideMobileRedirect', () => {

    it('sends a phone on the home page to the mobile home', () => {
        expect(decide()).toEqual({pathname: MOBILE_HOME, search: ''})
    })

    it('leaves a desktop browser alone', () => {
        expect(decide({userAgent: DESKTOP})).toBeNull()
    })

    it('leaves a phone alone once it has opted out of the mobile UI', () => {
        expect(decide({desktopOptOut: true})).toBeNull()
    })

    it('leaves a phone alone on a path the redirect must not touch', () => {
        expect(decide({pathname: '/auth/signin'})).toBeNull()
        expect(decide({pathname: '/mobile/projects'})).toBeNull()
    })

    it('carries the query string over to the mobile equivalent', () => {
        expect(decide({search: '?from=slack'}))
            .toEqual({pathname: MOBILE_HOME, search: '?from=slack'})
    })

    describe('when the route has no mobile equivalent', () => {

        it('sends the phone to the interstitial rather than the desktop page', () => {
            // Silently serving the desktop page costs the user the readable UI
            // the redirect exists to give them.
            const decision = decide({pathname: '/extension/scm/my-project/changelog'})
            expect(decision.pathname).toEqual(MOBILE_INTERSTITIAL)
        })

        it('tells the interstitial where the user was going', () => {
            // Dropping them on the mobile home instead would lose their intent.
            const decision = decide({pathname: '/extension/scm/my-project/changelog', search: '?from=1&to=2'})
            const target = new URLSearchParams(decision.search).get('target')
            expect(target).toEqual('/extension/scm/my-project/changelog?from=1&to=2')
        })

        it('does not let the target escape to another site', () => {
            // The target ends up in a navigation, so it has to stay a path on
            // this instance.
            const decision = decide({pathname: '//evil.example.com/'})
            const target = new URLSearchParams(decision.search).get('target')
            expect(target).toEqual(MOBILE_HOME)
        })
    })
})

describe('resolveInterstitialTarget', () => {

    it('keeps a path on this instance', () => {
        expect(resolveInterstitialTarget('/build/56?tab=validations'))
            .toEqual('/build/56?tab=validations')
    })

    it.each([
        ['//evil.example.com/', 'a protocol-relative URL'],
        ['https://evil.example.com/', 'an absolute URL'],
        ['javascript:alert(1)', 'a script URL'],
        // A browser normalises `\` to `/` while parsing, so these are host
        // references wearing a path's clothes.
        ['/\\evil.example.com/', 'a backslash-escaped host'],
        ['/\\/evil.example.com/', 'a mixed-slash host'],
        // C0 controls and spaces are stripped during parsing, which is exactly
        // what makes them dangerous: the string checked is not the one used.
        ['/\t/evil.example.com/', 'a tab-smuggled host'],
        ['/\n/evil.example.com/', 'a newline-smuggled host'],
        [' //evil.example.com/', 'a space-smuggled host'],
        [undefined, 'a missing parameter'],
        ['', 'an empty parameter'],
    ])('refuses %s (%s) and offers the desktop home instead', (raw) => {
        // The value goes straight into a navigation, and anyone can type it.
        expect(resolveInterstitialTarget(raw)).toEqual(DESKTOP_HOME)
    })
})
