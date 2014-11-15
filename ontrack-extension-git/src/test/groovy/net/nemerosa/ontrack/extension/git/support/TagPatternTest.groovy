package net.nemerosa.ontrack.extension.git.support

import org.junit.Test

class TagPatternTest {

    @Test
    void 'Tag name from build: default'() {
        TagPattern pattern = new TagPattern('*')
        assert pattern.getTagNameFromBuildName('any').get() == 'any'
        assert pattern.getTagNameFromBuildName('2.0.0').get() == '2.0.0'
    }

    @Test
    void 'Tag name from build: simple'() {
        TagPattern pattern = new TagPattern('2.0.*')
        assert !pattern.getTagNameFromBuildName('any').present
        assert pattern.getTagNameFromBuildName('2.0.0').get() == '2.0.0'
        assert pattern.getTagNameFromBuildName('2.0.1').get() == '2.0.1'
        assert pattern.getTagNameFromBuildName('2.0.12').get() == '2.0.12'
        assert !pattern.getTagNameFromBuildName('v2.0.12').present
        assert !pattern.getTagNameFromBuildName('2.1.0').present
    }

    @Test
    void 'Tag name from build: capturing group'() {
        TagPattern pattern = new TagPattern('ontrack-(2.0.*)')
        assert !pattern.getTagNameFromBuildName('any').present
        assert pattern.getTagNameFromBuildName('2.0.0').get() == 'ontrack-2.0.0'
        assert pattern.getTagNameFromBuildName('2.0.1').get() == 'ontrack-2.0.1'
        assert pattern.getTagNameFromBuildName('2.0.12').get() == 'ontrack-2.0.12'
        assert !pattern.getTagNameFromBuildName('2.1.0').present
    }
}
