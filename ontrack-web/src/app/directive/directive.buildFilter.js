angular.module('ot.directive.buildFilter', [
    'ot.service.buildfilter',
    'ot.service.core',
    'ot.service.graphql',
])
    .directive('otBuildFilter', function ($http, ot, otBuildFilterService, otGraphqlService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.buildFilter.tpl.html',
            scope: {
                branchId: '=',
                // Callback method with filter type & filter data as arguments
                setFilter: '&'
            },
            controller: ($scope) => {

                const gqlBranchFilters = `
                    query BranchFilters(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            id
                            links {
                                _buildFilterSave
                                _buildFilterShare
                            }
                            buildFilterForms {
                                type
                                typeName
                                isPredefined
                                form
                            }
                            buildFilterResources {
                                isShared
                                name
                                type
                                data
                                error
                                links {
                                    _update
                                    _share
                                    _delete
                                }
                            }
                        }
                    }
                `;

                const setCurrentBuildFilter = (filter) => {
                    $scope.currentBuildFilterResource = filter;
                    $scope.setFilter()(filter);
                };
                const unsetCurrentBuildFilter = () => {
                    setCurrentBuildFilter({type: undefined, data: undefined});
                    $scope.currentBuildFilterResource = undefined;
                };

                const loadCurrentBuildFilter = () => {
                    // Local data
                    const filter = {
                        type: undefined,
                        data: undefined
                    };
                    // Gets the filter from the local storage
                    let currentBuildFilterResource = otBuildFilterService.getCurrentFilter($scope.branchId);
                    if (currentBuildFilterResource) {
                        filter.type = currentBuildFilterResource.type;
                        if (currentBuildFilterResource.data) {
                            if (typeof currentBuildFilterResource.data === 'string' || currentBuildFilterResource.data instanceof String) {
                                filter.data = JSON.parse(currentBuildFilterResource.data);
                            } else {
                                filter.data = currentBuildFilterResource.data;
                            }
                        }
                        $scope.currentBuildFilterResource = currentBuildFilterResource;
                    } else {
                        $scope.currentBuildFilterResource = undefined;
                        $scope.invalidBuildFilterResource = undefined;
                        $scope.invalidBuildFilterMessage = undefined;
                    }
                    // Checking the filter before using it
                    if (filter.type) {
                        otGraphqlService.pageGraphQLCall(`
                            query BuildFilterValidation($branchId: Int!, $filterType: String!, $filterData: String!) {
                                buildFilterValidation(branchId: $branchId,filter: {type: $filterType, data: $filterData}) {
                                    error
                                }
                            }
                        `, {
                            branchId: $scope.branchId,
                            filterType: filter.type,
                            filterData: JSON.stringify(filter.data)
                        }).then(data => {
                            const message = data.buildFilterValidation.error;
                            if (message) {
                                if ($scope.currentBuildFilterResource) {
                                    // Displays a message to allow the deletion of this filter (if allowed)
                                    $scope.invalidBuildFilterResource = $scope.currentBuildFilterResource;
                                    $scope.invalidBuildFilterMessage = message;
                                }
                                // Removes current filter
                                otBuildFilterService.eraseCurrent($scope.branchId);
                                // Setting the default filter
                                unsetCurrentBuildFilter();
                            } else {
                                // No validation issue, calling the view call
                                setCurrentBuildFilter($scope.currentBuildFilterResource);
                            }
                        });
                    } else {
                        // Direct actual branch view call
                        unsetCurrentBuildFilter();
                    }
                };

                const loadBuildFilters = () => {
                    $scope.loadingFilters = true;
                    otGraphqlService
                        .pageGraphQLCall(gqlBranchFilters, {branchId: $scope.branchId})
                        .then(data => {
                            const branch = data.branches[0];
                            $scope.branch = branch;
                            $scope.buildFilterForms = branch.buildFilterForms;
                            $scope.buildFilterResources = otBuildFilterService.mergeRemoteAndLocalFilters(
                                $scope.branchId,
                                branch.buildFilterResources
                            );
                            // Current selected filter
                            loadCurrentBuildFilter();
                        })
                        .finally(() => {
                            $scope.loadingFilters = false;
                        });
                };

                $scope.$watch('branchId', (value) => {
                    if (value) {
                        loadBuildFilters();
                    }
                });

                /**
                 * Build filter: new one
                 */
                $scope.buildFilterNew = buildFilterForm => {
                    otBuildFilterService.createBuildFilter({
                        branchId: $scope.branchId,
                        buildFilterForm: buildFilterForm
                    }).then(filter => {
                        // Reloads the filters (only if not a predefined filter)
                        if (!buildFilterForm.isPredefined) {
                            loadBuildFilters();
                        }
                        // Sets the build filter
                        setCurrentBuildFilter(filter);
                    });
                };

                /**
                 * Removing the current filter
                 */
                $scope.buildFilterErase = () => {
                    otBuildFilterService.eraseCurrent($scope.branchId);
                    $scope.invalidBuildFilterResource = undefined;
                    unsetCurrentBuildFilter();
                };

                /**
                 * Applying a filter
                 */
                $scope.buildFilterApply = buildFilterResource => {
                    if (!buildFilterResource.removing) {
                        otBuildFilterService.storeCurrent($scope.branchId, buildFilterResource);
                        setCurrentBuildFilter(buildFilterResource);
                    }
                };

                /**
                 * Saving a local filter
                 */
                $scope.buildFilterSave = buildFilterResource => {
                    otBuildFilterService.saveFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
                };

                /**
                 * Sharing a saved filter
                 */
                $scope.buildFilterShare = buildFilterResource => {
                    otBuildFilterService.shareFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
                };

                /**
                 * Removing an existing filter
                 */
                $scope.buildFilterRemove = buildFilterResource => {
                    buildFilterResource.removing = true;
                    otBuildFilterService.removeFilter($scope.branch, buildFilterResource).then(loadBuildFilters);
                };

                /**
                 * Editing a filter
                 */
                $scope.buildFilterEdit = buildFilterResource => {
                    otBuildFilterService.editBuildFilter({
                        branch: $scope.branch,
                        buildFilterResource: buildFilterResource,
                        buildFilterForms: $scope.buildFilterForms
                    }).then(() => {
                        // Reloads the filters
                        loadBuildFilters();
                    });
                };
            }
        };
    })
;