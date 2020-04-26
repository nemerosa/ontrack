angular.module('ot.view.admin.log-entries', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-log-entries', {
            url: '/admin-log-entries',
            templateUrl: 'app/view/view.admin.log-entries.tpl.html',
            controller: 'AdminLogEntriesCtrl'
        });
    })
    .controller('AdminLogEntriesCtrl', function ($scope, $http, ot, otAlertService) {
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

        function getDateTime(dateField, timeField) {
            var date = $scope.logFilter[dateField];
            var time = $scope.logFilter[timeField];
            console.log("date = ", date);
            console.log("time = ", time);
            if (date) {
                var dateTime = date;
                if (time) {
                    var hours = Number(time.substring(0, 2));
                    var minutes = Number(time.substring(3, 5));
                    dateTime.setHours(hours);
                    dateTime.setMinutes(minutes);
                    dateTime.setSeconds(0);
                    dateTime.setMilliseconds(0);
                }
                console.log("datetime = ", dateTime);
                return dateTime;
            }
            return undefined;
        }

        function getBeforeDateTime() {
            return getDateTime('beforeDate', 'beforeTime');
        }

        function getAfterDateTime() {
            return getDateTime('afterDate', 'afterTime');
        }

        function loadLogs() {
            var filter = {
                before: getBeforeDateTime(),
                after: getAfterDateTime(),
                authentication: $scope.logFilter.authentication,
                text: $scope.logFilter.text
            };
            console.log("filter = ", filter);
            var params = angular.copy(filter);
            params.offset = $scope.offset;
            params.count = $scope.pageSize;
            $scope.loadingEntries = true;
            ot.call($http.get('rest/admin/logs', {
                params: params
            })).then(function (logs) {
                $scope.logs = logs;
            }).finally(function () {
                $scope.loadingEntries = false;
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

        // Deletes all log entries
        $scope.deleteAll = function () {
            otAlertService.confirm({
                title: "Delete log entries",
                message: "Do you really want to delete all log entries?"
            }).then(function () {
                ot.pageCall($http.delete('rest/admin/logs')).then(loadLogs);
            });
        };

        // Reloads the log
        $scope.refreshList = loadLogs;

    })
;