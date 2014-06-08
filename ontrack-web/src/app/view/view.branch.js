angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.dialog.validationStampRunView'
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
            ot.call($http.get($scope.branch._promotionLevels)).then(function (collection) {
                $scope.promotionLevelCollection = collection;
            });
        }

        // Loading the validation stamps
        function loadValidationStamps() {
            ot.call($http.get($scope.branch._validationStamps)).then(function (collection) {
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
                view.breadcrumbs = ot.projectBreadcrumbs(branchResource.project);
                // Branch commands
                view.commands = [
                    {
                        condition: function () {
                            return branchResource._createBuild;
                        },
                        id: 'createBuild',
                        name: "Create build",
                        cls: 'ot-command-build-new',
                        action: function () {
                            otStructureService.createBuild(branchResource._createBuild).then(loadBuildView);
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
            otStructureService.create($scope.branch._createPromotionLevel, "New promotion level").then(loadBranch);
        };

        // Creation of a validation stamp
        $scope.createValidationStamp = function () {
            otStructureService.create($scope.branch._createValidationStamp, 'New validation stamp').then(loadBranch);
        };

    })
    .directive('otBranchBuildView', function ($modal, otStructureService) {
        return {
            restrict: 'E',
            templateUrl: 'app/view/view.branchBuildView.tpl.html',
            scope: {
                view: '=',
                validationStamps: '='
            },
            link: function (scope) {
                scope.$watch('view', function () {
                    if (scope.view) {
                        /**
                         * Creating a validation run
                         */
                        scope.createValidationRun = function (buildView, validationStampRunView) {
                            // TODO Prefills the validation stamp
                            otStructureService.create(buildView.build._validate, 'Validation for the build').then(
                                function on_success() {
                                    // FIXME Reloads the branch build view
                                }
                            );
                        };
                        /**
                         * Displaying the validation runs
                         */
                        scope.displayValidationRuns = function (buildView, validationStampRunView) {
                            $modal.open({
                                templateUrl: 'app/dialog/dialog.validationStampRunView.tpl.html',
                                controller: 'otDialogValidationStampRunView',
                                resolve: {
                                    config: function () {
                                        return {
                                            buildView: buildView,
                                            validationStampRunView: validationStampRunView
                                        };
                                    }
                                }
                            }).result.then(
                                function success() {
                                },
                                function error() {
                                }
                            );
                        };
                    }
                });
            }
        };
    })
;