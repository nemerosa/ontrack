package net.nemerosa.ontrack.dsl.support

class BranchTemplateDefinition {

    def parameters = []
    def absencePolicy = 'DISABLE'

    def parameter(String name, String description = '', String expression = '') {
        parameters << [
                name       : name,
                description: description,
                expression : expression
        ]
    }

    def getData() {
        [
                parameters                 : parameters,
                // TODO Sync. source
                synchronisationSourceConfig: [
                        id  : '',
                        data: []
                ],
                absencePolicy              : absencePolicy,
                // TODO Sync. interval
        ]
    }
}
