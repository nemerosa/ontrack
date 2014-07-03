angular.module('ot.extension.svn.sync', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('svn-sync', {
            url: '/extension/svn/sync/{branch}',
            templateUrl: 'app/extension/svn/svn.sync.tpl.html',
            controller: 'SVNSyncCtrl'
        });
    })
    .controller('SVNSyncCtrl', function ($stateParams, $scope, $http, $interpolate, $interval, ot) {

        var branchId = $stateParams.branch;
        var view = ot.view();
        view.commands = [
            ot.viewCloseCommand('/branch/' + branchId)
        ];

        $scope.synchronising = false;
        $scope.synchronisingDone = false;

        // Loading of the sync information
        function loadSyncInfo() {
            ot.pageCall($http.get($interpolate("extension/svn/sync/{{branch}}")($stateParams))).then(function (syncInfo) {
                $scope.syncInfo = syncInfo;
                // View configuration
                view.title = $interpolate("Build synchronisation for {{branch.name}}")(syncInfo);
                view.breadcrumbs = ot.branchBreadcrumbs(syncInfo.branch);
            });
        }

        // Initialisation
        loadSyncInfo();

        // Synchronisation
        $scope.sync = function () {
            $scope.synchronising = true;
            $scope.synchronisingDone = false;
            // Launches the sync
            ot.pageCall($http.post($scope.syncInfo._self)).then(
                function success(syncInfoStatus) {
                    // Gets the sync status regularly
                    checkSyncStatus(syncInfoStatus);
                    // TODO Displays a message at the end of the sync
                    // TODO Put synchronising to false at the end
                }, function error() {
                    $scope.synchronising = false;
                });
        };

        // Checking the status
        function checkSyncStatus(syncInfoStatus) {
            $scope.syncInfoStatus = syncInfoStatus;
            if (syncInfoStatus.finished) {
                $scope.synchronising = false;
                $scope.synchronisingDone = true;
            } else {
                // TODO Displays the already created builds
                // Go on with checking the status
                $interval(goCheckSyncStatus, 3000, 1);
            }
        }

        // Fetches and checks the last status
        function goCheckSyncStatus() {
            ot.pageCall($http.get($scope.syncInfoStatus._self)).then(
                function success(syncInfoStatus) {
                    checkSyncStatus(syncInfoStatus);
                },
                function error() {
                    $scope.synchronising = false;
                }
            );
        }

    })
;