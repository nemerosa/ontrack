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
    void 'Fixed expression'() {
        assert engine.render('Test', [:]) == 'Test'
    }

    @Test
    void 'Incomplete expression'() {
        assert engine.render('${Test', [:]) == '${Test'
    }

    @Test
    void 'Simple expression'() {
        assert engine.render('${sourceName}', [sourceName: '2.0']) == '2.0'
    }

    @Test
    void 'Simple template'() {
        assert engine.render('branches/${sourceName}', [sourceName: '2.0']) == 'branches/2.0'
    }

    @Test
    void 'Concatenation'() {
        assert engine.render("\${sourceName + '-test'}", [sourceName: '2.0']) == '2.0-test'
    }

    @Test
    void 'Escaping the curved bracket'() {
        assert engine.render("\${sourceName + '-{test}'}", [sourceName: '2.0']) == '2.0-{test}'
    }

    @Test
    void 'Embedded expression'() {
        assert engine.render('/my/project/tags/{build:${sourceName}*}', [sourceName: '1.0.2']) == '/my/project/tags/{build:1.0.2*}'
    }

    @Test
    void 'Uppercase expression'() {
        assert engine.render('${sourceName.toUpperCase()}', [sourceName: 'ontrack-xx']) == 'ONTRACK-XX'
    }

    @Test(expected = ExpressionCompilationException)
    void 'Compilation: no such property'() {
        engine.resolve('x', [sourceName: 'test'])
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: no closure'() {
        engine.resolve('sourceName + { "test" }', [sourceName: 'test'])
    }

    @Test(expected = ExpressionCompilationException)
    void 'Secure resolve: runtime not authorised'() {
        engine.resolve('sourceName + Runtime.runtime.freeMemory()', [sourceName: 'test'])
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
        on('sourceName + Runtime.runtime.totalMemory()', [sourceName: 'test']) {
            'java.lang.Runtime class cannot be accessed.'
        }
    }

    @Test
    void 'Secure resolve: system not authorised'() {
        on('sourceName + System.getenv("PATH")', [sourceName: 'test']) {
            "java.lang.System class cannot be accessed."
        }
    }

    @Test
    void 'Secure resolve: execute not authorised'() {
        on('sourceName + "ls".execute()', [sourceName: 'test']) {
            // Hmmm, this test won't work on Windows...
            "java.lang.UNIXProcess class cannot be accessed."
        }
    }

    @Test
    void 'Secure resolve: regex: replacement'() {
        assert engine.resolve('sourceName.replaceAll("_", ".")', [sourceName: '1_0']) == '1.0'
    }

}