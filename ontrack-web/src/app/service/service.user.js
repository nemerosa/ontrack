var HEART_BEAT = 30000; // 30 seconds

angular.module('ot.service.user', [
    'ot.service.core'
])
    .service('otUserService', function (ot, $log, $interval, $http, $rootScope, otNotificationService) {
        var self = {};

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[user] init');
            $interval(self.loadUser, HEART_BEAT, 0);
            self.loadUser();
        };

        /**
         * Reloading the user
         */
        self.loadUser = function () {
            ot.call($http.get('user')).then(
                function success(userResource) {
                    $log.debug('[user] load user: ', userResource);
                    $log.debug('[user] logged: ', userResource.present);
                    // Saves the user in the root scope
                    $rootScope.user = userResource;
                    // Clears the error
                    otNotificationService.clear();
                },
                function error(message) {
                    $log.debug('[user] load - no user', message);
                    // Removes the user from the scope
                    $rootScope.user = undefined;
                    // Displays a general error
                    otNotificationService.error('Cannot connect. Please try later');
                }
            );
        };

        /**
         * User logged?
         */
        self.logged = function () {
            return $rootScope.user && $rootScope.user.present;
        };

        return self;
    })
;