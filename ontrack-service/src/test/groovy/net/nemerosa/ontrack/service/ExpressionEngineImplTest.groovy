package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.ExpressionEngine
import org.codehaus.groovy.control.MultipleCompilationErrorsException
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

    @Test
    void 'Simple expression'() {
        assert engine.render('${branchName}', [branchName: '2.0']) == '2.0'
    }

    @Test
    void 'Simple template'() {
        assert engine.render('branches/${branchName}', [branchName: '2.0']) == 'branches/2.0'
    }

    @Test
    void 'Uppercase expression'() {
        assert engine.render('${branchName.toUpperCase()}', [branchName: 'ontrack-xx']) == 'ONTRACK-XX'
    }

    @Test(expected = MultipleCompilationErrorsException)
    void 'Secure resolve: no closure'() {
        engine.resolve('branchName + { "test" }', [branchName: 'test'])
    }

}