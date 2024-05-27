Project configuration for the auto-versioning
=============================================

Most of the auto-versioning is configured at branch level, using a list of auto-versioning source configurations, which describes which branches (source), which promotions and which versions a given branch must subscribe to.

When a branch is configured for the auto-versioning of a dependency, if it is not disabled explicitly, it remains eligible for the auto-versioning of this dependency, as long the source branch matches.

At project level, we can define an _auto versioning project property_ to refine the condition of applications:

* branch patterns - using includes/excludes patterns, if a target branch matches, then it is eligible for auto-versioning. If not, any auto-versioning request is ignored
* last activity - if the target branch last activity (given by its last build creation time) is before a given date, any auto-versioning request is ignored

All conditions must be true for the auto-versioning request to be eligible.
