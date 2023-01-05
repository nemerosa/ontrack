angular.module('ot.view.promotionLevels', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('promotionLevels', {
            url: '/branch/{branchId}/promotionLevels',
            templateUrl: 'app/view/view.promotionLevels.tpl.html',
            controller: 'PromotionLevelsCtrl'
        });
    })
    .controller('PromotionLevelsCtrl', function ($http, $scope, $stateParams, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "Promotion levels";
        let viewInitialized = false;
        // Branch's id
        const branchId = Number($stateParams.branchId);

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
                        _reorderPromotionLevels
                    }
                    promotionLevels {
                        id
                        name
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
            otGraphqlService.pageGraphQLCall(gqlPromotionLevels, {branchId})
                .then(data => {
                    $scope.branch = data.branches[0];
                    $scope.promotionLevels = $scope.branch.promotionLevels;

                    if (!viewInitialized) {
                        view.breadcrumbs = ot.branchBreadcrumbs($scope.branch);
                        view.commands = [
                            ot.viewCloseCommand('/branch/' + $scope.branch.id),
                        ];
                        viewInitialized = true;
                    }

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
                })
                .finally(() => {
                    $scope.loadingPromotionLevels = false;
                });
        };

        // Loading the promotion levels first
        loadPromotionLevels();
    })
;