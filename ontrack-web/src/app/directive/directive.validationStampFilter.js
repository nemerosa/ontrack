angular.module('ot.directive.validationStampFilter', [
    'ot.service.core',
    'ot.service.graphql',
])
    .directive('otValidationStampFilter', function (ot, otGraphqlService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationStampFilter.tpl.html',
            scope: {
                branchId: '=',
                setFilter: '&'
            },
            controller: ($scope) => {

                // Selected validation stamp filter
                $scope.validationStampFilter = undefined;

                // Loading the filters
                const loadBuildFilters = () => {
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

            }
        };
    })
;