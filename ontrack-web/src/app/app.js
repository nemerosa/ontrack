const ontrack = angular.module('ontrack', [
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
        'ot.directive.duration',
        'ot.directive.creation',
        'ot.directive.validationRunData',
        'ot.directive.validationDataTypeConfig',
        'ot.directive.validationDataTypeDecoration',
        'ot.directive.projectLabel',
        'ot.directive.validationRunStatusComment',
        'ot.directive.search-box',
        'ot.directive.range-selector',
        'ot.directive.yes-no',
        // Dialogs
        'ot.dialog.applicationInfo',
        // Services
        'ot.service.core',
        'ot.service.user',
        'ot.service.info',
        'ot.service.task',
        'ot.service.form',
        'ot.service.configuration',
        'ot.service.graphql',
        'ot.service.label',
        'ot.service.search',
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
        'ot.view.admin.health',
        'ot.view.admin.extensions',
        'ot.view.admin.jobs',
        'ot.view.admin.labels',
        'ot.view.admin.log-entries',
        'ot.view.admin.predefined-validation-stamps',
        'ot.view.admin.predefined-promotion-levels',
        'ot.view.admin-group-mappings',
        'ot.view.user-profile'
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
        .run(function ($rootScope, $log, $http, $ocLazyLoad, $q, $urlRouter, ot, otUserService, otInfoService, otSearchService) {
            /**
             * Loading the extensions
             */
            $log.debug('[app] Loading extensions...');
            ot.pageCall($http.get('rest/extensions')).then(function (extensionList) {
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

            /**
             * Global search
             */
            $rootScope.globalSearchConfig = {
                css: "navbar-form navbar-right",
                resetOnSearch: true
            };

        })
        // Main controller
        .controller('AppCtrl', function ($log, $modal, $scope, $rootScope, $state, $http, ot, otUserService, otInfoService, otTaskService, otFormService, otSearchService) {

            $log.debug('[app] Initialising the app controller...');

            // Logout
            $scope.logout = function () {
                ot.call($http.post("logout", {})).then(function () {
                    location.href = '/login?logout';
                });
            };

            // User menu filter
            $scope.userMenuFilter = '';

            /**
             * Application info mgt
             */

            $scope.loadApplicationInfo = function () {
                $modal.open({
                    templateUrl: 'app/dialog/dialog.applicationInfo.tpl.html',
                    controller: 'otDialogApplicationInfo'
                });
            };

            $scope.displayVersionInfo = function (info) {
                otInfoService.displayVersionInfo(info);
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
             * Cancel running tasks when changing page
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
