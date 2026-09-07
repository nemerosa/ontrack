# Demo seed and reset

The demo environment's state is a function of the build, not an accumulation. An
accumulating demo drifts until nobody knows what state it is in, so every deployment resets
it: the seed program deletes everything and recreates the dataset.

The reset goes **through the Yontrack API**, not the database. No Postgres hooks, no PVC
churn, and no dependency on ArgoCD sync timing — a Helm pre-upgrade hook would fire on
*every* sync, so an unrelated values tweak would silently destroy the demo.

Settings stay covered by CasC and users live in Keycloak, so projects, environments and
dashboards are the only things the seed has to reset.

The token it runs with needs admin-level rights: it deletes projects, manages environments
and shares a dashboard (`DashboardSharing`).

## Running it

```bash
export YONTRACK_URL=https://demo.dev.yontrack.com
export YONTRACK_TOKEN=<a token on that instance>
./gradlew :ontrack-demo-seed:run
```

Against the local dev stack, read the port out of `.yontrack-dev/instance.env` (see
[DEVELOPMENT.md](../../DEVELOPMENT.md)) and point `YONTRACK_URL` at it.

`installDist` produces a standalone launcher, which is what a workflow uses so it does not
carry a Gradle build with it:

```bash
./gradlew :ontrack-demo-seed:installDist
ontrack-demo-seed/build/install/ontrack-demo-seed/bin/ontrack-demo-seed
```

The launcher runs on whatever `java` it finds, so it needs a JDK 21 on the `PATH` or a
`JAVA_HOME` pointing at one — the same JDK the rest of the build requires.

| Variable                  | Default              | Meaning                                            |
|---------------------------|----------------------|----------------------------------------------------|
| `YONTRACK_URL`            | _required_           | Instance to reset                                  |
| `YONTRACK_TOKEN`          | _required_           | API token on that instance                         |
| `DEMO_SEED_REPOSITORY`    | `.`                  | Checkout the changelog is read from                |
| `DEMO_SEED_CHANGELOG_MAX` | `25`                 | How many commits the changelog project shows       |
| `DEMO_SEED_URL_PATTERN`   | demo or local hosts  | Which instances the program is allowed to wipe     |

### The URL guard

The program deletes **every** project on whatever it is pointed at, and a production URL
differs from the demo's by a few characters. It therefore refuses to run against anything
that is not a `demo.` host or a local instance. Naming another instance is possible through
`DEMO_SEED_URL_PATTERN`, which is the difference between a decision and an accident.

### What the target instance must have

The dataset is not self-contained: it can only reference features the target instance
actually runs. Two of them are off by default, are on automatically in the `dev` profile —
so the local dev stack needs nothing — and must be switched on explicitly anywhere else,
which for the demo means an environment variable in its Helm values:

| Feature | Property | Helm value |
|---------|----------|------------|
| **Simulated gate** node executor (`executorId: mock`), which the CANARY promotion workflow is built on | `ontrack.config.extension.workflows.mock.enabled` | `ONTRACK_CONFIG_EXTENSION_WORKFLOWS_MOCK_ENABLED` |
| **Mock SCM**, which the change log on `petclinic` is read from | `ontrack.config.extension.scm.mock.enabled` | `ONTRACK_CONFIG_EXTENSION_SCM_MOCK_ENABLED` |

Without the first, the reset fails partway through, after the deletions, with
`Workflow node executor ID "mock" not found`. Without the second it fails the same way,
when the seed posts the demo's commits to an endpoint that is not there. The dataset
validation cannot catch either, because it checks the dataset against Yontrack's rules, not
against the target's configuration.

Neither belongs on an instance tracking real deliveries: one lets a workflow report a gate
as passed without anything having been verified, the other lets a project claim an SCM that
answers with whatever anyone posted to it.

### The changelog project

One project is seeded from the real changelog since the last release — every commit between
the last release tag and `HEAD`, one build each — so the demo does not go stale between the
times someone remembers to extend the curated dataset.

That needs tags and history, so a workflow running the seed has to check out with
`fetch-depth: 0`. When git cannot answer — no tags, no history, no git — the seed prints a
warning and carries on with an empty changelog project. That is deliberate: a demo one
project poorer beats a reset that refuses to run. The cost is that it degrades quietly, so
a smoke test asserting the demo is fresh will not catch a checkout misconfigured this way;
the warning in the log is the only signal.

### The change log

`petclinic` is the only project with an SCM, and it is the **mock** one: the seed registers
the commits behind the demo's change logs itself, over REST, rather than pointing the project
at a real repository. A real Git configuration would trade a self-contained reset for one
depending on credentials and network egress.

The commit subjects are conventional-commit ones on purpose. The semantic change log groups
commits by their type and `SemanticChangelogRenderingServiceImpl` drops every commit carrying
none, so a project writing subjects any other way demonstrates an empty semantic view — which
is why the change log is not on the `yontrack` project, whose subjects come from this
repository's own history and are overwhelmingly `#1234 Some message`.

**The demo's change log is in memory and does not survive a backend restart.**
`MockSCMExtension` keeps its repositories in a `mutableMapOf` on the bean. The demo resets on
every deployment, but a pod restart in between leaves the project pointing at a repository the
mock SCM no longer has. The build commit properties are in the database and survive, so the
change log is not empty — it **fails**, with `Repository petclinic not found`, and so do the
commit and issue info panels, until the next reset. The same applies locally:
`scripts/dev-stack.sh restart backend` loses whatever the seed registered. This is accepted
deliberately, for the reason above; a persistent mock SCM is filed separately as
[#1701](https://github.com/yontrack/yontrack/issues/1701).

Because those repositories outlive the projects the reset deletes, the seed **empties the
repository** before registering anything in it. Commit ids are derived from the branch and the
position of the commit on it, so a second run registering on top of the first would give every
commit a different id.

The seed registers all of it **over GraphQL** — `mockScmRegisterCommit`,
`mockScmRegisterIssue`, `mockScmDeleteRepository` — and not over the REST endpoints of
`MockSCMController` beside them. A deployed instance is reached through an ingress that routes
`/graphql` and `/hook` to the backend and everything else to the Next UI, so a
`POST /extension/scm/mock/commit` against the demo answers 404 from the UI whatever the backend
is configured with. That is what broke the first demo smoke run after this landed. The REST
endpoints stay for what only ever runs inside the cluster: the Playwright fixture in
`ontrack-web-tests/ontrack/extensions/scm/scm.js` and the files, branches and pull requests the
acceptance tests use.

Being reachable from outside also means the mutations are not content with an authenticated
user: each one checks a global function, so the token the seed runs with has to be an
administrator's — which it already had to be, to delete projects.

## Adding to the demo

A feature is not done until the demo seed shows it — the definition of done in `CLAUDE.md`
states the rule; this section says how to satisfy it.

The dataset is declarative, in
[`DemoContent`](../../ontrack-demo-seed/src/main/java/net/nemerosa/ontrack/demo/seed/DemoContent.kt).
Adding to the demo means adding entries there, not steps to a procedure.

Two rules keep the demo reproducible, and
`DemoSeedTest.running it twice in a row yields the same demo state` enforces them:

- **Nothing varies between runs.** No counters, no random data, no wall-clock names. Build
  creation times are the one exception and are expressed relative to the run
  (`BuildCreation.DaysAgo`), so the demo always reads as recent work.
- **Anything named has a fixed identity.** The dashboard and its widgets carry hard-coded
  UUIDs, because Yontrack rejects a second dashboard of the same name unless the UUID
  matches — a fresh UUID would make the second run fail.

`DemoDatasetValidation` checks the dataset against Yontrack's own rules — legal names, no
promotion to a level the branch does not declare, no link to a build that is never created
— **before** the reset deletes anything, and reports every problem at once. Destructive by
design must not mean blank on failure.

## How it is put together

| Piece               | Role                                                                  |
|---------------------|-----------------------------------------------------------------------|
| `DemoContent`       | What the demo shows, as data. The file a feature adds itself to.       |
| `DemoDataset`       | The vocabulary `DemoContent` is written in.                           |
| `DemoSeed`          | Deletes everything, then walks the dataset.                            |
| `DemoTarget`        | The Yontrack API, as far as the seed needs it.                        |
| `KdslDemoTarget`    | The one implementation that talks to a real instance, through the KDSL. |
| `GitChangelogSource`| Commits since the last release tag.                                    |
| `DemoSeedConfig`    | Environment variables, and the URL guard.                              |
| `DemoDatasetValidation` | Rejects a bad dataset before the reset deletes anything.           |

The `DemoTarget` seam is what makes the acceptance criterion testable: the unit tests run
the whole seed twice against an in-memory instance and compare the two states, with no
server involved.

## What it does not do

- **Validation runs are not backdated.** Builds and promotion runs carry the dataset's
  times; Yontrack's `createValidationRun` mutation takes no time, so a build dated two weeks
  ago shows validation runs stamped at reset time. Fixing this needs a server-side change.
- **The shared dashboard is not selected for visitors.** Yontrack only ever selects a
  dashboard for the account that saved it, so a visitor lands on the built-in dashboard and
  picks `Yontrack demo` from the list.
- **Another account's private dashboards survive.** The reset deletes every dashboard the
  seeding account can see: the shared ones and its own. Yontrack does not expose anyone
  else's private dashboards, so those are out of reach.
- **The change log does not survive a backend restart.** See above: the mock SCM holds its
  commits in memory, and the change log fails rather than reads empty once they are gone.
- **Commits are not backdated.** The mock SCM stamps a commit with the time it is registered
  and its REST endpoint takes no time, so a change log between two builds dated a week apart
  shows commits dated within the same second of the reset. The same limitation as validation
  runs, for the same reason, and it would need a server-side change to fix.
- **`KdslDemoTarget` has no automated test.** The seed is destructive by definition, so it
  cannot share an instance with the acceptance suite. Changes to it are verified by running
  the program against a throwaway instance — the local dev stack does fine — twice, and
  diffing the resulting state.
