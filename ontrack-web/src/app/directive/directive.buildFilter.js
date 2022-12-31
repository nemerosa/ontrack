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
            },
            controller: ($scope) => {

                const gqlBranchFilters = `
                    query BranchFilters(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            links {
                                _buildFilterForms
                                _buildFilterResources
                            }
                        }
                    }
                `;

                const loadBuildFilters = () => {
                    $scope.loadingFilters = true;
                    let branch;
                    otGraphqlService
                        .pageGraphQLCall(gqlBranchFilters, {branchId: $scope.branchId})
                        .then(data => {
                            branch = data.branches[0];
                            const buildFilterFormsUri = branch.links._buildFilterForms;
                            return ot.call($http.get(buildFilterFormsUri));
                        })
                        .then(buildFilterForms => {
                            $scope.buildFilterForms = buildFilterForms;
                            // Loads existing filters
                            return otBuildFilterService.loadFilters({
                                id: $scope.branchId,
                                _buildFilterResources: branch.links._buildFilterResources,
                            });
                        })
                        .then(buildFilterResources => {
                            $scope.buildFilterResources = buildFilterResources;
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
            }
        };
    })
;