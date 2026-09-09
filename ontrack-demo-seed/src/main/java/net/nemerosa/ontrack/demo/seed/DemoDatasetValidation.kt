package net.nemerosa.ontrack.demo.seed

import java.time.LocalDateTime

/**
 * Checks a dataset against the rules Yontrack would enforce, before anything is deleted.
 *
 * The seed is destructive by design, and it deletes before it creates. Without this, a
 * dataset the server rejects half-way through — an illegal name, a promotion level the
 * branch never declares — leaves the demo wiped and partly rebuilt, which is worse than
 * either the old demo or the new one. "Destructive by design" must not mean "blank on
 * failure".
 *
 * @throws IllegalArgumentException with every problem found, not just the first: fixing a
 * dataset one error per run is a poor way to spend a reset.
 */
fun DemoDataset.validate() {
    val problems = mutableListOf<String>()

    fun checkName(name: String, what: String) {
        if (!ENTITY_NAME.matches(name)) {
            problems += "$what name \"$name\" can only have letters, digits, dots, dashes or underscores."
        }
    }

    val buildRefs = mutableSetOf<BuildRef>()

    // Creation times are resolved against one arbitrary but fixed instant: `DaysAgo` is
    // monotonic in it, so which instant it is does not change the order it yields. Only a
    // branch mixing `DaysAgo` and `At` builds could read differently under another one, and
    // a branch doing that has no stable order to check in the first place.
    val reference = LocalDateTime.of(2000, 1, 1, 0, 0)

    projects.forEach { project ->
        checkName(project.name, "Project")
        project.scm?.issues?.forEach { issue ->
            if (!ISSUE_KEY.matches(issue.key)) {
                problems += "The ${project.name} project declares the issue \"${issue.key}\", " +
                        "which the mock issue service would not recognise in a commit message: " +
                        "it reads keys of the ABC-123 shape only."
            }
        }
        project.branches.forEach { branch ->
            checkName(branch.name, "Branch")
            if (branch.scmBranch != null && project.scm == null) {
                problems += "Branch ${branch.name} of ${project.name} follows the SCM branch " +
                        "\"${branch.scmBranch}\", but the project declares no SCM."
            }
            val promotionLevels = branch.promotionLevels.map { it.name }.toSet()
            val validationStamps = branch.validationStamps.map { it.name }.toSet()
            branch.promotionLevels.forEach { checkName(it.name, "Promotion level") }
            branch.validationStamps.forEach { checkName(it.name, "Validation stamp") }
            branch.builds.forEach { build ->
                checkName(build.name, "Build")
                buildRefs += BuildRef(project.name, branch.name, build.name)
                if (build.commits.isNotEmpty() && branch.scmBranch == null) {
                    problems += "Build ${build.name} of ${project.name}/${branch.name} declares " +
                            "commits, but the branch follows no SCM branch."
                }
                build.promotionLevels.forEach { promotionLevel ->
                    if (promotionLevel !in promotionLevels) {
                        problems += "Build ${build.name} of ${project.name}/${branch.name} " +
                                "is promoted to $promotionLevel, which the branch does not declare."
                    }
                }
                build.validations.forEach { validation ->
                    if (validation.validationStamp !in validationStamps) {
                        problems += "Build ${build.name} of ${project.name}/${branch.name} " +
                                "is validated against ${validation.validationStamp}, " +
                                "which the branch does not declare."
                    }
                }
            }
            // Yontrack orders the builds of a branch by creation ORDER, newest first: the
            // build created last is the one every view shows first, whatever creation time
            // it carries. A dataset declaring its builds newest first therefore reads
            // backwards everywhere - the pipeline timeline and the builds table alike.
            branch.builds.map { it.creation.resolve(reference) }
                .zipWithNext()
                .forEachIndexed { index, (previous, next) ->
                    if (next < previous) {
                        problems += "Builds of ${project.name}/${branch.name} must be declared " +
                                "oldest first: ${branch.builds[index + 1].name} is older than " +
                                "${branch.builds[index].name}."
                    }
                }
        }
    }

    // Links and deployments point at builds by name, and are only resolvable once every
    // project has been walked.
    projects.forEach { project ->
        project.branches.forEach { branch ->
            branch.builds.forEach { build ->
                build.links.forEach { ref ->
                    if (ref !in buildRefs) {
                        problems += "Build ${build.name} of ${project.name}/${branch.name} " +
                                "uses ${ref.build} of ${ref.project}/${ref.branch}, " +
                                "which the dataset never creates."
                    }
                }
            }
        }
    }

    val projectNames = projects.map { it.name }.toSet()
    environments.forEach { environment ->
        checkName(environment.name, "Environment")
        environment.slots.forEach { slot ->
            if (slot.project !in projectNames) {
                problems += "The ${environment.name} environment has a slot for ${slot.project}, " +
                        "which the dataset never creates."
            }
            slot.admissionRules.forEach { rule ->
                if (!ADMISSION_RULE_NAME.matches(rule.name)) {
                    problems += "The ${environment.name}/${slot.project} slot names an admission " +
                            "rule \"${rule.name}\"; a rule name starts with a letter and then has " +
                            "letters, digits or dashes only."
                }
            }
        }
    }

    // A deployment the server would refuse is the expensive kind of mistake: the demo would be
    // deleted, rebuilt, and left with an empty slot. The two rules checkable from the dataset
    // alone are checked here. The `environment` rule is not - it depends on what is deployed at
    // that point in the sequence, which is the server's own reading of its own state.
    val builds = projects.flatMap { project ->
        project.branches.flatMap { branch ->
            branch.builds.map { BuildRef(project.name, branch.name, it.name) to it }
        }
    }.toMap()
    deployments.forEach { deployment ->
        val ref = deployment.build
        val environment = environments.find { it.name == deployment.environment }
        val slot = environment?.slots?.find { it.project == ref.project }
        when {
            environment == null ->
                problems += "A deployment names the ${deployment.environment} environment, " +
                        "which the dataset never creates."

            slot == null ->
                problems += "A deployment puts a ${ref.project} build in ${deployment.environment}, " +
                        "which has no slot for that project."

            ref !in buildRefs ->
                problems += "The ${deployment.environment} environment deploys ${ref.build} of " +
                        "${ref.project}/${ref.branch}, which the dataset never creates."

            else -> {
                val build = builds.getValue(ref)
                slot.admissionRules.forEach { rule ->
                    val required = rule.config["promotion"] as? String
                    if (rule.ruleId == SlotAdmissionRules.PROMOTION && required != null &&
                        required !in build.promotionLevels
                    ) {
                        problems += "The ${deployment.environment}/${ref.project} slot only admits " +
                                "builds promoted to $required, and ${ref.build} of ${ref.branch} " +
                                "is not."
                    }
                    if (rule.ruleId == SlotAdmissionRules.BRANCH_PATTERN && !branchIncludedByPattern(ref.branch, rule.config)) {
                        problems += "The ${deployment.environment}/${ref.project} slot admits no " +
                                "build of ${ref.branch}, and ${ref.build} is one."
                    }
                }
            }
        }
    }

    require(problems.isEmpty()) {
        "The demo dataset is not valid, and nothing was deleted:\n" +
                problems.joinToString("\n") { "- $it" }
    }
}

/**
 * What Yontrack accepts as an entity name — `NameDescription.NAME` on the server side.
 * Repeated here rather than depended on: this module talks to Yontrack over its API, not
 * over its model classes.
 */
private val ENTITY_NAME = Regex("[A-Za-z0-9._-]+")

/**
 * What the mock issue service reads out of a commit message — `MockSCMExtension.issueRegex`
 * on the server side. An issue keyed anything else is one no commit is ever linked to, and
 * an issues section that silently stays empty.
 */
private val ISSUE_KEY = Regex("[A-Z]+-\\d+")
