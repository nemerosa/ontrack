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
                            }
                        }
                    }
                `;

                const setCurrentBuildFilter = (filter) => {
                    // console.log("setFilter", filter);
                    $scope.setFilter()(filter);
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
                            filter.data = JSON.stringify(currentBuildFilterResource.data);
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
                            filterData: filter.data
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
                                setCurrentBuildFilter({type: undefined, data: undefined});
                            } else {
                                // No validation issue, calling the view call
                                setCurrentBuildFilter(filter);
                            }
                        });
                    } else {
                        // Direct actual branch view call
                        setCurrentBuildFilter({type: undefined, data: undefined});
                    }
                };

                const loadBuildFilters = () => {
                    $scope.loadingFilters = true;
                    otGraphqlService
                        .pageGraphQLCall(gqlBranchFilters, {branchId: $scope.branchId})
                        .then(data => {
                            const branch = data.branches[0];
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
                        $scope.currentBuildFilterResource = filter;
                        setCurrentBuildFilter(filter);
                    });
                };
            }
        };
    })
;