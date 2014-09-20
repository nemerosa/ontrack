angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.task',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.buildfilter',
    'ot.service.branch.copy',
    'ot.dialog.validationStampRunView',
    'ot.dialog.promotionRuns'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch', {
            url: '/branch/{branchId}',
            templateUrl: 'app/view/view.branch.tpl.html',
            controller: 'BranchCtrl'
        });
    })
    .controller('BranchCtrl', function ($state, $scope, $stateParams, $http, $modal, $location, ot, otFormService, otStructureService, otBuildFilterService, otAlertService, otTaskService, otNotificationService, otBranchCopyService) {
        var view = ot.view();
        // Branch's id
        var branchId = $stateParams.branchId;

        // Auto refresh status
        function refreshBuildView() {
            loadBuildView();
        }

        var refreshTaskName = 'Branch build view refresh';
        $scope.$watch('autoRefresh', function () {
            if ($scope.autoRefresh) {
                otTaskService.register(refreshTaskName, refreshBuildView, 30 * 1000);
            } else {
                otTaskService.stop(refreshTaskName);
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

        // Loading the build filters
        function loadBuildFilters() {
            // Loads filter forms
            ot.call($http.get($scope.branch._buildFilterForms)).then(function (buildFilterForms) {
                $scope.buildFilterForms = buildFilterForms;
                // Loads existing filters
                return otBuildFilterService.loadFilters($scope.branch);
            }).then(function (buildFilterResources) {
                $scope.buildFilterResources = buildFilterResources;
                // Loading the build view AFTER the filter have been loaded
                loadBuildView();
            });
        }

        // Loading the build view
        function loadBuildView() {
            // Parameters for the call
            // TODO Use '_view' link from the branch
            var uri = 'structure/branches/' + branchId + '/view';
            var config = {};
            // Adds the filter parameters
            var currentBuildFilterResource = otBuildFilterService.getCurrentFilter(branchId);
            if (currentBuildFilterResource) {
                $scope.currentBuildFilterResource = currentBuildFilterResource;
                config.params = currentBuildFilterResource.data;
                uri += '/' + currentBuildFilterResource.type;
            } else {
                $scope.currentBuildFilterResource = undefined;
            }
            // Call
            $scope.loadingBuildView = true;
            ot.call($http.get(uri, config)).then(
                function success(branchBuildView) {
                    $scope.loadingBuildView = false;
                    $scope.branchBuildView = branchBuildView;
                    // Selection of build boundaries
                    var buildViews = branchBuildView.buildViews;
                    if (buildViews && buildViews.length > 0) {
                        $scope.selectedBuild.from = buildViews[0].build.id;
                        $scope.selectedBuild.to = buildViews[buildViews.length - 1].build.id;
                    }
                }, function error() {
                    $scope.loadingBuildView = false;
                }
            );
        }

        // Loading the promotion levels
        function loadPromotionLevels() {
            ot.call($http.get($scope.branch._promotionLevels)).then(function (collection) {
                $scope.promotionLevelCollection = collection;
                $scope.promotionLevelSortOptions = {
                    disabled: !$scope.branch._reorderPromotionLevels,
                    stop: function (event, ui) {
                        var ids = $scope.promotionLevelCollection.resources.map(function (pl) {
                            return pl.id;
                        });
                        ot.call($http.put(
                            $scope.branch._reorderPromotionLevels,
                            { ids: ids}
                        ));
                    }
                };
            });
        }

        // Loading the validation stamps
        function loadValidationStamps() {
            ot.call($http.get($scope.branch._validationStamps)).then(function (collection) {
                $scope.validationStampCollection = collection;
                $scope.validationStampSortOptions = {
                    disabled: !$scope.branch._reorderValidationStamps,
                    stop: function (event, ui) {
                        var ids = $scope.validationStampCollection.resources.map(function (pl) {
                            return pl.id;
                        });
                        ot.call($http.put(
                            $scope.branch._reorderValidationStamps,
                            { ids: ids}
                        ));
                    }
                };
            });
        }

        // Loading of the other branches
        function loadOtherBranches() {
            ot.call($http.get($scope.branch._branches)).then(function (branchCollection) {
                view.commands.push({
                    id: 'switch-branch',
                    name: "Switch",
                    group: true,
                    actions: branchCollection.resources
                        .filter(function (theBranch) {
                            return theBranch.id != branchId;
                        })
                        .map(function (theBranch) {
                            return {
                                id: 'switch-' + theBranch.id,
                                name: theBranch.name,
                                uri: 'branch/' + theBranch.id
                            };
                        })
                });
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
                view.decorationsEntity = branchResource;
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
                    {
                        condition: function () {
                            return branchResource._update;
                        },
                        id: 'updateBranch',
                        name: "Update branch",
                        cls: 'ot-command-branch-update',
                        action: function () {
                            otStructureService.update(
                                branchResource._update,
                                "Update branch"
                            ).then(loadBranch);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.branch._delete;
                        },
                        id: 'deleteBranch',
                        name: "Delete branch",
                        cls: 'ot-command-branch-delete',
                        action: function () {
                            otAlertService.confirm({
                                title: "Deleting a branch",
                                message: "Do you really want to delete the branch " + $scope.branch.name +
                                    " and all its associated data?"
                            }).then(function () {
                                return ot.call($http.delete($scope.branch._delete));
                            }).then(function () {
                                $state.go('project', {projectId: $scope.branch.project.id});
                            });
                        }
                    },
                    ot.viewCloseCommand('/project/' + branchResource.project.id),
                    ot.viewActionsCommand(
                        $scope.branch._actions,
                        getTools($scope.branch)
                    )
                ];
                // Loads the build filters
                loadBuildFilters();
                // Loads the promotion levels
                loadPromotionLevels();
                // Loads the validation stamps
                loadValidationStamps();
                // Loads the other branches
                loadOtherBranches();
            });
        }

        // Initialization
        loadPermalink();
        loadBranch();

        // Gets the list of tools for a branch
        function getTools(branch) {
            var tools = [];
            // Copy from branch
            if (branch._copy) {
                tools.push({
                    id: 'branch-copy',
                    name: "Copy config. from branch",
                    action: copyFromBranch
                });
            }
            // OK
            return tools;
        }

        // Copy from a branch
        function copyFromBranch() {
            otBranchCopyService.copyFrom($scope.branch).then(loadBranch);
        }

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
         * Displaying the promotion runs
         */
        $scope.displayPromotionRuns = function (buildView, promotionRun) {
            $modal.open({
                templateUrl: 'app/dialog/dialog.promotionRuns.tpl.html',
                controller: 'otDialogPromotionRuns',
                resolve: {
                    config: function () {
                        return {
                            build: buildView.build,
                            promotionLevel: promotionRun.promotionLevel,
                            uri: promotionRun._all
                        };
                    }
                }
            }).result.then(loadBuildView, loadBuildView);
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

        /**
         * Build filter: new one
         */
        $scope.buildFilterNew = function (buildFilterForm) {
            otBuildFilterService.createBuildFilter({
                branchId: branchId,
                buildFilterForm: buildFilterForm
            }).then(function () {
                // Reloads the filters
                loadBuildFilters();
            });
        };

        /**
         * Editing a filter
         */
        $scope.buildFilterEdit = function (buildFilterResource) {
            otBuildFilterService.editBuildFilter({
                branch: $scope.branch,
                buildFilterResource: buildFilterResource,
                buildFilterForms: $scope.buildFilterForms
            }).then(function () {
                // Reloads the filters
                loadBuildFilters();
            });
        };

        /**
         * Applying a filter
         */
        $scope.buildFilterApply = function (buildFilterResource) {
            if (!buildFilterResource.removing) {
                otBuildFilterService.storeCurrent(branchId, buildFilterResource);
                loadBuildView();
            }
        };

        /**
         * Removing the current filter
         */
        $scope.buildFilterErase = function () {
            otBuildFilterService.eraseCurrent(branchId);
            loadBuildView();
        };

        /**
         * Removing an existing filter
         */
        $scope.buildFilterRemove = function (buildFilterResource) {
            buildFilterResource.removing = true;
            otBuildFilterService.removeFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
        };

        /**
         * Saving a local filter
         */
        $scope.buildFilterSave = function (buildFilterResource) {
            otBuildFilterService.saveFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
        };

        /**
         * Permalink to the current filter
         */
        $scope.buildFilterLink = function () {
            var currentFilter = otBuildFilterService.getCurrentFilter(branchId);
            if (currentFilter) {
                // TODO Special case: shared filter (only the name is needed)
                var jsonFilter = JSON.stringify(currentFilter);
                $location.hash(jsonFilter);
            }
        };

        /**
         * Loading the permalink at startup
         */
        function loadPermalink() {
            var jsonFilter = $location.hash();
            if (jsonFilter) {
                // Parsing the JSON
                try {
                    var json = JSON.parse(jsonFilter);
                    // TODO Special case: shared filter (only the name is needed)
                    // Applies the filter
                    otBuildFilterService.storeCurrent(branchId, json);
                    // Removes the hash after use
                    $location.hash('');
                } catch (e) {
                    // TODO Ignoring the error, just logging it
                    otNotificationService.error("Cannot get the filter from the permalink.");
                }
            }
        }

    })
;