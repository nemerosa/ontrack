package net.nemerosa.ontrack.extension.git.support

import org.junit.Test

class TagPatternTest {

    @Test
    void 'Tag name validation: default'() {
        TagPattern pattern = new TagPattern('*')
        assert pattern.pattern == '*'
        assert pattern.isValidTagName('any')
        assert pattern.isValidTagName('2.0.0')
    }

    @Test
    void 'Tag name validation: simple pattern'() {
        TagPattern pattern = new TagPattern('2.0.*')
        assert pattern.pattern == '2.0.*'
        assert !pattern.isValidTagName('any')
        assert pattern.isValidTagName('2.0.0')
        assert pattern.isValidTagName('2.0.1')
        assert pattern.isValidTagName('2.0.12')
        assert !pattern.isValidTagName('v2.0.12')
        assert !pattern.isValidTagName('2.1.0')
    }

    @Test
    void 'Tag name validation: capturing group'() {
        TagPattern pattern = new TagPattern('ontrack-(2.0.*)')
        assert pattern.pattern == 'ontrack-(2.0.*)'
        assert !pattern.isValidTagName('any')
        assert !pattern.isValidTagName('2.0.0')
        assert pattern.isValidTagName('ontrack-2.0.0')
        assert pattern.isValidTagName('ontrack-2.0.1')
        assert pattern.isValidTagName('ontrack-2.0.12')
        assert !pattern.isValidTagName('v2.0.12')
        assert !pattern.isValidTagName('ontrack-2.1.0')
    }

    @Test
    void 'Build name from tag: default'() {
        TagPattern pattern = new TagPattern('*')
        assert pattern.getBuildNameFromTagName('any').get() == 'any'
        assert pattern.getBuildNameFromTagName('2.0.0').get() == '2.0.0'
    }

    @Test
    void 'Build name from tag: simple'() {
        TagPattern pattern = new TagPattern('2.0.*')
        assert !pattern.getBuildNameFromTagName('any').present
        assert pattern.getBuildNameFromTagName('2.0.0').get() == '2.0.0'
        assert pattern.getBuildNameFromTagName('2.0.1').get() == '2.0.1'
        assert pattern.getBuildNameFromTagName('2.0.12').get() == '2.0.12'
        assert !pattern.getBuildNameFromTagName('v2.0.12').present
        assert !pattern.getBuildNameFromTagName('2.1.0').present
    }

    @Test
    void 'Build name from tag: capturing group'() {
        TagPattern pattern = new TagPattern('ontrack-(2.0.*)')
        assert !pattern.getBuildNameFromTagName('any').present
        assert !pattern.getBuildNameFromTagName('2.0.0').present
        assert pattern.getBuildNameFromTagName('ontrack-2.0.0').get() == '2.0.0'
        assert pattern.getBuildNameFromTagName('ontrack-2.0.1').get() == '2.0.1'
        assert pattern.getBuildNameFromTagName('ontrack-2.0.12').get() == '2.0.12'
        assert !pattern.getBuildNameFromTagName('v2.0.12').present
        assert !pattern.getBuildNameFromTagName('ontrack-2.1.0').present
    }

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
