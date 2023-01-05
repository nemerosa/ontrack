angular.module('ot.directive.validationStampFilter', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql',
    'ot.service.user'
])
    .directive('otValidationStampFilter', function ($http, $location, $rootScope, ot, otAlertService, otFormService, otGraphqlService, otUserService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationStampFilter.tpl.html',
            scope: {
                branchId: '=',
                reload: '&',
                validationStampFilterEdition: '='
            },
            controller: ($scope) => {

                // Query: list of validation stamp filters
                const gqlValidationStampFilters = `
                    query ValidationStampFilters(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            project {
                                name
                            }
                            links {
                                _validationStampFilterCreate
                            }
                            validationStampFilters(all: true) {
                                id
                                name
                                vsNames
                                scope
                                links {
                                    _update
                                    _delete
                                    _shareAtProject
                                    _shareAtGlobal
                                }
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
                        $scope.branch = data.branches[0];
                        $scope.validationStampFilters = $scope.branch.validationStampFilters;
                        loadCurrentFilter();
                    }).finally(() => {
                        $scope.loadingFilters = false;
                    });
                };

                // Calling back the branch page
                const reload = (noReload) => {
                    $scope.reload()($scope.validationStampFilter, noReload);
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
                $scope.selectBranchValidationStampFilter = (validationStampFilter, noReload) => {
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
                    reload(noReload);
                };

                // Creating a new filter
                $scope.newBranchValidationStampFilter = () => {
                    if ($scope.branch.links._validationStampFilterCreate) {
                        $scope.validationStampFilterEdition.enabled = false;
                        otFormService.create($scope.branch.links._validationStampFilterCreate, "Validation stamp filter")
                            .then(filter => {
                                loadFilters();
                                // Enter in edition mode immediately
                                $scope.directEditValidationStampFilter(filter);
                            });
                    }
                };

                $scope.shareValidationStampFilterAtProject = validationStampFilter => {
                    if (validationStampFilter.links._shareAtProject) {
                        $scope.validationStampFilterEdition.enabled = false;
                        ot.pageCall($http.put(validationStampFilter.links._shareAtProject, {})).then(vsf => {
                            loadFilters();
                            $scope.selectBranchValidationStampFilter(vsf, true);
                        });
                    }
                };

                $scope.shareValidationStampFilterAtGlobal = validationStampFilter => {
                    if (validationStampFilter.links._shareAtGlobal) {
                        $scope.validationStampFilterEdition.enabled = false;
                        ot.pageCall($http.put(validationStampFilter.links._shareAtGlobal, {})).then(vsf => {
                            loadFilters();
                            $scope.selectBranchValidationStampFilter(vsf, true);
                        });
                    }
                };

                $scope.editBranchValidationStampFilter = validationStampFilter => {
                    if (validationStampFilter.links._update) {
                        $scope.validationStampFilterEdition.enabled = false;
                        otFormService.update(validationStampFilter.links._update, "Validation stamp filter").then(vsf => {
                            loadFilters();
                            $scope.selectBranchValidationStampFilter(vsf);
                        });
                    }
                };

                $scope.directEditValidationStampFilter = validationStampFilter => {
                    if (validationStampFilter.links._update) {
                        // Makes sure to select the filter without reloading the builds
                        $scope.selectBranchValidationStampFilter(validationStampFilter, true);
                        $scope.validationStampFilterEdition.vsNames = $scope.validationStampFilter.vsNames;
                        $scope.validationStampFilterEdition.enabled = true;
                        $scope.validationStampFilterEdition.changing = false;
                    }
                };

                $scope.stopDirectEditValidationStampFilter = validationStampFilter => {
                    // Makes sure to select the filter without reloading the builds
                    $scope.selectBranchValidationStampFilter(validationStampFilter, true);
                    $scope.validationStampFilterEdition.enabled = false;
                    $scope.validationStampFilterEdition.changing = false;
                };

                // Deleting an existing filter
                $scope.deleteBranchValidationStampFilter = validationStampFilter => {
                    if (validationStampFilter.links._delete) {
                        $scope.validationStampFilterEdition.enabled = false;
                        otAlertService.confirm({
                            title: "Validation stamp filter deletion",
                            message: `Do you really want to delete the ${validationStampFilter.name} validation stamp filter?`
                        }).then(() => {
                            ot.pageCall($http.delete(validationStampFilter.links._delete)).then(() => {
                                loadFilters();
                                $scope.selectBranchValidationStampFilter(undefined);
                            });
                        });
                    }
                };

                // Clears the selection
                $scope.clearBranchValidationStampFilter = () => {
                    $scope.validationStampFilterEdition.enabled = false;
                    $scope.selectBranchValidationStampFilter(undefined);
                };

                // Toggles the preferences for the VS names
                $scope.toggleBranchViewVsNames = () => {
                    $scope.branchViewVsNames = !$scope.branchViewVsNames;
                    otUserService.setPreferences({
                        branchViewVsNames: $scope.branchViewVsNames
                    });
                    reload(true);
                };

                // Toggles the preferences for the grouping per status
                $scope.toggleBranchViewVsGroups = () => {
                    $scope.branchViewVsGroups = !$scope.branchViewVsGroups;
                    otUserService.setPreferences({
                        branchViewVsGroups: $scope.branchViewVsGroups
                    });
                    reload(false);
                };

                // Updating the filter upon changes
                $scope.$watch('validationStampFilterEdition.vsNames', () => {
                    if ($scope.validationStampFilterEdition.enabled &&
                        $scope.validationStampFilter &&
                        $scope.validationStampFilterEdition.changing &&
                        $scope.validationStampFilter.links._update
                    ) {
                        ot.pageCall($http.put($scope.validationStampFilter.links._update, {
                            name: $scope.validationStampFilter.name,
                            vsNames: $scope.validationStampFilterEdition.vsNames
                        })).finally(() => {
                            $scope.validationStampFilterEdition.changing = false;
                            $scope.validationStampFilter.vsNames = $scope.validationStampFilterEdition.vsNames;
                        });
                    }
                }, true);

            }
        };
    })
;