angular.module('ot.directive.validationStampFilter', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.user'
])
    .directive('otValidationStampFilter', function ($location, $rootScope, ot, otGraphqlService, otUserService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationStampFilter.tpl.html',
            scope: {
                branchId: '=',
                reload: '&'
            },
            controller: ($scope) => {

                // Query: list of validation stamp filters
                const gqlValidationStampFilters = `
                    query ValidationStampFilters(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            validationStampFilters(all: true) {
                                id
                                name
                                vsNames
                                project { id } 
                                branch { id }
                            }
                        }
                    }
                `;

                // Selected validation stamp filter
                $scope.validationStampFilter = undefined;

                // Initial preferences
                $scope.branchViewVsNames = $rootScope.user.preferences.branchViewVsNames;
                $scope.branchViewVsGroups = $rootScope.user.preferences.branchViewVsGroups;

                // Loading the filters
                const loadFilters = () => {
                    $scope.loadingFilters = true;
                    otGraphqlService.pageGraphQLCall(gqlValidationStampFilters, {
                        branchId: $scope.branchId
                    }).then(data => {
                        $scope.validationStampFilters = data.branches[0].validationStampFilters;
                        loadCurrentFilter();
                    }).finally(() => {
                        $scope.loadingFilters = false;
                    });
                };

                // Calling back the branch page
                const reload = () => {
                    $scope.reload()($scope.validationStampFilter);
                };

                // Loading the filters when branch ID is ready
                $scope.$watch('branchId', (value) => {
                    if (value) {
                        loadFilters();
                    }
                });

                // Loading the initial validation stamp filter
                const loadCurrentFilter = () => {
                    // Gets the validation stamp filter in the URL
                    const search = $location.search();
                    const vsFilterName = search.vsFilter || localStorage.getItem(`validationStampFilter_${$scope.branchId}`);
                    if (vsFilterName) {
                        // Gets the filter with same name
                        const existingFilter = $scope.validationStampFilters.find(vsf => {
                            //noinspection EqualityComparisonWithCoercionJS
                            return vsf.name === vsFilterName;
                        });
                        if (existingFilter) {
                            $scope.selectBranchValidationStampFilter(existingFilter);
                        }
                    }
                };

                // Selection of a filter
                $scope.selectBranchValidationStampFilter = validationStampFilter => {
                    $scope.validationStampFilter = validationStampFilter;
                    // Permalink
                    const search = $location.search();
                    if (validationStampFilter) {
                        search.vsFilter = validationStampFilter.name;
                        localStorage.setItem(`validationStampFilter_${$scope.branchId}`, validationStampFilter.name);
                    } else {
                        delete search.vsFilter;
                        localStorage.removeItem(`validationStampFilter_${$scope.branchId}`);
                    }
                    $location.search(search);
                    // Setting the VS filter
                    reload();
                };

                // Clears the selection
                $scope.clearBranchValidationStampFilter = () => {
                    // TODO $scope.validationStampFilterEdition = false;
                    $scope.selectBranchValidationStampFilter(undefined);
                };

                // Toggles the preferences for the VS names
                $scope.toggleBranchViewVsNames = () => {
                    $scope.branchViewVsNames = !$scope.branchViewVsNames;
                    otUserService.setPreferences({
                        branchViewVsNames: $scope.branchViewVsNames
                    });
                    reload();
                };

                // Toggles the preferences for the grouping per status
                $scope.toggleBranchViewVsGroups = () => {
                    $scope.branchViewVsGroups = !$scope.branchViewVsGroups;
                    otUserService.setPreferences({
                        branchViewVsGroups: $scope.branchViewVsGroups
                    });
                    reload();
                };

            }
        };
    })
;