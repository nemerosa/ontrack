package net.nemerosa.ontrack.dsl.support

class BranchTemplateDefinition {

    def parameters = []
    def absencePolicy = 'DISABLE'
    def synchronisationSourceConfig = [
            id  : '',
            data: []
    ]

    /**
     * Defines a template parameter
     */
    def parameter(String name, String description = '', String expression = '') {
        parameters << [
                name       : name,
                description: description,
                expression : expression
        ]
    }

    /**
     * Defines a fixed list sync source
     */
    def fixedSource(Collection<String> names) {
        synchronisationSourceConfig = [
                id  : 'fixed',
                data: [
                        names: names
                ]
        ]
    }

    /**
     * Defines a fixed list sync source
     */
    def fixedSource(String... names) {
        fixedSource(names as List)
    }

    def getData() {
        [
                parameters                 : parameters,
                synchronisationSourceConfig: synchronisationSourceConfig,
                absencePolicy              : absencePolicy,
                // TODO Sync. interval
        ]
    }
}
