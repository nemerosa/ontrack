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
    .controller('SVNSyncCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var branchId = $stateParams.branch;
        var view = ot.view();
        view.commands = [
            ot.viewCloseCommand('/branch/' + branchId)
        ];

        $scope.synchronising = false;

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
            // Launches the sync
            ot.pageCall($http.post($scope.syncInfo._self)).then(
                function success(syncInfoStatus) {
                    $scope.syncInfoStatus = syncInfoStatus;
                    $scope.synchronising = !syncInfoStatus.message;
                }, function error() {
                    $scope.synchronising = false;
                });
        };

    })
;