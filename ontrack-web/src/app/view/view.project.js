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
    .controller('ProjectCtrl', function ($scope, $stateParams, $state, $http, ot, otStructureService, otAlertService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;
        // Loading the branches
        function loadBranches() {
            ot.call($http.get($scope.project._branches)).then(function (branchCollection) {
                $scope.branchCollection = branchCollection;
                // Loading the branch status view for each branch
                angular.forEach(branchCollection.resources, function (branch) {
                    ot.call($http.get(branch._status)).then(function (branchStatusView) {
                        branch.branchStatusView = branchStatusView;
                    });
                });
                // View commands
                view.commands = [
                    {
                        condition: function () {
                            return branchCollection._create;
                        },
                        id: 'createBranch',
                        name: "Create branch",
                        cls: 'ot-command-branch-new',
                        action: function () {
                            otStructureService.create(branchCollection._create, "New branch").then(loadBranches);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project._delete;
                        },
                        id: 'deleteProject',
                        name: "Delete project",
                        cls: 'ot-command-project-delete',
                        action: function () {
                            otAlertService.confirm({
                                title: "Deleting a project",
                                message: "Do you really want to delete the project " + $scope.project.name +
                                    " and all its associated data?"
                            }).then(function () {
                                return ot.call($http.delete($scope.project._delete));
                            }).then(function () {
                                $state.go('home');
                            });
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