angular.module('ot.directive.promotionLevels', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.structure'
])
    .directive('otPromotionLevels', function ($http, ot, otGraphqlService, otStructureService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.promotionLevels.tpl.html',
            scope: {
                branchId: '='
            },
            controller: ($scope) => {

                // Loading indicator
                $scope.loadingPromotionLevels = true;

                // Query: promotion levels
                const gqlPromotionLevels = `
                    query PromotionLevels(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            id
                            name
                            project {
                                id
                                name
                            }
                            links {
                                _createPromotionLevel
                                _reorderPromotionLevels
                            }
                            promotionLevels {
                                id
                                name
                                description
                                image
                                _image
                                decorations {
                                    ...decorationContent
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

                // Loading the promotion levels
                const loadPromotionLevels = () => {
                    $scope.loadingPromotionLevels = true;
                    otGraphqlService.pageGraphQLCall(gqlPromotionLevels, {
                        branchId: $scope.branchId
                    }).then(data => {
                        $scope.branch = data.branches[0];
                        $scope.promotionLevels = $scope.branch.promotionLevels;

                        $scope.promotionLevelSortOptions = {
                            disabled: !$scope.branch.links._reorderPromotionLevels,
                            stop: () => {
                                const ids = $scope.promotionLevels.map(pl => pl.id);
                                ot.call($http.put(
                                    $scope.branch.links._reorderPromotionLevels,
                                    {ids: ids}
                                ));
                            }
                        };
                    }).finally(() => {
                        $scope.loadingPromotionLevels = false;
                    });
                };

                // Loading the promotion levels on branch being ready
                $scope.$watch('branchId', () => {
                    if ($scope.branchId) {
                        loadPromotionLevels();
                    }
                });

                // Creation of a new promotion level
                $scope.createPromotionLevel = () => {
                    otStructureService.create($scope.branch.links._createPromotionLevel, "New promotion level").then(loadPromotionLevels);
                };
            }
        };
    })
;