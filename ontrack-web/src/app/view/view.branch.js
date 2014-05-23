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
    .controller('BranchCtrl', function ($scope, $stateParams, $http, ot, otStructureService) {
        var view = ot.view();
        // Branch's id
        var branchId = $stateParams.branchId;

        // Loading the build view
        function loadBuildView() {
            // TODO Use links from the branch
            // TODO Adds the filter parameters
            ot.call(
                $http.get('structure/branches/' + branchId + '/view')
            ).then(
                function success(branchBuildView) {
                    $scope.branchBuildView = branchBuildView;
                }
            );
        }

        // Loading the promotion levels
        function loadPromotionLevels() {
            ot.call($http.get($scope.branch.promotionLevels.href)).then(function (collection) {
                $scope.promotionLevelCollection = collection;
            });
        }

        // Loading the validation stamps
        function loadValidationStamps() {
            ot.call($http.get($scope.branch.validationStamps.href)).then(function (collection) {
                $scope.validationStampCollection = collection;
            });
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
                // Loads the promotion levels
                loadPromotionLevels();
                // Loads the validation stamps
                loadValidationStamps();
                // TODO Branch commands
            });
        }

        // Initialization
        loadBranch();

        // Creation of a promotion level
        $scope.createPromotionLevel = function () {
            otStructureService.createPromotionLevel($scope.branch.createPromotionLevel.href).then(loadBranch);
        };

        // Creation of a validation stamp
        $scope.createValidationStamp = function () {
            otStructureService.create($scope.branch.createValidationStamp.href, 'New validation stamp').then(loadBranch);
        };

    })
    .directive('otBranchBuildView', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/view/directive.branchBuildView.tpl.html',
            scope: {
                view: '=',
                validationStamps: '='
            }
        };
    })
;