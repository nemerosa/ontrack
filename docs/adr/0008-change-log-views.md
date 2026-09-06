# The change log page is read through pluggable views, whose settings live in the URL and in user preferences

The change log page is gaining a second way to read the same change log — the semantic
change log, grouping commits by conventional-commit type — and we make the page pluggable on
a **single axis**, exactly as `0001-branch-content-views.md` did for the branch: `classic`
and `semantic` are peers in one registry, chosen from one menu in the command bar, with a
live `?view=` parameter making any choice linkable. Everything that ADR inherits — the
registry shape, the parameter winning over the stored preference, the default staying on what
existing users already know — applies here unchanged and is not restated.

Three decisions are specific to this page, and each would read as an inconsistency to someone
who did not know it was deliberate.

## The semantic view has no issues table

The classic view shows the change log's issues as their own panel, with a flyout exporting
them grouped by issue type. The semantic view shows **no issues panel at all**: issues appear
only inside the rendered text, when the `issues` option is on.

This is not an omission. The two things group different content — the *issue export* groups
issues by issue type, the *semantic change log* groups commits by commit type — and putting
both on one page would offer two format pickers for what a user would reasonably read as one
feature. The export flyout stays untouched, and stays in the classic view.

## The view's settings persist in server-side preferences, beside a panel that uses localStorage

`selectedChangeLogViewKey` and the four semantic options (`format`, `emojis`, `issues`,
`commits`) are fields on `Preferences`, so they follow a user between machines — while the
issue export flyout on the very same page keeps its settings in `localStorage`.

The two stores are not an accident of history. The view choice is the same kind of choice as
the branch content view, which ADR 0001 already put in `Preferences`; splitting one panel's
state across two stores to avoid that would be worse than the asymmetry. The export flyout's
`localStorage` entry is left alone because moving it is unrelated work with a migration
attached.

## The rendered output is shown raw, in every format

The `semantic` field returns a string in the chosen renderer's syntax, and the page shows that
string as-is in a monospace block with a copy button — including for `html`, where the source
is displayed rather than the rendered markup.

The point of choosing `jira` or `markdown` is to paste the result somewhere else, so the raw
text *is* the product. Rendering HTML as HTML would mean injecting server-produced markup into
the page for one format out of five, and can be added later as an explicit preview if anyone
asks for it.

## Consequences

The URL contract — `?view=`, plus flat `format`, `emojis`, `issues` and `commits` — is public
the moment someone shares a link, so the parameter names are effectively permanent, as are the
view keys `classic` and `semantic`. Parameters are written only when the user changes
something: a bare change log link keeps meaning "however *you* like to read it".

Because `SemanticChangelogRenderingServiceImpl` drops every commit without a conventional-commit
type, the semantic view is empty for projects not using them — including Yontrack's own
history. The view says so rather than rendering a blank panel, and the demo demonstrates the
feature on a project whose commits are conventional by construction.
