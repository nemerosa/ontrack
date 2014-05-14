angular.module('ot.service.core', [
])
/**
 * Basic services
 */
    .service('ot', function ($q, $log, $interpolate) {
        var self = {};

        /**
         * Error message for a HTTP call
         */
        self.error = function (response) {
            if (response.status == 400) {
                return {
                    type: 'error',
                    content: response.message
                };
            } else if (status == 401 || status == 403) {
                return {
                    type: 'error',
                    content: 'Not authorized.'
                };
            } else {
                return {
                    type: 'error',
                    content: response.error
                };
            }
        };

        /**
         * Wraps a HTTP call into a promise.
         */
        self.call = function (httpCall) {
            var d = $q.defer();
            httpCall
                .success(function (result) {
                    d.resolve(result);
                })
                .error(function (response) {
                    var errorMessage = self.error(response);
                    if (errorMessage) {
                        d.reject(errorMessage);
                    }
                });
            return d.promise;
        };

        return self;
    })
;