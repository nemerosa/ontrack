angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch', {
            url: '/branch/{branchId}',
            templateUrl: 'app/view/view.branch.tpl.html',
            controller: 'BranchCtrl'
        });
    })
    .controller('BranchCtrl', function ($scope, $stateParams, ot, otStructureService) {
        var view = ot.view();
        // Branch's id
        var branchId = $stateParams.branchId;

        // Loading the branch
        function loadBranch() {
            otStructureService.getBranch(branchId).then(function (branchResource) {
                $scope.branch = branchResource;
                // View settings
                view.title = branchResource.name;
                view.description = branchResource.description;
                // TODO Loads the build view
                // TODO Loads the promotion levels
                // TODO Loads the validation stamps
            });
        }

        // Initialization
        loadBranch();
        // TODO Loading the project's view
        // TODO Project commands
    })
;