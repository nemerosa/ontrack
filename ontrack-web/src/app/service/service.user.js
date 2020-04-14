angular.module('ot.service.user', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otUserService', function (ot, $q, $state, $location, $log, $interval, $http, $rootScope, otNotificationService, otFormService) {
        var self = {};

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[user] init');
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

        return self;
    })
;