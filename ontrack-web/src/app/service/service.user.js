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

        /**
         * Logout
         */
        self.logout = function () {
            return ot.call($http.post('user/logout', {})).then(function () {
                // Reloads the user information
                self.loadUser();
                // Goes back to the home page
                location.href = '/#home';
                location.reload();
            });
        };

        return self;
    })
;