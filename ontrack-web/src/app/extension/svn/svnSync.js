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
    .controller('SVNSyncCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otStructureService) {

        var branchId = $stateParams.branch;
        var view = ot.view();
        view.commands = [
            ot.viewCloseCommand('/branch/' + branchId)
        ];

        // Loading of the branch information
        function loadBranch() {
            otStructureService.getBranch(branchId).then(function (branch) {
                // View configuration
                view.title = $interpolate("Build synchronisation for {{name}}")(branch);
                view.breadcrumbs = ot.branchBreadcrumbs(branch);
            });
        }

        // Initialisation
        loadBranch();

    })
;