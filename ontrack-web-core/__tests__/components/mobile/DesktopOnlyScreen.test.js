import "@testing-library/jest-dom"
import {act, render, screen} from "@testing-library/react"
import DesktopOnlyScreen from "@/app/mobile/desktop-only/DesktopOnlyScreen"
import {DESKTOP_UI_COOKIE_NAME} from "@components/mobile/desktopUiCookie"

const clearCookies = () => {
    document.cookie.split(';').forEach(c => {
        const name = c.split('=')[0].trim()
        if (name) document.cookie = `${name}=; max-age=0; path=/`
    })
}

const assign = jest.fn()

beforeAll(() => {
    // jsdom refuses to navigate, and will not let `assign` be spied on either,
    // so the whole `location` is replaced for the duration of the suite.
    Object.defineProperty(window, 'location', {
        configurable: true,
        value: {...window.location, assign},
    })
})

beforeEach(() => {
    assign.mockClear()
    clearCookies()
})

afterEach(clearCookies)

describe('the desktop-only interstitial', () => {

    it('names the destination when it knows it', () => {
        render(<DesktopOnlyScreen target="/extension/scm/my-project/changelog"/>)
        expect(screen.getByTestId('desktop-only-destination')).toHaveTextContent('a change log')
    })

    it('names the path itself when it has no better name', () => {
        // Still more use to the user than a made-up label.
        render(<DesktopOnlyScreen target="/something/entirely/new"/>)
        expect(screen.getByTestId('desktop-only-destination')).toHaveTextContent('/something/entirely/new')
    })

    describe('opening the desktop version', () => {

        it('goes to the page the user was actually heading for', () => {
            // Not the desktop home: losing their intent is the whole failure
            // this screen exists to prevent.
            render(<DesktopOnlyScreen target="/build/56?tab=validations"/>)
            act(() => screen.getByTestId('open-desktop-version').click())
            expect(assign).toHaveBeenCalledWith('/build/56?tab=validations')
        })

        it('remembers the choice, so the redirect does not bounce them back', () => {
            render(<DesktopOnlyScreen target="/build/56"/>)
            act(() => screen.getByTestId('open-desktop-version').click())
            expect(document.cookie).toContain(`${DESKTOP_UI_COOKIE_NAME}=desktop`)
        })
    })

    it('offers the mobile home as the other way out', () => {
        render(<DesktopOnlyScreen target="/build/56"/>)
        expect(screen.getByTestId('desktop-only-home')).toHaveAttribute('href', '/mobile')
    })

    it('does not remember a desktop preference merely for being shown', () => {
        // The cookie is a decision, not a side effect of landing here.
        render(<DesktopOnlyScreen target="/build/56"/>)
        expect(document.cookie).not.toContain(DESKTOP_UI_COOKIE_NAME)
    })
})
