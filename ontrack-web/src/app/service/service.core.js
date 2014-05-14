angular.module('ot.service.core', [
])
/**
 * Basic services
 */
    .service('ot', function ($q, $rootScope, $log, $interpolate) {
        var self = {};

        /**
         * Initial view
         */
        self.view = function () {
            $rootScope.view = {
                title: '',
                commands: []
            };
            return $rootScope.view;
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
                    d.reject({
                        type: 'error',
                        content: response.message
                    });
                });
            return d.promise;
        };

        return self;
    })
;