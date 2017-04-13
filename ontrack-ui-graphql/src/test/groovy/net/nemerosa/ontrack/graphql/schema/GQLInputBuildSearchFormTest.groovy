package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.BuildSearchForm
import org.junit.Test

class GQLInputBuildSearchFormTest {

    @Test
    void 'Null argument'() {
        def form = new GQLInputBuildSearchForm().convert(null);
        assert form == new BuildSearchForm().withMaximumCount(10)
    }

    @Test
    void 'Validation stamp name'() {
        def form = new GQLInputBuildSearchForm().convert([validationStampName: 'VS']);
        assert form == new BuildSearchForm().withValidationStampName('VS')
    }

    @Test
    void 'Build exact match'() {
        def form = new GQLInputBuildSearchForm().convert([buildName: '1.0.0-123', buildExactMatch: true]);
        assert form == new BuildSearchForm().withBuildName('1.0.0-123').withBuildExactMatch(true)
    }

}
