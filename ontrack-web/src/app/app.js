var ontrack = angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'ui.sortable',
        'multi-select',
        'angular_taglist_directive',
        'ngSanitize',
        'oc.lazyLoad',
        // Directives
        'ot.directive.view',
        'ot.directive.misc',
        'ot.directive.entity',
        'ot.directive.field',
        'ot.directive.properties',
        'ot.directive.health',
        // Services
        'ot.service.core',
        'ot.service.user',
        'ot.service.info',
        'ot.service.task',
        'ot.service.form',
        'ot.service.configuration',
        // Views
        'ot.view.api',
        'ot.view.api-doc',
        'ot.view.home',
        'ot.view.search',
        'ot.view.settings',
        'ot.view.project',
        'ot.view.branch',
        'ot.view.build',
        'ot.view.promotionLevel',
        'ot.view.validationStamp',
        'ot.view.validationRun',
        'ot.view.buildSearch',
        'ot.view.admin.accounts',
        'ot.view.admin.global-acl',
        'ot.view.admin.project-acl',
        'ot.view.admin.console',
        'ot.view.admin.log-entries',
        'ot.view.admin.predefined-validation-stamps',
        'ot.view.admin.predefined-promotion-levels'
    ])
        // HTTP configuration
        .config(function ($httpProvider) {
            // General HTTP interceptor
            $httpProvider.interceptors.push('httpGlobalInterceptor');
            // Authentication using cookies and CORS protection
            $httpProvider.defaults.withCredentials = true;
        })
        // HTTP global interceptor
        .factory('httpGlobalInterceptor', function ($q, $log, $rootScope) {
            $rootScope.currentCalls = 0;
            return {
                'request': function (config) {
                    $rootScope.currentCalls++;
                    // $log.debug('Start of request, ', $rootScope.currentCalls);
                    return config;
                },
                'response': function (response) {
                    $rootScope.currentCalls--;
                    // $log.debug('End of request, ', $rootScope.currentCalls);
                    return response;
                },
                'responseError': function (rejection) {
                    $rootScope.currentCalls--;
                    // $log.debug('End of request with error, ', $rootScope.currentCalls);
                    return $q.reject(rejection);
                }
            };
        })
        // Routing configuration
        .config(function ($stateProvider, $urlRouterProvider) {
            // For any unmatched url, redirect to /home
            $urlRouterProvider.otherwise("/home");
            // Disables routing until extensions are loaded
            $urlRouterProvider.deferIntercept();
        })
        // Initialisation work
        .run(function ($rootScope, $log, $http, $ocLazyLoad, $q, $urlRouter, ot, otUserService, otInfoService) {
            /**
             * Loading the extensions
             */
            $log.debug('[app] Loading extensions...');
            ot.pageCall($http.get('extensions')).then(function (extensionList) {
                // Creates a start promise
                var d = $q.defer();
                d.resolve();
                var promise = d.promise;
                // Appends the load of extensions
                extensionList.extensions.forEach(function (extension) {
                     promise = promise.then(function (result) {
                         $log.debug('[app] Extension [' + extension.id + '] ' + extension.name + '...');
                         if (extension.options.gui) {
                             // Computing the path to the extension
                             var extensionPath;
                             var extensionVersion = extension.version;
                             if (extensionVersion && extensionVersion != 'none') {
                                 extensionPath = 'extension/' + extension.id + '/' + extensionVersion + '/module.js';
                             } else {
                                 extensionPath = 'extension/' + extension.id + '/module.js';
                             }
                             // Loading the extension dynamically at...
                             $log.debug('[app] Extension [' + extension.id + '] Loading GUI module at ' + extensionPath + '...');
                             // Returning the promise
                             return $ocLazyLoad.load(extensionPath).then(function (result) {
                                 $log.debug('[app] Extension [' + extension.id + '] GUI module has been loaded.');
                                 return result;
                             }, function (error) {
                                 $log.error('[app] Extension [' + extension.id + '] Error at loading: ' + error);
                                 return $q.reject(error);
                             });
                         } else {
                             return result;
                         }
                     });
                });
                // Returns the promise
                return promise;
            }).then(function () {
                // Loading is done
                $log.debug('[app] All extensions have been loaded - application ready');
                $rootScope.appReady = true;
                // Everything has been loaded
                $log.debug('[app] All extensions have been loaded - resuming the routing');
                // Resumes the routing
                $urlRouter.listen();
                $urlRouter.sync();
            }, function (error) {
                $log.error('[app] Could not load the application: ' + error.message);
                $rootScope.appLoadingError = error.message;
            });

            /**
             * User mgt
             */

            otUserService.init();

            /**
             * Application info mgt
             */

            otInfoService.init();
        })
        // Main controller
        .controller('AppCtrl', function ($log, $scope, $rootScope, $state, $http, ot, otUserService, otInfoService, otTaskService, otFormService) {

            $log.debug('[app] Initialising the app controller...');


            // User status
            $scope.logged = function () {
                return otUserService.logged();
            };

            // Login
            $scope.login = function () {
                otUserService.login().then(
                    function success() {
                        $log.debug('[app] Reloading after signing in.');
                        location.reload();
                    }
                );
            };

            // Logout
            $scope.logout = function () {
                otUserService.logout();
            };

            // User menu filter
            $scope.userMenuFilter = '';

            /**
             * Application info mgt
             */

            $scope.displayVersionInfo = function (versionInfo) {
                otInfoService.displayVersionInfo(versionInfo);
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

            // User menu actions

            $scope.showActionForm = function (action) {
                otFormService.display({
                    title: action.name,
                    uri: action.uri,
                    submit: function (data) {
                        return ot.call($http.post(action.uri, data));
                    }
                });
            };

            /**
             * Search
             */

            $scope.search = function () {
                if ($scope.searchToken) {
                    $state.go('search', {token: $scope.searchToken});
                    $scope.searchToken = '';
                }
            };

            /**
             * Cancel running tasks when chaning page
             */

            $scope.$on('$stateChangeStart', function () {
                otTaskService.stopAll();
            });


        })
    ;

// Bootstrapping

angular.element(document).ready(function () {
    if (console) console.log('[app] Bootstrapping the application');
    angular.bootstrap(document, ['ontrack']);
});
