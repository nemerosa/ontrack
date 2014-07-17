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
    .controller('AdminConsoleCtrl', function ($scope, $http, $interval, ot, otAlertService, otTaskService) {
        var view = ot.view();
        view.title = "Administration console";
        view.description = "Tools for the general management of ontrack";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loads the jobs
        function loadJobs() {
            ot.call($http.get('admin/jobs')).then(function (jobs) {
                $scope.jobs = jobs;
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

    })
;