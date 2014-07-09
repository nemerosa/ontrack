angular.module('ot.view.admin.console', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-console', {
            url: '/admin-console',
            templateUrl: 'app/view/view.admin.console.tpl.html',
            controller: 'AdminConsoleCtrl'
        });
    })
    .controller('AdminConsoleCtrl', function ($scope, $http, $interval,  ot, otAlertService) {
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
            ot.call($http.get('admin/logs')).then(function (logs) {
                $scope.logs = logs;
            });
        }

        // Initialisation
        loadJobs();
        loadLogs();

        $interval(loadJobs, 5000);
        $interval(loadLogs, 5000);

        // Showing the details of a log entry
        $scope.showLogDetails = function (log) {
            otAlertService.popup({
                data: log,
                template: 'app/dialog/dialog.applicationLogEntry.tpl.html'
            });
        };

    })
;