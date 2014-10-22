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

    protected def on(String expression, Map<String, String> parameters, Closure<String> message) {
        try {
            engine.resolve(expression, parameters)
            fail('Should not have compiled')
        } catch (ExpressionCompilationException ex) {
            assert ex.message == """\
Expression "${expression}" cannot be compiled:
- ${message()}"""
        }
    }

    @Test
    void 'Secure resolve: runtime not authorised - output message'() {
        on('branchName + Runtime.runtime.totalMemory()', [branchName: 'test']) {
            'java.lang.Runtime class cannot be accessed.'
        }
    }

    @Test
    void 'Secure resolve: system not authorised'() {
        on('branchName + System.getenv("PATH")', [branchName: 'test']) {
            "java.lang.System class cannot be accessed."
        }
    }

    @Test
    void 'Secure resolve: execute not authorised'() {
        on('branchName + "ls".execute()', [branchName: 'test']) {
            // Hmmm, this test won't work on Windows...
            "java.lang.UNIXProcess class cannot be accessed."
        }
    }

    @Test
    void 'Secure resolve: regex: replacement'() {
        assert engine.resolve('branchName.replaceAll("_", ".")', [branchName: '1_0']) == '1.0'
    }

}