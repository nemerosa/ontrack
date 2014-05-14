angular.module('ot.view.project', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($scope, $stateParams, ot, otStructureService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;
        // Loading the branches
        function loadBranches() {
            otStructureService.getProjectBranches(projectId).then(function (branchCollection) {
                $scope.branchCollection = branchCollection;
                // View commands
                view.commands = [
                    {
                        condition: function () {
                            return branchCollection.create;
                        },
                        id: 'createBranch',
                        name: "Create branch",
                        cls: 'ot-command-branch-new',
                        action: function () {
                            otStructureService.createBranch(branchCollection.create.href).then(loadBranches);
                        }
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.title = projectResource.name;
                view.description = projectResource.description;
                // Loads the branches
                loadBranches();
            });
        }

        // Initialization
        loadProject();
        // TODO Loading the project's view
        // TODO Project commands
    })
;