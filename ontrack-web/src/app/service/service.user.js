var HEART_BEAT = 30000; // 30 seconds

angular.module('ot.service.user', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otUserService', function (ot, $state, $log, $interval, $http, $rootScope, otNotificationService, otFormService) {
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
            return $rootScope.user && $rootScope.user.account;
        };

        /**
         * Login
         */
        self.login = function () {
            return otFormService.display({
                uri: $rootScope.user.login,
                title: "Sign in",
                submit: function (data) {
                    return ot.call($http.post(
                        $rootScope.user.login,
                        {},
                        {
                            headers: {
                                'Authorization': 'Basic ' + window.btoa(data.name + ':' + data.password)
                            }
                        }
                    ));
                }
            });
        };

        /**
         * Logout
         */
        self.logout = function () {
            return ot.call($http.post('user/logout', {})).then(function () {
                // Reloads the user information
                self.loadUser();
                // Goes back to the home page
                $state.go('home');
            });
        };

        return self;
    })
;