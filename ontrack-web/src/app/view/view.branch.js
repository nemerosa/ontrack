angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.task',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.buildfilter',
    'ot.service.copy',
    'ot.dialog.validationStampRunView',
    'ot.dialog.promotionRuns',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch', {
            url: '/branch/{branchId}',
            templateUrl: 'app/view/view.branch.tpl.html',
            controller: 'BranchCtrl'
        });
    })
    .controller('BranchCtrl', function ($state, $scope, $stateParams, $http, $modal, $location,
                                        ot, otFormService, otStructureService, otAlertService, otTaskService, otNotificationService, otCopyService,
                                        otBuildFilterService, otGraphqlService) {
        const view = ot.view();
        // Branch's id
        const branchId = $stateParams.branchId;

        // Initial loading taking place... now
        $scope.loadingBuildView = true;

        // Auto refresh status
        function refreshBuildView() {
            loadBuildView();
        }

        const refreshTaskName = 'Branch build view refresh';
        $scope.$watch('autoRefresh', function () {
            if ($scope.autoRefresh) {
                // 1 minute interval
                otTaskService.register(refreshTaskName, refreshBuildView, 60 * 1000);
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
        $scope.selectedBuilds = {};

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

        // Switch branches loaded?
        let switchBranchesLoaded = false;

        function callBuildView(filterType, filterData) {
            $scope.loadingBuildView = true;
            otGraphqlService.pageGraphQLCall(`query BranchView($branchId: Int!, $filterType: String, $filterData: String) {
              branches(id: $branchId) {
                id
                name
                description
                annotatedDescription
                otherBranches {
                  id
                  name
                  disabled
                }
                buildDiffActions {
                  id
                  name
                  type
                  uri
                }
                links {
                  _reorderValidationStamps
                  _reorderPromotionLevels
                  _extra
                }
                promotionLevels {
                  id
                  name
                  image
                  _image
                  decorations {
                    ...decorationContent
                  }
                }
                validationStamps {
                  id
                  name
                  description
                  image
                  _image
                  decorations {
                    ...decorationContent
                  }
                  dataType {
                    descriptor {
                      id
                      displayName
                    }
                    config
                  }
                }
                builds(generic: {type: $filterType, data: $filterData}) {
                  id
                  name
                  runInfo {
                    sourceType
                    sourceUri 
                    triggerType
                    triggerData
                    runTime
                  }
                  decorations {
                    ...decorationContent
                  }
                  creation {
                    time
                  }
                  promotionRuns(lastPerLevel: true) {
                    creation {
                      time
                    }
                    promotionLevel {
                      id
                      name
                      image
                      _image
                    }
                  }
                  validations {
                    validationStamp {
                      id
                      name
                      dataType {
                        descriptor {
                          id
                        }
                        config
                      }
                    }
                    validationRuns(count: 1) {
                      validationRunStatuses {
                        statusID {
                          id
                          name
                        }
                        description
                        creation {
                          user
                        }
                      }
                    }
                  }
                  links {
                    _validate
                  }
                }
              }
            }
            
            fragment decorationContent on Decoration {
              decorationType
              error
              data
              feature {
                id
              }
            }`, {
                    branchId: $scope.branch.id,
                    filterType: filterType,
                    filterData: filterData
                }
            ).then(function (data) {
                $scope.branchView = data.branches[0];
                $scope.builds = data.branches[0].builds;
                // Management of promotion levels
                $scope.promotionLevels = data.branches[0].promotionLevels;
                $scope.promotionLevelSortOptions = {
                    disabled: !$scope.branchView.links._reorderPromotionLevels,
                    stop: function () {
                        const ids = $scope.promotionLevels.map(function (pl) {
                            return pl.id;
                        });
                        ot.call($http.put(
                            $scope.branchView.links._reorderPromotionLevels,
                            {ids: ids}
                        ));
                    }
                };
                // Management of validation stamps
                $scope.validationStamps = data.branches[0].validationStamps;
                $scope.validationStampSortOptions = {
                    disabled: !$scope.branchView.links._reorderValidationStamps,
                    stop: function () {
                        const ids = $scope.validationStamps.map(function (vs) {
                            return vs.id;
                        });
                        ot.call($http.put(
                            $scope.branchView.links._reorderValidationStamps,
                            {ids: ids}
                        ));
                    }
                };
                // Other branches
                if (!switchBranchesLoaded) {
                    switchBranchesLoaded = true;
                    view.commands.push({
                        id: 'switch-branch',
                        name: "Switch",
                        cls: 'ot-command-switch',
                        group: true,
                        actions: data.branches[0].otherBranches
                            .filter(function (theBranch) {
                                return !theBranch.disabled;
                            })
                            .map(function (theBranch) {
                                return {
                                    id: 'switch-' + theBranch.id,
                                    name: theBranch.name,
                                    uri: 'branch/' + theBranch.id
                                };
                            })
                    });
                }
            }).finally(function () {
                $scope.loadingBuildView = false;
            });
        }

// Loading the build view
        function loadBuildView() {
            // Parameters for the call
            let filterType = null;
            let filterData = null;
            // Adds the filter parameters
            let currentBuildFilterResource = otBuildFilterService.getCurrentFilter(branchId);
            if (currentBuildFilterResource) {
                filterType = currentBuildFilterResource.type;
                if (currentBuildFilterResource.data) {
                    filterData = JSON.stringify(currentBuildFilterResource.data);
                }
                $scope.currentBuildFilterResource = currentBuildFilterResource;
            } else {
                $scope.currentBuildFilterResource = undefined;
                $scope.invalidBuildFilterResource = undefined;
                $scope.invalidBuildFilterMessage = undefined;
            }
            // Checking the filter before using it
            if (filterType) {
                otGraphqlService.pageGraphQLCall(`
                    query BuildFilterValidation($branchId: Int!, $filterType: String!, $filterData: String!) {
                        buildFilterValidation(branchId: $branchId,filter: {type: $filterType, data: $filterData}) {
                            error
                        }
                    }
                `, {
                    branchId: $scope.branch.id,
                    filterType: filterType,
                    filterData: filterData
                }).then(function (data) {
                    const message = data.buildFilterValidation.error;
                    if (message) {
                        if ($scope.currentBuildFilterResource) {
                            // Displays a message to allow the deletion of this filter (if allowed)
                            $scope.invalidBuildFilterResource = $scope.currentBuildFilterResource;
                            $scope.invalidBuildFilterMessage = message;
                        }
                        // Removes current filter
                        otBuildFilterService.eraseCurrent($scope.branch.id);
                        // Calling with the default filter
                        callBuildView(undefined, undefined);
                    } else {
                        // No validation issue, calling the view call
                        callBuildView(filterType, filterData);
                    }
                });
            } else {
                // Direct actual branch view call
                callBuildView(filterType, filterData);
            }
        }

        // Loading the branch
        function loadBranch() {
            otStructureService.getBranch(branchId).then(function (branchResource) {
                $scope.branch = branchResource;
                // View settings
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
                    {
                        condition: function () {
                            return branchResource._disable;
                        },
                        id: 'disableBranch',
                        name: "Disable branch",
                        cls: 'ot-command-branch-disable',
                        action: function () {
                            ot.pageCall($http.put(branchResource._disable)).then(loadBranch);
                        }
                    },
                    {
                        condition: function () {
                            return branchResource._enable;
                        },
                        id: 'enableBranch',
                        name: "Enable branch",
                        cls: 'ot-command-branch-enable',
                        action: function () {
                            ot.pageCall($http.put(branchResource._enable)).then(loadBranch);
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
                            otStructureService.deleteBranch($scope.branch).then(function () {
                                $state.go('project', {projectId: $scope.branch.project.id});
                            }).then(function () {
                                $state.go('project', {projectId: $scope.branch.project.id});
                            });
                        }
                    },
                    ot.viewApiCommand(branchResource._self),
                    ot.viewCloseCommand('/project/' + branchResource.project.id),
                    ot.viewActionsCommand(
                        $scope.branch._actions,
                        getTools($scope.branch)
                    )
                ];
                // Loads the build filters
                loadBuildFilters();
                // Loads the validation stamp filters
                loadBranchValidationStampFilters();
            });
        }

        // Initialization
        loadPermalink();
        loadBranch();

        // Reload callback available in the scope
        $scope.reloadBranch = loadBranch;

        // Gets the list of tools for a branch
        function getTools(branch) {
            const tools = [];
            // Clone into branch
            if (branch._clone) {
                tools.push({
                    id: 'branch-clone',
                    name: "Clone branch",
                    action: cloneBranch
                });
            }
            // Copy from branch
            if (branch._copy) { 
                tools.push({
                    id: 'branch-copy',
                    name: "Copy config. from branch",
                    action: copyFromBranch
                });
            }
            // Bulk update
            if (branch._bulkUpdate) {
                tools.push({
                    id: 'branch-bulk-update',
                    name: "Bulk update",
                    action: bulkUpdateBranch
                });
            }
            // OK
            return tools;
        }

        // Bulk update of a branch
        function bulkUpdateBranch() {
            otCopyService.bulkUpdate($scope.branch).then(loadBranch);
        }

        // Cloning a branch
        function cloneBranch() {
            otCopyService.cloneBranch($scope.branch).then(function (newBranch) {
                $state.go('branch', {
                    branchId: newBranch.id
                });
            });
        }

        // Copy from a branch
        function copyFromBranch() {
            otCopyService.copyFrom($scope.branch).then(loadBranch);
        }

        // Gets the tooltip for a build filter
        $scope.getBuildFilterTooltip = (buildFilterResource) => {
            if (buildFilterResource.error) {
                if (buildFilterResource._delete) {
                    return `Invalid build filter (you should delete it): ${buildFilterResource.error}`;
                } else {
                    return `Invalid build filter: ${buildFilterResource.error}`;
                }
            } else {
                return "";
            }
        };

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
        $scope.createValidationRun = function (build, validationStamp) {
            otStructureService.create(
                build.links._validate,
                'Validation for the build',
                {
                    postForm: function (form) {
                        return otFormService.updateFieldValue(
                            form,
                            'validationStampData',
                            {
                                id: validationStamp.name,
                                data: validationStamp.dataType ? validationStamp.dataType.config : undefined
                            }
                        );
                    }
                }
            ).then(loadBuildView);
        };

        /**
         * Displaying the promotion runs
         */
        $scope.displayPromotionRuns = function (build, promotionRun) {
            $modal.open({
                templateUrl: 'app/dialog/dialog.promotionRuns.tpl.html',
                controller: 'otDialogPromotionRuns',
                resolve: {
                    config: function () {
                        return {
                            build: build,
                            promotionLevel: promotionRun.promotionLevel
                        };
                    }
                }
            }).result.then(loadBuildView, loadBuildView);
        };

        /**
         * Displaying the validation runs
         */
        $scope.displayValidationRuns = function (build, validationStamp) {
            $modal.open({
                templateUrl: 'app/dialog/dialog.validationStampRunView.tpl.html',
                controller: 'otDialogValidationStampRunView',
                resolve: {
                    config: function () {
                        return {
                            build: build,
                            validationStamp: validationStamp,
                            callbackOnStatusChange: function () {
                                loadBranch();
                            }
                        };
                    }
                }
            });
        };

        /**
         * Build diff action
         */
        $scope.buildDiff = function (action) {
            if ($scope.selectedBuilds.first && $scope.selectedBuilds.second) {
                $state.go(action.id, {
                    branch: branchId,
                    from: $scope.selectedBuilds.first.id,
                    to: $scope.selectedBuilds.second.id
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
            $scope.invalidBuildFilterResource = undefined;
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
         * Sharing a saved filter
         */
        $scope.buildFilterShare = function (buildFilterResource) {
            otBuildFilterService.shareFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
        };

        /**
         * Permalink to the current filter
         */
        $scope.buildFilterLink = function () {
            const currentFilter = otBuildFilterService.getCurrentFilter(branchId);
            if (currentFilter) {
                const jsonFilter = JSON.stringify(currentFilter);
                $location.hash(jsonFilter);
            }
        };

        /**
         * Loading the permalink at startup
         */
        function loadPermalink() {
            const jsonFilter = $location.hash();
            if (jsonFilter) {
                // Parsing the JSON
                try {
                    const json = JSON.parse(jsonFilter);
                    // Applies the filter
                    otBuildFilterService.storeCurrent(branchId, json);
                    // Removes the hash after use
                    $location.hash('');
                } catch (e) {
                    otNotificationService.error("Cannot get the filter from the permalink.");
                }
            }
        }

        /*
         * Branch validation stamp filters
         */

        function loadBranchValidationStampFilters() {
            ot.pageCall($http.get($scope.branch._allValidationStampFilters)).then(function (resources) {
                $scope.branchValidationStampFilterResources = resources;
                // Gets the validation stamp filter in the URL
                const search = $location.search();
                const vsFilterName = search.vsFilter || localStorage.getItem(`validationStampFilter_${$scope.branch.id}`);
                if (vsFilterName) {
                    // Gets the filter with same name
                    const existingFilter = $scope.branchValidationStampFilterResources.resources.find(function (vsf) {
                        //noinspection EqualityComparisonWithCoercionJS
                        return vsf.name === vsFilterName;
                    });
                    if (existingFilter) {
                        $scope.selectBranchValidationStampFilter(existingFilter);
                    }
                }
            });
        }

        $scope.selectBranchValidationStampFilter = function (validationStampFilter) {
            $scope.validationStampFilter = validationStampFilter;
            // Permalink
            const search = $location.search();
            if (validationStampFilter) {
                search.vsFilter = validationStampFilter.name;
                localStorage.setItem(`validationStampFilter_${$scope.branch.id}`, validationStampFilter.name);
            } else {
                delete search.vsFilter;
                localStorage.removeItem(`validationStampFilter_${$scope.branch.id}`);
            }
            $location.search(search);
        };

        $scope.clearBranchValidationStampFilter = function () {
            $scope.validationStampFilterEdition = false;
            $scope.selectBranchValidationStampFilter(undefined);
        };

        $scope.newBranchValidationStampFilter = function () {
            if ($scope.branch._validationStampFilterCreate) {
                $scope.validationStampFilterEdition = false;
                otFormService.create($scope.branch._validationStampFilterCreate, "Validation stamp filter").then(function (filter) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(filter);
                    // Enter in edition mode immediately
                    $scope.validationStampFilterEdition = true;
                });
            }
        };

        $scope.validationStampFilterFn = function (validationStamp) {
            return !$scope.validationStampFilter || $scope.validationStampFilterEdition || $scope.validationStampFilter.vsNames.indexOf(validationStamp.name) >= 0;
        };

        $scope.validationStampRunViewFilter = function (validation) {
            return $scope.validationStampFilterFn(validation.validationStamp);
        };

        $scope.validationStampFilterCount = function (plus) {
            if ($scope.validationStamps) {
                return plus + $scope.validationStamps.filter($scope.validationStampFilterFn).length;
            } else {
                return plus;
            }
        };

        $scope.editBranchValidationStampFilter = function (validationStampFilter) {
            if (validationStampFilter._update) {
                $scope.validationStampFilterEdition = false;
                otFormService.update(validationStampFilter._update, "Validation stamp filter").then(function (vsf) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(vsf);
                });
            }
        };

        $scope.shareValidationStampFilterAtProject = function (validationStampFilter) {
            if (validationStampFilter._shareAtProject) {
                $scope.validationStampFilterEdition = false;
                ot.pageCall($http.put(validationStampFilter._shareAtProject, {})).then(function (vsf) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(vsf);
                });
            }
        };

        $scope.shareValidationStampFilterAtGlobal = function (validationStampFilter) {
            if (validationStampFilter._shareAtGlobal) {
                $scope.validationStampFilterEdition = false;
                ot.pageCall($http.put(validationStampFilter._shareAtGlobal, {})).then(function (vsf) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(vsf);
                });
            }
        };

        $scope.deleteBranchValidationStampFilter = function (validationStampFilter) {
            if (validationStampFilter._delete) {
                $scope.validationStampFilterEdition = false;
                otAlertService.confirm({
                    title: "Validation stamp filter deletion",
                    message: "Do you really want to delete the " + validationStampFilter.name + " validation stamp filter?"
                }).then(function () {
                    ot.pageCall($http.delete(validationStampFilter._delete)).then(function () {
                        loadBranchValidationStampFilters();
                        $scope.selectBranchValidationStampFilter(undefined);
                    });
                });
            }
        };

        $scope.validationStampFilterEdition = false;

        $scope.directEditValidationStampFilter = function (validationStampFilter) {
            $scope.selectBranchValidationStampFilter(validationStampFilter);
            $scope.validationStampFilterEdition = true;
        };

        $scope.stopDirectEditValidationStampFilter = function (validationStampFilter) {
            $scope.selectBranchValidationStampFilter(validationStampFilter);
            $scope.validationStampFilterEdition = false;
        };

        $scope.selectNoneValidationStampFilter = function (validationStampFilter) {
            if (validationStampFilter._update) {
                ot.pageCall($http.put(validationStampFilter._update, {
                    name: validationStampFilter.name,
                    vsNames: []
                })).then(function (vsf) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(vsf);
                });
            }
        };

        $scope.selectAllValidationStampFilter = function (validationStampFilter) {
            if (validationStampFilter._update) {
                ot.pageCall($http.put(validationStampFilter._update, {
                    name: validationStampFilter.name,
                    vsNames: $scope.validationStamps.map(function (vs) {
                        return vs.name;
                    })
                })).then(function (vsf) {
                    loadBranchValidationStampFilters();
                    $scope.selectBranchValidationStampFilter(vsf);
                });
            }
        };

        $scope.toggleValidationStampFromFilter = function (validationStampName) {
            if ($scope.validationStampFilter && $scope.validationStampFilter._update) {
                const index = $scope.validationStampFilter.vsNames.indexOf(validationStampName);
                if (index >= 0) {
                    $scope.removeValidationStampFromFilter($scope.validationStampFilter, validationStampName);
                } else {
                    $scope.addValidationStampFromFilter($scope.validationStampFilter, validationStampName);
                }
            }
        };

        $scope.removeValidationStampFromFilter = function (validationStampFilter, validationStampName) {
            if (validationStampFilter._update) {
                const index = validationStampFilter.vsNames.indexOf(validationStampName);
                if (index >= 0) {
                    const names = validationStampFilter.vsNames.slice(0); // Copy
                    names.splice(index, 1);
                    ot.pageCall($http.put(validationStampFilter._update, {
                        name: validationStampFilter.name,
                        vsNames: names
                    })).then(function (vsf) {
                        loadBranchValidationStampFilters();
                        $scope.selectBranchValidationStampFilter(vsf);
                    });
                }
            }
        };

        $scope.addValidationStampFromFilter = function (validationStampFilter, validationStampName) {
            if (validationStampFilter._update) {
                const index = validationStampFilter.vsNames.indexOf(validationStampName);
                if (index < 0) {
                    const names = validationStampFilter.vsNames.slice(0); // Copy
                    names.push(validationStampName);
                    ot.pageCall($http.put(validationStampFilter._update, {
                        name: validationStampFilter.name,
                        vsNames: names
                    })).then(function (vsf) {
                        loadBranchValidationStampFilters();
                        $scope.selectBranchValidationStampFilter(vsf);
                    });
                }
            }
        };

    })
;