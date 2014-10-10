package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.ExpressionEngine
import org.junit.Test

class ExpressionEngineImplTest {

    private final ExpressionEngine engine = new ExpressionEngineImpl()

    @Test
    void 'Null expression'() {
        assert engine.render(null, [:]) == null
    }

    @Test
    void 'Blank expression'() {
        assert engine.render('', [:]) == ''
    }

}