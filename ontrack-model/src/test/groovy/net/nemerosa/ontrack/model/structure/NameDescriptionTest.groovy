package net.nemerosa.ontrack.model.structure

import org.junit.Test

import java.util.regex.Pattern

import static net.nemerosa.ontrack.model.structure.NameDescription.escapeName

class NameDescriptionTest {

    @Test
    void 'Equality'() {
        assert NameDescription.nd("a", "A a") == NameDescription.nd("a", "A a")
    }

    @Test
    void 'Name pattern'() {
        assert Pattern.matches(NameDescription.NAME, "Test")
        assert Pattern.matches(NameDescription.NAME, "2")
        assert Pattern.matches(NameDescription.NAME, "2.0.0")
        assert Pattern.matches(NameDescription.NAME, "2.0.0-alpha")
        assert Pattern.matches(NameDescription.NAME, "2.0.0-alpha-1")
        assert Pattern.matches(NameDescription.NAME, "2.0.0-alpha-1-14")
        assert !Pattern.matches(NameDescription.NAME, "2.0.0-alpha 1-14")
        assert Pattern.matches(NameDescription.NAME, "TEST")
        assert Pattern.matches(NameDescription.NAME, "TEST_1")
        assert Pattern.matches(NameDescription.NAME, "TEST_ONE")
        assert !Pattern.matches(NameDescription.NAME, "TEST ONE")
    }

    @Test
    void 'Escaping: ok'() {
        assert '2.0.0-beta-12' == escapeName('2.0.0-beta-12')
    }

    @Test
    void 'Escaping: special characters'() {
        assert '2.0.0-feature-accentu-e' == escapeName('2.0.0-feature-accentuée')
    }

    @Test
    void 'Escaping: slashes'() {
        assert 'feature-templating' == escapeName('feature/templating')
    }

}
