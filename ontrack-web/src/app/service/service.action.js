/**
 * Using UI actions
 */
angular.module('ot.service.action', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .service('otActionService', function ($q, ot, otFormService, otGraphqlService) {
        const self = {};

        // Using a form & a mutation
        self.runActionForm = (action, mutationConfig) => {
            // Gets the form URI
            const uri = action.links.form.uri;
            // Form configuration
            var formConfig = {
                uri: uri,
                title: action.links.form.description,
                submit: function (data) {
                    return callMutationWithForm(action.mutation, data, mutationConfig);
                }
            };
            // Displaying the form
            return otFormService.display(formConfig);
        };

        // Calling a mutation based on the content of a form
        const callMutationWithForm = (mutation, data, mutationConfig) => {
            const d = $q.defer();
            // Gets the query variables
            const variables = mutationConfig.variables(data);
            // Calling the GraphQL interface
            otGraphqlService.pageGraphQLCall(mutationConfig.query, variables).then(root => {
                // We expect the main mutation to have been called
                const returnedData = root[mutation];
                // Management of errors
                const errors = returnedData.errors;
                if (errors && errors.length > 0) {
                    if (errors.length === 1) {
                        const error = errors[0];
                        d.reject(error.message);
                    } else {
                        d.reject(errors.map(error => error.message).join(" | "));
                    }
                } else {
                    // Success
                    d.resolve(returnedData);
                }
            });
            // OK
            return d.promise;
        };

        // OK
        return self;
    })
;