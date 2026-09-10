const {devices, expect} = require('@playwright/test');
const {login} = require("./login");
const {selectUserMenu} = require("./userMenu");
const {test} = require("../fixtures/connection");

/**
 * The mobile UI.
 *
 * The user agent is the whole input to the redirect, so most of this file runs
 * under a phone device rather than the suite's default `Desktop Chrome` - which
 * would exercise none of it. The one test that must *not* look like a phone is
 * in its own block.
 *
 * The theme assertions read `data-theme` off <html>: it is what every colour
 * token keys off, and it is set before the first paint. They assume the shared
 * account is in the default `system` theme mode, exactly as `theme.spec.js`
 * does - which is also what leaves that mode behind.
 */

/*
 * A phone, minus `defaultBrowserType`: Playwright refuses that one inside a
 * describe group because it would force a new worker, and the suite runs on
 * chromium anyway.
 */
const {defaultBrowserType: _ignored, ...PHONE} = devices['Pixel 5']

/** Signs in from a phone - which already goes through the redirect. */
const signInOnPhone = (page, ontrack) => login(page, ontrack, undefined, undefined, {
    // The sign-in page has to be exempt from the redirect, or this never
    // completes. Landing on the mobile home is the proof that it is. Matched on
    // the screen's own test id rather than on its text: "Home" is also the label
    // of a bottom-bar tab, and Next's route announcer repeats a page title on
    // top of that.
    ready: page => page.getByTestId('mobile-screen-title'),
})

test.describe('the mobile UI on a phone', () => {

    test.use(PHONE)

    test('a phone lands on the mobile shell', async ({page, ontrack}) => {
        await signInOnPhone(page, ontrack)
        await page.goto(ontrack.connection.ui)
        await expect(page).toHaveURL(/\/mobile$/)
        await expect(page.getByTestId('mobile-header')).toBeVisible()
        await expect(page.getByTestId('mobile-nav')).toBeVisible()
        // The tab the user is on, for the eye and for a screen reader alike.
        await expect(page.getByTestId('mobile-nav-home')).toHaveAttribute('aria-current', 'page')
    })

    test('the shell renders in both themes', async ({page, ontrack}) => {
        await signInOnPhone(page, ontrack)

        await page.emulateMedia({colorScheme: 'light'})
        await page.goto(`${ontrack.connection.ui}/mobile`)
        await expect(page.getByTestId('mobile-header')).toBeVisible()
        await expect(page.locator('html')).toHaveAttribute('data-theme', 'light')

        await page.emulateMedia({colorScheme: 'dark'})
        await page.reload()
        await expect(page.getByTestId('mobile-header')).toBeVisible()
        await expect(page.locator('html')).toHaveAttribute('data-theme', 'dark')
    })

    test('the home screen is the favourites, and the project list is one tap away', async ({page, ontrack}) => {
        // A project nothing has starred yet, so the loop below starts where a
        // first-time user starts.
        const project = await ontrack.createProject()

        await signInOnPhone(page, ontrack)
        await page.goto(`${ontrack.connection.ui}/mobile`)

        // One tap, from the bottom bar.
        await page.getByTestId('mobile-nav-projects').click()
        await expect(page).toHaveURL(/\/mobile\/projects$/)

        // Through the filter rather than by scrolling: an instance holds far more
        // projects than a phone screen, which is what the filter is for.
        await page.getByTestId('mobile-projects-filter').fill(project.name)
        await expect(page.getByTestId('mobile-projects')).toContainText(project.name)

        const star = page.getByTestId(`mobile-favourite-project-${project.id}`)
        await expect(star).toHaveAttribute('aria-pressed', 'false')
        await star.click()
        await expect(star).toHaveAttribute('aria-pressed', 'true')

        // Home is the favourites, so what was just starred is on it.
        await page.getByTestId('mobile-nav-home').click()
        await expect(page.getByTestId(`mobile-project-${project.id}`)).toContainText(project.name)

        // And unstarring from the home screen itself takes it away again.
        await page.getByTestId(`mobile-favourite-project-${project.id}`).click()
        await expect(page.getByTestId(`mobile-project-${project.id}`)).toHaveCount(0)
    })

    test('a favourite branch is on the home screen, under the project it belongs to', async ({page, ontrack}) => {
        // Favourite branches come from every project at once, so the home screen
        // has to say which project each one belongs to.
        const project = await ontrack.createProject()
        const branch = await project.createBranch()
        await branch.favourite()

        await signInOnPhone(page, ontrack)
        await page.goto(`${ontrack.connection.ui}/mobile`)

        const row = page.getByTestId(`mobile-branch-${branch.id}`)
        await expect(row).toContainText(branch.name)
        await expect(row).toContainText(project.name)
    })

    test('a route with no mobile equivalent gets the interstitial', async ({page, ontrack}) => {
        await signInOnPhone(page, ontrack)
        await page.goto(`${ontrack.connection.ui}/search`)
        // Named, not silently swallowed: neither the desktop page nor the mobile
        // home would tell the user what became of their link.
        await expect(page).toHaveURL(/\/mobile\/desktop-only\?target=%2Fsearch$/)
        await expect(page.getByTestId('desktop-only-destination')).toContainText('the search page')
    })

    test('a phone can switch to the desktop UI and back again', async ({page, ontrack}) => {
        await signInOnPhone(page, ontrack)
        await page.goto(`${ontrack.connection.ui}/search`)
        await page.getByTestId('open-desktop-version').click()

        // It actually gets there, rather than being bounced straight back by the
        // redirect - which is what the cookie is for.
        await expect(page).toHaveURL(/\/search$/)

        // And that cookie dies with the browser session. The way back below is
        // the intended escape, but at phone width the desktop UI's page bar
        // overlaps its own user-menu trigger, so it cannot be relied on; ending
        // the opt-out with the session is what stops a phone being stranded for
        // good. Playwright reports -1 for a session cookie.
        const [optOut] = (await page.context().cookies())
            .filter(cookie => cookie.name === 'yontrack-ui')
        expect(optOut.value).toEqual('desktop')
        expect(optOut.expires).toEqual(-1)

        // And it stays there.
        await page.goto(ontrack.connection.ui)
        await expect(page.getByText("Dashboard", {exact: true})).toBeVisible()

        // The way back. Without it, a phone that once chose the desktop UI would
        // be stranded on it - and once the mobile UI is installed as a PWA there
        // is no address bar to escape with.
        //
        // Widened first, and only for this step. What is under test is the
        // *contract* - the entry clears the cookie and the redirect resumes -
        // and the middleware reads the user agent, which is still a phone's.
        // At 393px the desktop UI's own page bar overlaps its user-menu trigger,
        // so the click lands on "New project" instead; that is a property of the
        // non-responsive desktop UI, not of anything this change added.
        await page.setViewportSize({width: 1280, height: 800})
        await selectUserMenu(page, "Mobile version")
        await expect(page).toHaveURL(/\/mobile$/)
        await expect(page.getByTestId('mobile-header')).toBeVisible()
    })
})

test('a desktop browser is left alone', async ({page, ontrack}) => {
    await login(page, ontrack)
    await expect(page).not.toHaveURL(/\/mobile/)
})
