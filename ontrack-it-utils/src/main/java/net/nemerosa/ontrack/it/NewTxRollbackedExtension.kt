package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate

class NewTxRollbackedExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    override fun beforeTestExecution(exc: ExtensionContext) {
        val transactionManager = getTransactionManager(exc)
        val transactionTemplate = TransactionTemplate(transactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        val status = transactionManager.getTransaction(transactionTemplate)
        exc.getStore(namespace).put(TX_STATUS, status)
    }

    override fun afterTestExecution(exc: ExtensionContext) {
        val transactionManager = getTransactionManager(exc)
        val status = exc.getStore(namespace).get(TX_STATUS, TransactionStatus::class.java)
        transactionManager.rollback(status)
    }

    companion object {

        private fun getTransactionManager(exc: ExtensionContext): PlatformTransactionManager {
            val applicationContext = SpringExtension.getApplicationContext(exc)
            return applicationContext.getBean(PlatformTransactionManager::class.java)
        }

        private val namespace: ExtensionContext.Namespace =
            ExtensionContext.Namespace.create(NewTxRollbackedExtension::class.java)

        private const val TX_STATUS = "txStatus"
    }
}