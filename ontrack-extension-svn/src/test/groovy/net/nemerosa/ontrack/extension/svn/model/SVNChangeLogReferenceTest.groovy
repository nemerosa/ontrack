package net.nemerosa.ontrack.extension.svn.model

import org.junit.Test

import static org.junit.Assert.*

class SVNChangeLogReferenceTest {

    @Test
    void 'Equal revisions: no change log'() {
        SVNChangeLogReference ref = new SVNChangeLogReference(
                '/project/trunk',
                150,
                150
        )
        assert ref.none
    }

    @Test
    void 'Correct revisions: change log'() {
        SVNChangeLogReference ref = new SVNChangeLogReference(
                '/project/trunk',
                150,
                151
        )
        assert !ref.none
    }

}