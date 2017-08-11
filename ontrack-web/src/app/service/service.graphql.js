angular.module('ot.service.graphql', [
    'ot.service.core'
])
    .service('otGraphqlService', function (ot, $q, $http, otNotificationService) {
        var self = {};

        /**
         * Performs a GraphQL query, displays the errors if there are some and returns
         * the data in a promise.
         *
         * @param query GraphQL text
         * @param variables Optional map of variables
         * @return Promise accepting the data as JSON in case of success, and the list of
         *         error messages in case of error.
         */
        self.pageGraphQLCall = function (query, variables) {
            var d = $q.defer();
            self.graphQLCall(query, variables).then(
                function success(result) {
                    d.resolve(result);
                },
                function error(messages) {
                    otNotificationService.error(messages[0]);
                    d.reject();
                }
            );
            return d.promise;
        };

        /**
         * Performs a GraphQL query and processes the errors if there are some.
         *
         * @param query GraphQL text
         * @param variables Optional map of variables
         * @return Promise accepting the data as JSON in case of success, and the list of
         *         error messages in case of error.
         */
        self.graphQLCall = function (query, variables) {
            var d = $q.defer();
            self.rawGraphQLCall(query, variables).then(function (json) {
                // Errors?
                var errors = json.errors;
                if (errors && errors.length > 0) {
                    // List of messages
                    d.reject(errors.map(function (error) {
                        return error.message;
                    }));
                }
                // Data
                else {
                    d.resolve(json.data);
                }
            });
            return d.promise;
        };

        /**
         * Performs a raw GraphQL query
         *
         * @param query GraphQL text
         * @param variables Optional map of variables
         * @return Promise accepting the raw JSON, without any error / data preprocessing
         */
        self.rawGraphQLCall = function (query, variables) {
            var params = {
                query: query
            };
            if (variables) {
                params.variables = JSON.stringify(variables);
            }
            return ot.pageCall($http.get('/graphql', {params: params}));
        };

        return self;
    })
;