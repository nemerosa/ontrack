angular.module('ot.extension.svn.sync', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('svn-sync', {
            url: '/extension/svn/sync/{branch}',
            templateUrl: 'app/extension/svn/svn.sync.tpl.html',
            controller: 'SVNSyncCtrl'
        });
    })
    .controller('SVNSyncCtrl', function ($stateParams, $state, $scope, $http, $interpolate, ot, otStructureService, otNotificationService) {

        var branchId = $stateParams.branch;
        var view = ot.view();
        view.commands = [
            ot.viewCloseCommand('/branch/' + branchId)
        ];

        // Loading of the sync information
        function load() {
            otStructureService.getBranch(branchId).then(function (branch) {
                $scope.branch = branch;
                view.title = $interpolate("Build synchronisation for branch {{project.name}}/{{name}}")(branch);
                view.breadcrumbs = ot.branchBreadcrumbs(branch);
            });
        }

        // Initialisation
        load();

        // Launching the sync
        $scope.launchSync = function () {
            ot.pageCall($http.post('extension/svn/sync/' + branchId, {})).then(function () {
                // Message
                otNotificationService.info("The build synchronisation has been launched in the background.");
                // Goes back to the branch
                $state.go('branch', {branchId: branchId});
            });
        };

    })
;