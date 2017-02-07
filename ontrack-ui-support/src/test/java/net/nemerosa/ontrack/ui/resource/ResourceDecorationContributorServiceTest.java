package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceDecorationContributorServiceTest {

    @Test
    public void no_contribution() {
        ResourceDecorationContributor contributor =
                mock(ResourceDecorationContributor.class);
        when(contributor.applyTo(any(ProjectEntityType.class))).thenReturn(false);
        ResourceDecorationContributorService service =
                new ResourceDecorationContributorServiceImpl(
                        Collections.singletonList(
                                contributor
                        )
                );
        Stream.of(ProjectEntityType.values()).forEach(type -> {
            List<LinkDefinition<ProjectEntity>> linkDefinitions = service.getLinkDefinitions(type);
            assertTrue(linkDefinitions.isEmpty());
        });
    }

    @Test
    public void one_type_contribution() {
        ResourceDecorationContributor contributor = new ResourceDecorationContributor() {
            @Override
            public List<LinkDefinition> getLinkDefinitions() {
                return Collections.singletonList(
                        LinkDefinitions.page()
                );
            }

            @Override
            public boolean applyTo(ProjectEntityType projectEntityType) {
                return projectEntityType == ProjectEntityType.BRANCH;
            }
        };

        ResourceDecorationContributorService service =
                new ResourceDecorationContributorServiceImpl(
                        Collections.singletonList(
                                contributor
                        )
                );

        Stream.of(ProjectEntityType.values()).forEach(type -> {
            List<LinkDefinition<ProjectEntity>> linkDefinitions = service.getLinkDefinitions(type);
            if (type == ProjectEntityType.BRANCH) {
                assertEquals(1, linkDefinitions.size());
                LinkDefinition<ProjectEntity> link = linkDefinitions.get(0);
                assertEquals("_page", link.getName());
            } else {
                assertTrue(linkDefinitions.isEmpty());
            }
        });
    }

    @Test
    public void contributions() {
        ResourceDecorationContributor contributor1 = new ResourceDecorationContributor() {
            @Override
            public List<LinkDefinition> getLinkDefinitions() {
                return Collections.singletonList(
                        LinkDefinitions.page("_page1", (b, rc) -> true, "page1")
                );
            }

            @Override
            public boolean applyTo(ProjectEntityType projectEntityType) {
                return projectEntityType == ProjectEntityType.BRANCH;
            }
        };
        ResourceDecorationContributor contributor2 = new ResourceDecorationContributor() {
            @Override
            public List<LinkDefinition> getLinkDefinitions() {
                return Collections.singletonList(
                        LinkDefinitions.page("_page2", (b, rc) -> true, "page2")
                );
            }

            @Override
            public boolean applyTo(ProjectEntityType projectEntityType) {
                return projectEntityType == ProjectEntityType.BRANCH;
            }
        };

        ResourceDecorationContributorService service =
                new ResourceDecorationContributorServiceImpl(
                        Arrays.asList(
                                contributor1,
                                contributor2
                        )
                );

        // Branch
        List<LinkDefinition<ProjectEntity>> linkDefinitions = service.getLinkDefinitions(ProjectEntityType.BRANCH);
        assertEquals(2, linkDefinitions.size());
        assertEquals("_page1", linkDefinitions.get(0).getName());
        assertEquals("_page2", linkDefinitions.get(1).getName());
    }

}
