var ontrack = angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'ngSanitize',
        // Templates as JS
        'ot.templates',
        // Directives
        'ot.directive.view',
        'ot.directive.misc',
        // Services
        'ot.service.user',
        // Views
        'ot.view.home',
        'ot.view.project',
        'ot.view.branch'
    ])
        // TODO HTTP configuration
        // TODO Runs the initial security service (in case of refresh)
        // TODO HTTP error interceptor
        // Routing configuration
        .config(function ($stateProvider, $urlRouterProvider) {
            // For any unmatched url, redirect to /state1
            $urlRouterProvider.otherwise("/home");
        })
        // Main controller
        .controller('AppCtrl', function ($scope, $rootScope, otUserService) {

            /**
             * User mgt
             */

            // User heart beat initialisation at startup
            otUserService.init();

            // User status
            $scope.logged = function () {
                return otUserService.logged();
            };

            /**
             * Scope methods
             */

            // Notification

            $scope.hasNotification = function () {
                return angular.isDefined($rootScope.notification);
            };
            $scope.notificationContent = function () {
                return $rootScope.notification.content;
            };
            $scope.notificationType = function () {
                return $rootScope.notification.type;
            };
            $scope.closeNotification = function () {
                $rootScope.notification = undefined;
            };


        })
    ;