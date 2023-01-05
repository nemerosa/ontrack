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
    .controller('PromotionLevelsCtrl', function ($scope, $stateParams, ot, otGraphqlService) {
        const view = ot.view();
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
                })
                .finally(() => {
                    $scope.loadingPromotionLevels = false;
                });
        };

        // Loading the promotion levels first
        loadPromotionLevels();
    })
;