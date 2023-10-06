package net.nemerosa.ontrack.extension.av.config

data class BranchSourceExpression(
    val id: String,
    val config: String?,
) {
    companion object {

        const val SEPARATOR = ":"

        fun parseBranchSourceExpression(expression: String) =
            if (expression.indexOf(SEPARATOR) >= 0) {
                BranchSourceExpression(
                    id = expression.substringBefore(SEPARATOR),
                    config = expression.substringAfter(SEPARATOR)
                )
            } else {
                BranchSourceExpression(id = expression, config = null)
            }
    }
}