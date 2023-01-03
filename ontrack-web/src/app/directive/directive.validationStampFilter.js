angular.module('ot.directive.validationStampFilter', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.user'
])
    .directive('otValidationStampFilter', function ($rootScope, ot, otGraphqlService, otUserService) {
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

                    }).finally(() => {
                        $scope.loadingFilters = false;
                    });
                };

                // Loading the filters when branch ID is ready
                $scope.$watch('branchId', (value) => {
                    if (value) {
                        loadFilters();
                        // Current selected filter
                        // TODO loadCurrentFilter();
                    }
                });

                // Toggles the preferences for the VS names
                $scope.toggleBranchViewVsNames = () => {
                    $scope.branchViewVsNames = !$scope.branchViewVsNames;
                    otUserService.setPreferences({
                        branchViewVsNames: $scope.branchViewVsNames
                    });
                    $scope.reload()($scope.validationStampFilter);
                };

                // Toggles the preferences for the grouping per status
                $scope.toggleBranchViewVsGroups = () => {
                    $scope.branchViewVsGroups = !$scope.branchViewVsGroups;
                    otUserService.setPreferences({
                        branchViewVsGroups: $scope.branchViewVsGroups
                    });
                    $scope.reload()($scope.validationStampFilter);
                };

            }
        };
    })
;