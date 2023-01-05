angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.task',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.copy',
    'ot.dialog.validationStampRunView',
    'ot.dialog.validationStampRunGroup',
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
    .controller('BranchCtrl', function ($state, $scope, $stateParams, $http, $modal, $location, $rootScope,
                                        ot, otFormService, otStructureService, otAlertService, otTaskService, otNotificationService, otCopyService,
                                        otGraphqlService) {
        const view = ot.view();
        let viewInitialized = false;
        // Branch's id
        const branchId = $stateParams.branchId;

        // Loading indicators
        $scope.loadingBranch = true;
        $scope.loadingBuilds = false;

        // Query: loading the branch
        const gqlBranch = `
            query LoadBranch(
                $branchId: Int!,
            ) {
                validationRunStatusIDList {
                    id
                    name
                }
                branches(id: $branchId) {
                    id
                    name
                    description
                    annotatedDescription
                    project {
                        id
                        name
                    }
                    decorations {
                      ...decorationContent
                    }
                    links {
                        _createBuild
                        _actions
                        _update
                        _delete
                    }
                    buildDiffActions {
                        id
                        name
                        type
                        uri
                    }
                    otherBranches {
                        id
                        name
                        disabled
                    }
                    validationStamps {
                      id
                      name
                      description
                      image
                      _image
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
            }
        `;

        // Query: loading the builds
        const gqlBuilds = `
            query LoadBuilds(
                $branchId: Int!,
                $offset: Int!,
                $size: Int!,
                $filterType: String,
                $filterData: String,
            ) {
                branches(id: $branchId) {
                    buildsPaginated(
                        offset: $offset,
                        size: $size,
                        generic: {
                            type: $filterType,
                            data: $filterData
                        }
                    ) {
                        pageInfo {
                            totalSize
                            nextPage {
                                offset
                                size
                            }
                        }
                        pageItems {
                            id
                            name
                            creation {
                              time
                            }
                            decorations {
                              ...decorationContent
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
                              }
                              validationRuns(count: 1) {
                                validationRunStatuses(lastOnly: true) {
                                  statusID {
                                    id
                                    name
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
            }
            
            fragment decorationContent on Decoration {
                decorationType
                error
                data
                feature {
                    id
                }
            }
        `;

        // Query: validation stamp data type information
        const gqlValidationStampData = `
            query ValidationStampData(
                $id: Int!,
            ) {
              validationStamp(id: $id) {
                dataType {
                  config
                }
              }
            }
        `;

        // Loading the branch
        const loadBranch = () => {
            $scope.loadingBranch = true;
            otGraphqlService.pageGraphQLCall(gqlBranch, {branchId})
                .then(data => {
                    $scope.branch = data.branches[0];
                    $scope.validationRunStatusIDList = data.validationRunStatusIDList;
                    $scope.validationStamps = $scope.branch.validationStamps;
                    if (!viewInitialized) {
                        view.breadcrumbs = ot.projectBreadcrumbs($scope.branch.project);
                        view.commands = [
                            {
                                condition: function () {
                                    return $scope.branch.links._createBuild;
                                },
                                id: 'createBuild',
                                name: "Create build",
                                cls: 'ot-command-build-new',
                                action: () => {
                                    otStructureService.createBuild($scope.branch.links._createBuild).then(() => {
                                        loadBuilds(true);
                                    });
                                }
                            },
                            {
                                condition: function () {
                                    return $scope.branch.links._update;
                                },
                                id: 'updateBranch',
                                name: "Update branch",
                                cls: 'ot-command-branch-update',
                                action: function () {
                                    otStructureService.update(
                                        $scope.branch.links._update,
                                        "Update branch"
                                    ).then(loadBranch);
                                }
                            },
                            {
                                condition: function () {
                                    return $scope.branch.links._delete;
                                },
                                id: 'deleteBranch',
                                name: "Delete branch",
                                cls: 'ot-command-branch-delete',
                                action: () => {
                                    otStructureService.deleteBranch($scope.branch).then(() => {
                                        $state.go('project', {projectId: $scope.branch.project.id});
                                    });
                                }
                            },
                            ot.viewActionsCommand($scope.branch.links._actions, []),
                            {
                                id: 'switch-branch',
                                name: "Switch",
                                cls: 'ot-command-switch',
                                group: true,
                                actions: $scope.branch.otherBranches
                                    .filter(theBranch => !theBranch.disabled)
                                    .map(theBranch => ({
                                        id: 'switch-' + theBranch.id,
                                        name: theBranch.name,
                                        uri: 'branch/' + theBranch.id
                                    }))
                            },
                            ot.viewCloseCommand('/project/' + $scope.branch.project.id),
                        ];
                        viewInitialized = true;
                    }
                })
                .finally(() => {
                    $scope.loadingBranch = false;
                });
        };

        // Pagination status
        const pagination = {
            offset: 0,
            size: 10,
        };

        /**
         * Enriches the builds with their validation stamp group information
         */
        const computeGroupedValidations = (builds, validationRunStatusIDList) => {
            builds.forEach(build => {
                const statuses = {};
                build.validations.forEach(validation => {
                    if (validation.validationRuns.length > 0) {
                        const statusID = validation.validationRuns[0].validationRunStatuses[0].statusID;
                        const group = statuses[statusID.id];
                        if (!group) {
                            statuses[statusID.id] = {
                                count: 1,
                                validations: [
                                    validation
                                ]
                            };
                        } else {
                            group.count = group.count + 1;
                            group.validations.push(validation);
                        }
                    }
                });
                // Sorting
                build.groupedValidations = [];
                validationRunStatusIDList.forEach(statusID => {
                    const group = statuses[statusID.id];
                    if (group) {
                        build.groupedValidations.push({
                            statusID: statusID,
                            description: `Validations with status ${statusID.name}`,
                            count: group.count,
                            validations: group.validations
                        });
                    }
                });
            });
        };

        /**
         * Loading the list of builds
         * @param reset True if the list of builds must be reset
         */
        const loadBuilds = (reset) => {
            $scope.loadingBuilds = true;
            const gqlVariables = {
                branchId,
                offset: pagination.offset,
                size: pagination.size,
                filterType: currentBuildFilter.type,
                // GraphQL type for the filter data is expected to be a string
                filterData: JSON.stringify(currentBuildFilter.data)
            };
            otGraphqlService.pageGraphQLCall(gqlBuilds, gqlVariables)
                .then(data => {
                    const dataBranch = data.branches[0];
                    const dataBuilds = dataBranch.buildsPaginated;
                    $scope.buildsPageInfo = dataBuilds.pageInfo;
                    let builds = dataBuilds.pageItems;
                    // Groups of validation stamps per status
                    if ($rootScope.user.preferences.branchViewVsGroups) {
                        computeGroupedValidations(builds, $scope.validationRunStatusIDList);
                    }
                    // Completing or resetting the list of builds
                    if (reset) {
                        $scope.builds = builds;
                    } else {
                        $scope.builds.push(...builds);
                    }
                })
                .finally(() => {
                    $scope.loadingBuilds = false;
                });
        };

        // Starts by loading the branch
        loadBranch();

        // Pagination: loading more builds
        $scope.loadMoreBuilds = () => {
            if ($scope.buildsPageInfo.nextPage) {
                pagination.offset = $scope.buildsPageInfo.nextPage.offset;
                pagination.size = $scope.buildsPageInfo.nextPage.size;
                loadBuilds(false);
            }
        };

        // Auto refresh management
        $scope.autoRefresh = localStorage.getItem('autoRefresh') === 'true';
        const refreshTaskName = 'Branch builds reloading';
        const refreshBuildView = () => {
            // Resetting the pagination
            pagination.offset = 0;
            pagination.size = 10;
            // Reloading
            loadBuilds(true);
        };
        $scope.$watch('autoRefresh', () => {
            if ($scope.autoRefresh) {
                // 1 minute interval
                otTaskService.register(refreshTaskName, refreshBuildView, 60 * 1000);
            } else {
                otTaskService.stop(refreshTaskName);
            }
        });
        $scope.toggleAutoRefresh = () => {
            $scope.autoRefresh = !$scope.autoRefresh;
            localStorage.setItem('autoRefresh', $scope.autoRefresh);
        };

        // =================================================
        // Validation runs
        // =================================================

        /**
         * Creating a validation run
         */
        $scope.createValidationRun = (build, validationStamp) => {
            // Loads the validation stamp data config
            otGraphqlService.pageGraphQLCall(gqlValidationStampData, {id: validationStamp.id})
                .then(data => {
                    return otStructureService.create(
                        build.links._validate,
                        'Validation for the build',
                        {
                            postForm: function (form) {
                                return otFormService.updateFieldValue(
                                    form,
                                    'validationStampData',
                                    {
                                        id: validationStamp.name,
                                        data: data.validationStamp.dataType ? data.validationStamp.dataType.config : undefined
                                    }
                                );
                            }
                        }
                    );
                })
                .then(() => {
                    loadBuilds(true);
                });
        };

        /**
         * Displaying the validation runs
         */
        $scope.displayValidationRuns = (build, validationStamp) => {
            $modal.open({
                templateUrl: 'app/dialog/dialog.validationStampRunView.tpl.html',
                controller: 'otDialogValidationStampRunView',
                resolve: {
                    config: () => ({
                        build: build,
                        validationStamp: validationStamp,
                        callbackOnStatusChange: () => {
                            loadBuilds(true);
                        }
                    })
                }
            });
        };

        /**
         * Displaying a list of validation runs grouped by status
         */
        $scope.displayValidationRunsGroup = (build, group) => {
            $modal.open({
                templateUrl: 'app/dialog/dialog.validationStampRunGroup.tpl.html',
                controller: 'otDialogValidationStampRunGroup',
                resolve: {
                    config: () => ({
                        build: build,
                        group: group,
                        callbackOnRunOpen: (validationStamp) => {
                            $scope.displayValidationRuns(build, validationStamp);
                        }
                    })
                }
            });
        };

        // =================================================
        // Management of selected builds
        // =================================================

        // Selected builds
        $scope.selectedBuilds = {};

        /**
         * Build diff action
         */
        $scope.buildDiff = action => {
            if ($scope.selectedBuilds.first && $scope.selectedBuilds.second) {
                $state.go(action.id, {
                    branch: branchId,
                    from: $scope.selectedBuilds.first.id,
                    to: $scope.selectedBuilds.second.id
                });
            }
        };

        // =================================================
        // Management of the build filters
        // =================================================

        // Current build filter
        let currentBuildFilter = {
            type: undefined,
            data: undefined
        };

        // Callback from the build filter component
        $scope.setBuildFilter = () => (filter) => {
            currentBuildFilter = filter;
            // console.log("currentBuildFilter", currentBuildFilter);
            refreshBuildView();
        };

        // =================================================
        // Validation stamps filters
        // =================================================

        /**
         * Current selected filter
         */
        $scope.validationStampFilter = undefined;
        $scope.validationStampFilterEdition = {
            enabled: false,
            changing: false,
            vsNames: []
        };

        /**
         * Reloading the view for the validation stamp filters
         */
        $scope.reloadForValidationStampFilter = () => (filter, noReload) => {
            if (angular.equals($scope.validationStampFilter, filter)) {
                // Filter did not change
                if (!noReload) {
                    loadBuilds(true);
                }
            } else {
                // Just changing the filter is enough
                $scope.validationStampFilter = filter;
            }
        };

        /**
         * Computes the height of the row for the validation stamps
         */
        $scope.validationStampFilterNameMaxHeight = () => {
            if ($rootScope.user.preferences.branchViewVsNames && $scope.validationStamps) {
                const nameLengths = $scope.validationStamps.map(vs => $scope.validationStampFilterNameElapsed(vs.name).length);
                const maxLength = Math.max(...nameLengths);
                return `${Math.floor(maxLength / 1.4142)}em`;
            } else {
                return "36px";
            }
        };

        /**
         * Gets the length of a validation stamp name
         */
        $scope.validationStampFilterNameElapsed = (name) => {
            const maxLength = 20;
            if (name.length <= maxLength) {
                return name;
            } else {
                return name.substring(0, maxLength) + '…';
            }
        };

        $scope.validationStampFilterCount = plus => {
            let placeForGroups = 0;
            if ($rootScope.user.preferences.branchViewVsGroups) {
                placeForGroups = 1;
            }
            if ($scope.validationStamps) {
                return placeForGroups + plus + $scope.validationStamps.filter($scope.validationStampFilterFn).length;
            } else {
                return placeForGroups + plus;
            }
        };

        /**
         * Checks if a given validation stamp must be displayed or not.
         *
         * A validation stamp is displayed if:
         *
         * * there is a selected validation stamp filter (VSF), then
         *   * the VSF is being edited
         *   * OR the VSF contains the validation stamp
         * * there is no selected VSF, then
         *   * NOT if groups are displayed
         * @param validationStamp Validation stamp to check
         * @returns {boolean|boolean|*} `true` if the validation must be displayed.
         */
        $scope.validationStampFilterFn = function (validationStamp) {
            if ($scope.validationStampFilter) {
                return $scope.validationStampFilterEdition.enabled || $scope.validationStampFilter.vsNames.indexOf(validationStamp.name) >= 0;
            } else {
               return !$rootScope.user.preferences.branchViewVsGroups;
            }
        };

        $scope.validationStampRunViewFilter = function (validation) {
            return $scope.validationStampFilterFn(validation.validationStamp);
        };

        $scope.selectAllValidationStampFilter = () => {
            if ($scope.validationStampFilterEdition.enabled && $scope.validationStampFilter && $scope.validationStampFilter.links._update) {
                $scope.validationStampFilterEdition.vsNames.splice(0, $scope.validationStampFilterEdition.vsNames.length);
                const allNames = $scope.validationStamps.map(vs => vs.name);
                $scope.validationStampFilterEdition.vsNames.push(...allNames);
                $scope.validationStampFilterEdition.changing = true;
            }
        };

        $scope.selectNoneValidationStampFilter = () => {
            if ($scope.validationStampFilterEdition.enabled && $scope.validationStampFilter && $scope.validationStampFilter.links._update) {
                $scope.validationStampFilterEdition.vsNames.splice(0, $scope.validationStampFilterEdition.vsNames.length);
                $scope.validationStampFilterEdition.changing = true;
            }
        };

        $scope.toggleValidationStampFromFilter = (name) => {
            if ($scope.validationStampFilterEdition.enabled && $scope.validationStampFilter && $scope.validationStampFilter.links._update) {
                const index = $scope.validationStampFilterEdition.vsNames.indexOf(name);
                $scope.validationStampFilterEdition.changing = true;
                if (index >= 0) {
                    $scope.validationStampFilterEdition.vsNames.splice(index, 1);
                } else {
                    $scope.validationStampFilterEdition.vsNames.push(name);
                }
            }
        };

        $scope.stopDirectEditValidationStampFilter = () => {
            $scope.validationStampFilterEdition.enabled = false;
        };
    })
;