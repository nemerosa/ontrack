# The mobile UI

Yontrack serves two user interfaces from one instance: the desktop UI, at the root, and a
phone-sized one under `/mobile`. This page describes the shell they share the instance
through — the redirect, the boundary between them, and the route map that decides what a
phone sees. The mobile screens themselves are documented as they land.

## Why two UIs rather than one responsive one

The desktop UI has no responsive design at all: no `useBreakpoint`, no layout media queries,
no antd responsive grid props across roughly 1260 components and 62k lines. Retrofitting that
is a much larger and riskier change than building a small, purpose-built app for the handful
of things people actually do from a phone — check a build, promote it, deploy it.

The trade is that the mobile UI is deliberately **incomplete**. It covers a few flows well
rather than everything badly, and it says so when a user arrives somewhere it does not cover.

## What is shared, and what is not

| Shared | Not shared |
|---|---|
| Services, GraphQL fragments and mutations | Every layout component |
| Authorization helpers | `MainLayout`, `MainPage`, `MainPageBar`, `NavBar`, `UserMenu` |
| Theme tokens (`styles/globals.css`) and the pre-paint theme script | The mobile shell: `MobileLayout`, `MobileHeader`, `MobileBottomNav` |
| The next-auth session and the `/api/protected/graphql` proxy | The provider stack — see below |

Keep that boundary sharp. Sharing a layout component between the two UIs means a change made
for one silently deforms the other, in a viewport whoever made the change is not looking at.

`/mobile` is its own App Router root, with its own `<html>` and `<body>` — exactly like the
sign-in page under `app/(auth)`. It is therefore covered by neither `pages/_document.js` nor
the provider stack in `pages/_app.js`, and assembles its own in `app/mobile/MobileProviders.js`.
Theme resolution before the first paint uses the same `themeInitScript` both other roots use.

## The redirect

`middleware.js` sends phones to `/mobile`:

- **User agent detection, phones only.** It is the only thing a middleware knows about the
  device; viewport width is a client-side fact and arrives far too late. Tablets keep the
  desktop UI — they have the width for it. The rules are written to fail *towards* the desktop
  UI, which is the complete one. See `components/mobile/userAgent.js`.
- **A redirect, not a rewrite.** The PWA is scoped to `/mobile`, and a scope only works if
  that path actually appears in the URL. It also makes mobile pages bookmarkable.
- **A `yontrack-ui=desktop` cookie opts out**, per device rather than per user. It is set by
  the interstitial's "open the desktop version" and cleared by the "Mobile version" entry in
  the desktop user menu. Both halves matter: without the second, a phone that once chose the
  desktop UI is stranded on it, and once the mobile UI is installed as a PWA there is no
  address bar to escape with.

  It is a **session** cookie, on purpose. The way back is that user-menu entry — and at phone
  width the desktop UI's own page bar overlaps its user-menu trigger, so a tap there lands on
  "New project" instead. The escape hatch is not reliably reachable on the devices it exists
  for, and making the page bar responsive is precisely the retrofit the mobile UI exists to
  avoid. Ending the opt-out with the browser session turns "stranded for good" into "stranded
  until the browser restarts". Once the desktop page bar behaves at narrow widths, this can
  become a long-lived cookie.

The decision itself is a pure function in `components/mobile/mobileRedirect.js`, so it can be
tested without a request or the edge runtime. `middleware.js` is the adapter over it.

## The route map

`components/mobile/mobileRoutes.js` holds three things:

- `mobileEquivalent(pathname)` — the desktop routes that have a mobile screen. Deliberately
  short: a route earns an entry only once the mobile screen behind it exists and does the job.
- `isRedirectExempt(pathname)` — the paths the redirect must not touch at all: `/mobile`,
  `/auth` (redirecting the sign-in page would loop), `/api`, `/display` and static files.
- `describeDesktopRoute(pathname)` — what to call a desktop route in the interstitial.

A phone whose destination has no mobile equivalent lands on the **interstitial**
(`/mobile/desktop-only`), which names where they were going and offers both ways out. Neither
of the easy alternatives is acceptable: silently serving the desktop page costs the user the
readable UI the redirect exists to give them, and silently dropping them on the mobile home
loses what they came for.

**Adding a desktop route means deciding what a phone following a link to it should see.** That
decision is recorded in this map — a desktop route added without one silently sends phones to
the interstitial, which may well be right, but should be a choice rather than an oversight.

## The screens

| Screen | Route | What it is |
|---|---|---|
| Home | `/mobile` | The user's favourite projects and branches |
| Projects | `/mobile/projects` | Every project, filterable by name, with the favourite toggle |
| Search | `/mobile/search` | Not built yet — placeholder |
| Interstitial | `/mobile/desktop-only` | A route with no mobile equivalent |

**Home is the favourites, not a project list.** Someone reaching for their phone is checking
something they already care about; the full list is one tap away in the bottom bar for the
times it is not. Both screens read the existing `projects(favourites: true)` and
`branches(favourite: true)` — the mobile UI adds no GraphQL of its own.

A user who has never curated favourites on the desktop UI would otherwise open a blank home
screen, which reads as a broken app rather than as an empty list. Two things stop that: the
empty state (`MobileFavouritesEmpty`), which says what favourites are and links to the
project list, and the favourite toggle on the project list, which is where those favourites
get made. The demo seed marks a couple of its own, so the demo is populated on a phone —
see `doc/dev-guide/demo-seed.md`.

### Favourites

`MobileFavourite` is **controlled**: the favourite state lives in the list that renders the
toggle, and the toggle owns only whether its own call is in flight. Its parent then refetches
rather than patching its copy of the list, so there is one answer to "is this a favourite"
and it is the server's.

It is deliberately not the desktop `Favourite`, which is a 14px icon inside a
`Typography.Text` with a click handler — neither a 44px touch target nor a control a screen
reader announces. Same four mutations, own affordance; the mapping from entity type to
mutation is a pure module, `components/mobile/favourites/favouriteMutations.js`.

The mobile provider stack has no `EventsContextProvider`, so a screen cannot refresh off the
`project.favourite` page event the desktop widgets use. A local counter in the screen's
`deps` does the same job for one screen, which is all a phone shows at a time.

### Filtering a long list

An instance holds hundreds of projects, which is more than anyone scrolls through on a
phone, so the project list filters by name. It filters **on the server**, through
`projects(pattern:)` — an `ILIKE '%…%'` ordered by name. A client-side filter could only
narrow the answer to the last query, and would never reach a project the server had not
already sent.

Two things about that argument are worth knowing before reusing it: the server refuses
`pattern` alongside any *other* argument, and it tells "no pattern" from "empty pattern" by
whether the argument was supplied at all — so the screen sends `null`, never `''`. The
typing is debounced, because the alternative is one query per keystroke against that
`ILIKE`.

### The list shape

`MobileEntityGroup` and `MobileEntityRow` are the mobile UI's one list: a name, a line of
context under it, and a single trailing action. Plain `ul`/`li` rather than antd's `List`,
whose paddings and split lines are sized for a desktop page — and which is a layout
component, on the wrong side of the boundary above.

Rows are not links yet. The project and branch screens behind them are #1721; until they
exist, linking would send a tap to a 404, which is worse than a row that does not move.

## Adding a mobile screen

1. Add the page under `app/mobile/`, replacing its `MobileScreenPending` placeholder if it has
   one. Keep the page itself to the route and put the screen in a client component beside it,
   as `app/mobile/page.js` and `app/mobile/HomeScreen.js` do.
2. Add a row to `EQUIVALENTS` in `components/mobile/mobileRoutes.js`, so phones stop getting
   the interstitial for the desktop route it stands in for.
3. If it belongs in the bottom bar, add it to `MOBILE_NAV_ITEMS` in
   `components/mobile/layout/mobileNav.js` — and think hard first: three destinations are what
   fits a thumb.
4. Cover it in `ontrack-web-tests/tests/core/mobile.spec.js`, whose tests run in a phone
   browser context.
