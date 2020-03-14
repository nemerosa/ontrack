angular.module('ot.dialog.promotionRuns', [
    'ot.service.core',
    'ot.service.graphql'
])
    .controller('otDialogPromotionRuns', function ($scope, $modalInstance, $http, config, ot, otAlertService, otGraphqlService) {
        // General configuration
        $scope.config = config;
        // GraphQL query
        const query = `query PromotionRuns($build: Int!, $promotionLevel: String!) {
            builds(id: $build) {
                promotionRuns(promotion: $promotionLevel) {
                    id
                    description
                    annotatedDescription
                    creation {
                        user
                        time
                    }
                    promotionLevel {
                        id
                        name
                        description
                        image
                        _image
                    }
                    links {
                        _delete
                    }
                }
            }
        }`;
        // Variables
        const queryParams = {
            build: config.build.id,
            promotionLevel: config.promotionLevel.name
        };
        // Loading all the promotion runs
        function loadRuns() {
            otGraphqlService.pageGraphQLCall(query, queryParams).then((data) => {
                $scope.promotionRuns = data.builds[0].promotionRuns;
            });
        }

        loadRuns();
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Deleting a promotion run
        $scope.deletePromotionRun = function (promotionRun) {
            otAlertService.confirm({
                title: "Promotion deletion",
                message: "Do you really want to delete this promotion?"
            }).then(function () {
                return ot.call($http.delete(promotionRun.links._delete));
            }).then(loadRuns);
        };
    })
;