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

                // Selected validation stamp filter
                $scope.validationStampFilter = undefined;

                // Initial preferences
                $scope.branchViewVsNames = $rootScope.user.preferences.branchViewVsNames;

                // Loading the filters
                const loadFilters = () => {
                    $scope.loadingFilters = true;
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

            }
        };
    })
;