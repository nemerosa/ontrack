angular.module('ot.service.user', [
    'ot.service.core'
])
    .service('otUserService', function (ot, $log, $interval, $http, $rootScope) {
        var self = {};

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[user] init');
            $interval(self.loadUser, 2000, 0);
            self.loadUser();
        };

        /**
         * Reloading the user
         */
        self.loadUser = function () {
            ot.call($http.get('user')).then(
                function success(userResource) {
                    // TODO Saves the user in the root scope
                },
                function error(message) {
                    $log.debug('[user] init - no user', message);
                    // TODO Displays a general error
                }
            );
        };

        return self;
    })
;