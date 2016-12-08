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

        // Filtering
        $scope.logFilter = {};

        $scope.filterLogs = function () {
            $scope.offset = 0;
            $scope.pageSize = 20;
            loadLogs();
        };

        $scope.resetFilter = function () {
            $scope.offset = 0;
            $scope.pageSize = 20;
            delete $scope.logFilter.text;
            delete $scope.logFilter.authentication;
            loadLogs();
        };

        // Loads the logs
        $scope.offset = 0;
        $scope.pageSize = 20;
        function loadLogs() {
            var params = angular.copy($scope.logFilter);
            params.offset = $scope.offset;
            params.count = $scope.pageSize;
            ot.call($http.get('admin/logs', {
                params: params
            })).then(function (logs) {
                $scope.logs = logs;
            });
        }

        // Initialisation
        loadLogs();

        // Showing the details of a log entry
        $scope.showLogDetails = function (log) {
            otAlertService.popup({
                data: log,
                template: 'app/dialog/dialog.applicationLogEntry.tpl.html'
            });
        };

        // Previous page
        $scope.previousPage = function () {
            if ($scope.logs.pagination.prev) {
                $scope.offset -= $scope.pageSize;
                loadLogs();
            }
        };

        // Next page
        $scope.nextPage = function () {
            if ($scope.logs.pagination.next) {
                $scope.offset += $scope.pageSize;
                loadLogs();
            }
        };

    })
;