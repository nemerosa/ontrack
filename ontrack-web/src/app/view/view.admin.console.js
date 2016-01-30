angular.module('ot.view.admin.console', [
    'ui.router',
    'ot.service.core',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-console', {
            url: '/admin-console',
            templateUrl: 'app/view/view.admin.console.tpl.html',
            controller: 'AdminConsoleCtrl'
        });
    })
    .controller('AdminConsoleCtrl', function ($scope, $http, ot, otAlertService, otTaskService, otNotificationService) {
        var view = ot.view();
        view.title = "Administration console";
        view.description = "Tools for the general management of ontrack";

        // Loads the statuses
        function loadHealth() {
            ot.call($http.get('admin/status')).then(function (health) {
                $scope.health = health;
            });
        }

        // Loads the jobs
        function loadJobs() {
            ot.call($http.get('admin/jobs')).then(function (jobs) {
                $scope.jobs = jobs;
                view.commands = [
                    ot.viewApiCommand(jobs._self),
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        // Loads the logs
        function loadLogs() {
            ot.call($http.get('admin/logs', {
                params: {
                    offset: 0,
                    count: 20
                }
            })).then(function (logs) {
                $scope.logs = logs;
            });
        }

        // Initialisation
        loadJobs();
        loadLogs();
        loadHealth();

        var interval = 10 * 1000; // 10 seconds
        otTaskService.register('Admin Console Load Jobs', loadJobs, interval);
        otTaskService.register('Admin Console Load Logs', loadLogs, interval);

        // Showing the details of a log entry
        $scope.showLogDetails = function (log) {
            otAlertService.popup({
                data: log,
                template: 'app/dialog/dialog.applicationLogEntry.tpl.html'
            });
        };

        // Duration formatting
        $scope.jobDuration = function (ms) {
            return moment.duration(ms, 'ms').humanize();
        };

        // Launching a job
        $scope.launchJob = function (job) {
            ot.pageCall($http.post(job._launch)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was launched.");
                } else {
                    otNotificationService.warning("Job is already running or is disabled and could not be launched.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Pausing a job
        $scope.pauseJob = function (job) {
            ot.pageCall($http.post(job._pause)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was paused.");
                } else {
                    otNotificationService.warning("Job could not be paused.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Resuming a job
        $scope.resumeJob = function (job) {
            ot.pageCall($http.post(job._resume)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job schedule was resumed.");
                } else {
                    otNotificationService.warning("Job could not be resumed.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Showing the error for a status message
        $scope.showError = function (config) {
            config.errorShown = true;
        };

        // Showing the details for a status message
        $scope.showDetails = function (config) {
            config.detailsShown = true;
        };

    })
;