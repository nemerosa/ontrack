package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.exceptions.ExpressionCompilationException
import net.nemerosa.ontrack.model.structure.ExpressionEngine
import org.junit.Test

import static org.junit.Assert.fail

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

    @Test(expected = ExpressionCompilationException)
    void 'Compilation: no such property'() {
        engine.resolve('x', [branchName: 'test'])
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: no closure'() {
        engine.resolve('branchName + { "test" }', [branchName: 'test'])
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: runtime not authorised'() {
        engine.resolve('branchName + Runtime.runtime.freeMemory()', [branchName: 'test'])
    }

    @Test
    void 'Secure resolve: runtime not authorised - output message'() {
        try {
            engine.resolve('branchName + Runtime.runtime.totalMemory()', [branchName: 'test'])
            fail("Should not have compiled")
        } catch (ExpressionCompilationException ex) {
            String message = ex.message
            assert message == """\
Expression "branchName + Runtime.runtime.totalMemory()" cannot be compiled:
- java.lang.Class class cannot be accessed."""
        }
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: system not authorised'() {
        engine.resolve('branchName + System.getenv("PATH")', [branchName: 'test'])
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: execute not authorised'() {
        engine.resolve('branchName + "ls".execute()', [branchName: 'test'])
    }

    @Test
    void 'Secure resolve: regex: replacement'() {
        assert engine.resolve('branchName.replaceAll("_", ".")', [branchName: '1_0']) == '1.0'
    }

}