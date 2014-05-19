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

        // TODO Loading the build view
        function loadBuildView() {
        }

        // Loading the branch
        function loadBranch() {
            otStructureService.getBranch(branchId).then(function (branchResource) {
                $scope.branch = branchResource;
                // View settings
                view.title = branchResource.name;
                view.description = branchResource.description;
                // Branch commands
                view.commands = [
                    {
                        condition: function () {
                            return branchResource.createBuild;
                        },
                        id: 'createBuild',
                        name: "Create build",
                        cls: 'ot-command-build-new',
                        action: function () {
                            otStructureService.createBuild(branchResource.createBuild.href).then(loadBuildView);
                        }
                    },
                    ot.viewCloseCommand('/project/' + branchResource.project.id)
                ];
                // Loads the build view
                loadBuildView();
                // TODO Loads the promotion levels
                // TODO Loads the validation stamps
            });
        }

        // Initialization
        loadBranch();
        // TODO Loading the project's view
        // TODO Project commands

        // Creation of a promotion level
        $scope.createPromotionLevel = function () {
            otStructureService.createPromotionLevel($scope.branch.createPromotionLevel.href).then(loadBranch);
        };

    })
;