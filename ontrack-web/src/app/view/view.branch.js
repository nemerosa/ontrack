angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.form',
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
    .controller('BranchCtrl', function ($interval, $state, $scope, $stateParams, $http, $modal, ot, otFormService, otStructureService) {
        var view = ot.view();
        // Branch's id
        var branchId = $stateParams.branchId;

        // Auto refresh status
        function refreshBuildView() {
            loadBuildView();
        }

        $scope.$watch('autoRefresh', function () {
            if ($scope.autoRefresh) {
                $scope.autoRefreshPromise = $interval(refreshBuildView, 30000);
            } else if ($scope.autoRefreshPromise) {
                $interval.cancel($scope.autoRefreshPromise);
            }
        });
        $scope.autoRefresh = localStorage.getItem('autoRefresh');
        $scope.toggleAutoRefresh = function () {
            $scope.autoRefresh = !$scope.autoRefresh;
            localStorage.setItem('autoRefresh', $scope.autoRefresh);
        };

        // Selected builds
        $scope.selectedBuild = {
            from: undefined,
            to: undefined
        };

        // Loading the build view
        function loadBuildView() {
            // TODO Use links from the branch
            // TODO Adds the filter parameters
            ot.call(
                $http.get('structure/branches/' + branchId + '/view')
            ).then(
                function success(branchBuildView) {
                    $scope.branchBuildView = branchBuildView;
                    // Selection of build boundaries
                    var buildViews = branchBuildView.buildViews;
                    if (buildViews) {
                        $scope.selectedBuild.from = buildViews[0].build.id;
                        $scope.selectedBuild.to = buildViews[buildViews.length - 1].build.id;
                    }
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

        /**
         * Creating a validation run
         */
        $scope.createValidationRun = function (buildView, validationStampRunView) {
            otStructureService.create(
                buildView.build._validate,
                'Validation for the build',
                {
                    postForm: function (form) {
                        return otFormService.updateFieldValue(form, 'validationStamp', validationStampRunView.validationStamp.id);
                    }
                }
            ).then(loadBuildView);
        };

        /**
         * Displaying the validation runs
         */
        $scope.displayValidationRuns = function (buildView, validationStampRunView) {
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
            });
        };

        /**
         * Build diff action
         */
        $scope.buildDiff = function (action) {
            var selectedBuild = $scope.selectedBuild;
            if (selectedBuild.from && selectedBuild.to && selectedBuild.from != selectedBuild.to) {
                $state.go(action.id, {
                    branch: branchId,
                    from: selectedBuild.from,
                    to: selectedBuild.to
                });
            }
        };

    })
;