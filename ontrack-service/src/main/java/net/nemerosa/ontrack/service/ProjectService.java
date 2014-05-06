package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.Project;

/**
 * Management of the global structure of projects: branches, promotion levels, validation stamps.
 */
public interface ProjectService {

    Project getProject(String id);

}
