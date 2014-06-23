angular.module('ot.service.core', [
    'ot.alert.confirm'
])
/**
 * Basic services
 */
    .service('ot', function ($q, $rootScope, otNotificationService) {
        var self = {};

        /**
         * Initial view
         */
        self.view = function () {
            $rootScope.view = {
                title: '',
                commands: [],
                breadcrumbs: self.homeBreadcrumbs()
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
        return self;
    })
;