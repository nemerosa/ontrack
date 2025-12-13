package net.nemerosa.ontrack.boot.support

import org.springframework.boot.test.context.SpringBootTest

/**
 * This annotation is to be used for most of the tests in `ontrack-ui`.
 *
 * It makes sure that a Spring Boot web application context is loaded.
 *
 * By default, all tests inheriting from [net.nemerosa.ontrack.it.AbstractITTestSupport]
 * _do not provide_ a web application context at all.
 *
 * @see net.nemerosa.ontrack.boot.ui.AbstractWebTestSupport
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
annotation class UITest
