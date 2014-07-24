angular.module('ot.extension.git.sync', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('git-sync', {
            url: '/extension/git/sync/{branch}',
            templateUrl: 'app/extension/git/git.sync.tpl.html',
            controller: 'GitSyncCtrl'
        });
    })
    .controller('GitSyncCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otStructureService) {

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

    })
;