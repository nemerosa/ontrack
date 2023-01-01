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