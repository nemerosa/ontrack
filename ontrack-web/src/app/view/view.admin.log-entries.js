angular.module('ot.view.admin.log-entries', [
    'ui.router',
    'ot.service.core',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-log-entries', {
            url: '/admin-log-entries',
            templateUrl: 'app/view/view.admin.log-entries.tpl.html',
            controller: 'AdminLogEntriesCtrl'
        });
    })
    .controller('AdminLogEntriesCtrl', function ($scope, $http, ot, otAlertService, otTaskService) {
        var view = ot.view();
        view.title = "Log entries";
        view.description = "List of application log messages.";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

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
        loadLogs();

        var interval = 10 * 1000; // 10 seconds
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