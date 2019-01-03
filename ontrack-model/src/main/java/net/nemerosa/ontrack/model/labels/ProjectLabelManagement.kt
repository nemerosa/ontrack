package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.security.CoreFunction
import net.nemerosa.ontrack.model.security.ProjectFunction

/**
 * Authorization to manage the labels of a project
 */
@CoreFunction
interface ProjectLabelManagement : ProjectFunction
