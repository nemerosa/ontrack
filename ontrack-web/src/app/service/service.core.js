angular.module('ot.service.core', [
    'ot.alert.confirm',
    'ot.alert.progress'
])
/**
 * Basic services
 */
    .service('ot', function ($log, $q, $rootScope, $http, $state, otNotificationService) {
        var self = {};

        /**
         * Initial view
         */
        self.view = function () {
            $rootScope.view = {
                title: '',
                commands: [],
                breadcrumbs: self.homeBreadcrumbs(),
                disableSearch: false
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
         * API command
         */
        self.viewApiCommand = function (link) {
            return {
                id: 'api',
                name: "API",
                cls: "ot-command-api",
                action: function () {
                    $log.debug("[api] Going to API page for ", link);
                    var encodedLink = encodeURIComponent(link);
                    $log.debug("[api] Encoding link ", encodedLink);
                    $state.go('api', {'link': encodedLink});
                }
            };
        };

        /**
         * Defines a command that list some actions in a select.
         *
         * The list of actions is returned by a call to the URI in parameter.
         */
        self.viewActionsCommand = function (uri, actions) {
            var def = {
                id: 'actions',
                name: "Tools",
                cls: 'ot-command-actions',
                group: true,
                actions: actions ? actions : []
            };
            self.call($http.get(uri)).then(function (actionResources) {
                def.actions = def.actions.concat(actionResources.resources);
            });
            return  def;
        };

        /**
         * Breadcrumbs for a link to the home page
         */
        self.homeBreadcrumbs = function () {
            return [
                ['home', '#/home']
            ];
        };

        /**
         * Breadcrumbs for a project
         */
        self.projectBreadcrumbs = function (project) {
            var bc = self.homeBreadcrumbs();
            bc.push([project.name, '#/project/' + project.id]);
            return bc;
        };

        /**
         * Breadcrumbs for a branch
         */
        self.branchBreadcrumbs = function (branch) {
            var bc = self.projectBreadcrumbs(branch.project);
            bc.push([branch.name, '#/branch/' + branch.id]);
            return bc;
        };

        /**
         * Breadcrumbs for a build
         */
        self.buildBreadcrumbs = function (build) {
            var bc = self.branchBreadcrumbs(build.branch);
            bc.push([build.name, '#/build/' + build.id]);
            return bc;
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
                        $log.debug('[403] HTTP 403 received - current location = ' + location.href);
                        // Goes back to the home back and refreshes with a status
                        location.href = '#/home?code=403&url=' + encodeURIComponent(location.href);
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

        /**
         * Wraps a HTTP call into a promise and uses the notification service for errors.
         */
        self.pageCall = function (httpCall) {
            var d = $q.defer();
            self.call(httpCall).then(
                function success(result) {
                    d.resolve(result);
                },
                function error(e) {
                    otNotificationService.error(e.content);
                    d.reject();
                }
            );
            return d.promise;
        };

        return self;
    })
    .service('otNotificationService', function ($rootScope) {
        var self = {};

        self.error = function (message) {
            self.display('error', message);
        };

        self.warning = function (message) {
            self.display('warning', message);
        };

        self.info = function (message) {
            self.display('info', message);
        };


        self.success = function (message) {
            self.display('success', message);
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
    .service('otAlertService', function ($modal) {
        var self = {};
        /**
         * Displays a confirmation box.
         *
         * For example:
         *
         * otAlertService.confirm({title: "My title", message: "My message"}).then(function () {
         *     // Does something on success
         * });
         *
         * @param config.title Title for the alert
         * @param config.message Message content for the alert
         * @returns Promise on the confirmation result.
         */
        self.confirm = function (config) {
            //noinspection JSUnusedGlobalSymbols
            return $modal.open({
                templateUrl: 'app/dialog/alert.confirm.tpl.html',
                controller: 'otAlertConfirm',
                resolve: {
                    alertConfig: function () {
                        return config;
                    }
                }
            }).result;
        };
        /**
         * Displays a generic dialog box.
         * @param config.data Data to use as model in the template
         * @param config.template URI to the template
         */
        self.popup = function (config) {
            return $modal.open({
                templateUrl: config.template,
                controller: function ($scope, $modalInstance) {
                    $scope.data = config.data;
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                }
            }).result;
        };
        /**
         * Displays a progress dialog
         * @param config.title Title of the dialog
         * @param config.promptMessage Optional. If set, the task won't start before the user has confirmed.
         * @param config.waitingMessage Message to display during the execution of the task
         * @param config.endMessage Message to display when task has been completed successfully
         * @param config.resultUri Optional. If set, includes a template with <code>result</code> as root object.
         * @param config.task Function that must return a promise for the execution of the task
         */
        self.displayProgressDialog = function (config) {
            return $modal.open({
                templateUrl: 'app/dialog/alert.progress.tpl.html',
                controller: 'otAlertProgress',
                resolve: {
                    config: function () {
                        return config;
                    }
                }
            }).result;
        };
        // OK
        return self;
    })
;