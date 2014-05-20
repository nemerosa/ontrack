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
         * Default close command
         */
        self.viewCloseCommand = function (link) {
            return {
                id: 'close',
                name: "Close",
                cls: 'ot-command-close',
                link: link
            };
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
                    if (response.status == 403) {
                        // Goes back to the home back and refreshes with a status
                        location.href = '#/home?code=403';
                        // Rejects the current closure
                        d.reject();
                    } else {
                        d.reject({
                            status: response.status,
                            type: 'error',
                            content: response.message
                        });
                    }
                });
            return d.promise;
        };

        return self;
    })
    .service('otNotificationService', function ($rootScope) {
        var self = {};

        self.error = function (message) {
            self.display('error', message);
        };

        self.display = function (type, message) {
            $rootScope.notification = {
                type: type,
                content: message
            };
        };

        self.clear = function () {
            $rootScope.notification = undefined;
        };

        return self;
    })
;