package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.boot.Application
import net.nemerosa.ontrack.it.AbstractITTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * Generation of the documentation.
 */
@SpringBootTest(classes = [Application::class])
abstract class AbstractDocGenIT : AbstractITTestSupport() {

    @Autowired
    protected lateinit var docGenSupport: DocGenSupport

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    protected fun findAllBeansAnnotatedWith(annotationClass: KClass<out Annotation>): List<Any> {
        val beanNames = applicationContext.beanDefinitionNames
        val annotatedBeanNames = beanNames.filter { beanName ->
            val beanType = applicationContext.getType(beanName)
            beanType?.isAnnotationPresent(annotationClass.java) == true
        }
        return annotatedBeanNames.map { beanName ->
            applicationContext.getBean(beanName)
        }
    }

}