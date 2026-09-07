// @ts-check
const {expect} = require("@playwright/test");
const {login} = require("../../core/login");
const {
    SCMChangeLogPage,
    issues,
    provisionChangeLog,
    provisionSemanticChangeLog,
    semanticIssues,
} = require("./scm");
const {test} = require("../../fixtures/connection");
const {resetChangeLogPreferences} = require("@ontrack/preferences");

/**
 * The semantic change log view: the same change log read as commits grouped into sections by
 * their conventional-commit type. See `docs/adr/0008-change-log-views.md`.
 *
 * The chosen view is a *server-side preference*, so it outlives the test that set it and is
 * shared with every other spec — `scm-changelog.spec.js`, which asserts the classic view, sorts
 * right after this file. Each test therefore starts from the preferences of a user who has
 * never chosen, and the file puts them back when it is done.
 */

test.beforeEach(async ({ontrack}) => {
    await resetChangeLogPreferences(ontrack)
})

test.afterAll(async ({}, testInfo) => {
    // A fixture cannot be reached from `afterAll`, so the connection is rebuilt here — worth it
    // to leave the account as this file found it whatever happened above.
    const {createConnection} = require("@ontrack/connection")
    const {Ontrack} = require("@ontrack/ontrack")
    await resetChangeLogPreferences(new Ontrack(await createConnection()))
})

test('switching to the semantic view groups the commits by type', async ({page, ontrack}) => {
    const {from, to} = await provisionSemanticChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to})

    // The page opens on the classic view, which existing users keep: the preferences were
    // reset above, so this is what a user who has never chosen sees
    await changeLogPage.checkCommitMessage("search owners by their phone number", {present: true})

    await changeLogPage.selectView("Semantic")

    // One section per commit type, and the commit subjects under them
    await changeLogPage.checkSemanticContent("Features")
    await changeLogPage.checkSemanticContent("search owners by their phone number")
    await changeLogPage.checkSemanticContent("Fixes")
    await changeLogPage.checkSemanticContent("write the CSV in UTF-8")
    await changeLogPage.checkSemanticContent("Documentation")
    await changeLogPage.checkSemanticContent("describe the export format")

    // The commit before the change log's first boundary is not in it
    await changeLogPage.checkSemanticContent("bump the shared library", {present: false})

    // The choice is in the URL, so the reading can be shared
    await changeLogPage.checkUrlParameter('view', 'semantic')
})

test('each semantic option is reflected in the rendering and in the URL', async ({page, ontrack}) => {
    const {from, to} = await provisionSemanticChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to, params: {view: 'semantic'}})

    /**
     * Emojis: on by default, in the section titles
     */
    await changeLogPage.checkSemanticContent("✨")
    await changeLogPage.setSemanticOption('emojis', false)
    await changeLogPage.checkUrlParameter('emojis', 'false')
    await changeLogPage.checkSemanticContent("✨", {present: false})
    // ... and the sections are still there, only their titles are plainer
    await changeLogPage.checkSemanticContent("Features")

    /**
     * Issues: a section inside the rendered text, which is the only place the semantic view
     * shows them — it has no issues panel of its own.
     */
    await changeLogPage.checkSemanticContent(semanticIssues["ISS-31"].summary)
    await changeLogPage.setSemanticOption('issues', false)
    await changeLogPage.checkUrlParameter('issues', 'false')
    await changeLogPage.checkSemanticContent(semanticIssues["ISS-31"].summary, {present: false})

    /**
     * Commits: the classic commits panel, beside the rendered text
     */
    await expect(page.locator('#commits')).not.toBeVisible()
    await changeLogPage.setSemanticOption('commits', true)
    await changeLogPage.checkUrlParameter('commits', 'true')
    await changeLogPage.checkCommitMessage("feat(api): search owners by their phone number", {present: true})

    /**
     * Format: the syntax the change log is rendered in
     */
    await changeLogPage.checkSemanticContent("**api**")
    await changeLogPage.selectSemanticFormat("Jira")
    await changeLogPage.checkUrlParameter('format', 'jira')
    // Jira marks bold with one star where Markdown uses two. Matched with the bullet in front,
    // because `*api*` on its own is a substring of Markdown's `**api**` and would tell the two
    // renderings apart in neither direction.
    await changeLogPage.checkSemanticContent("* *api*")
    await changeLogPage.checkSemanticContent("**api**", {present: false})
})

/**
 * The promise the URL parameters exist for: copy the link, send it to somebody else, and they
 * read what you were reading.
 *
 * The link is opened in a fresh browser context, and it deliberately carries options which
 * contradict what the first context stored as the user's preference — otherwise the second
 * reading would be explained by the preference and the URL would be proving nothing.
 */
test('a shared link reproduces the reading, over the reader\'s own preference', async ({page, ontrack, browser}) => {
    const {from, to} = await provisionSemanticChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to, params: {view: 'semantic'}})

    // The reader's own preference: Jira, without emojis
    await changeLogPage.selectSemanticFormat("Jira")
    await changeLogPage.setSemanticOption('emojis', false)
    await changeLogPage.checkSemanticContent("**api**", {present: false})

    // The link somebody else shares, made in Markdown with emojis
    const sharedContext = await browser.newContext()
    try {
        const sharedPage = await sharedContext.newPage()
        await login(sharedPage, ontrack)
        const sharedChangeLog = new SCMChangeLogPage(sharedPage, ontrack)
        await sharedChangeLog.goToById({
            from, to,
            params: {view: 'semantic', format: 'markdown', emojis: 'true', issues: 'true', commits: 'false'},
        })

        // The link wins over the stored preference
        await sharedChangeLog.checkSemanticContent("✨")
        await sharedChangeLog.checkSemanticContent("Features")
        await sharedChangeLog.checkSemanticContent("**api**")
    } finally {
        await sharedContext.close()
    }
})

/**
 * `SemanticChangelogRenderingServiceImpl` drops every commit whose subject carries no
 * conventional-commit type, so a project not using them renders nothing at all. The view says
 * so, and points at the commits toggle.
 *
 * `issues` is off here because the empty state is keyed on the *rendering* being empty, and the
 * issues section is part of that rendering: with it on, a change log with issues but no typed
 * commits renders that section and nothing else. The page shows what the service returns and
 * invents nothing — see `docs/adr/0008-change-log-views.md` — so "no sections" is not something
 * it tries to detect.
 */
test('the semantic view explains itself when no commit carries a type', async ({page, ontrack}) => {
    // The classic fixture's commits are `ISS-20 Some message` — untyped by construction
    const {from, to} = await provisionChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to, params: {view: 'semantic', issues: 'false'}})

    await changeLogPage.checkSemanticEmpty()

    // The way out the empty state offers: read the commits as they are
    await changeLogPage.setSemanticOption('commits', true)
    await changeLogPage.checkCommitMessage("ISS-23 Fixing some CSS", {present: true})
})

/**
 * The same project with the issues section on: the commits are still all dropped, so what is
 * left is the issues and nothing else. Worth pinning down, because it is the state a project
 * not using conventional commits lands in by default.
 */
test('with issues on, an untyped change log renders its issues and no sections', async ({page, ontrack}) => {
    const {from, to} = await provisionChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to, params: {view: 'semantic', issues: 'true'}})

    await changeLogPage.checkSemanticContent("Issues")
    await changeLogPage.checkSemanticContent(issues["ISS-21"].summary)
    // None of the type sections, since no commit carries a type
    await changeLogPage.checkSemanticContent("Features", {present: false})
    await changeLogPage.checkSemanticContent("Fixes", {present: false})
})

/**
 * The view survives a reload because it is stored in the user's preferences, server-side —
 * unlike the classic view's export settings, which are in `localStorage`.
 */
test('the chosen view is restored on the next change log', async ({page, ontrack}) => {
    const {from, to} = await provisionSemanticChangeLog(ontrack)

    await login(page, ontrack)
    const changeLogPage = new SCMChangeLogPage(page, ontrack)
    await changeLogPage.goToById({from, to})
    await changeLogPage.selectView("Semantic")
    await changeLogPage.checkSemanticContent("Features")

    // A bare change log link, with no parameter at all: the preference decides
    await changeLogPage.goToById({from, to})
    await changeLogPage.checkSemanticContent("Features")
})
