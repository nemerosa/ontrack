angular.module('ot.view.promotionLevel', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('promotionLevel', {
            url: '/promotionLevel/{promotionLevelId}',
            templateUrl: 'app/view/view.promotionLevel.tpl.html',
            controller: 'PromotionLevelCtrl'
        });
    })
    .controller('PromotionLevelCtrl', function ($scope, $stateParams, $http, ot, otStructureService) {
        var view = ot.view();
        // PromotionLevel's id
        var promotionLevelId = $stateParams.promotionLevelId;

        // Loading the promotion level
        function loadPromotionLevel() {
            ot.call($http.get('structure/promotionLevels/' + promotionLevelId)).then(function (promotionLevel) {
                $scope.promotionLevel = promotionLevel;
                // View title
                view.title = $scope.promotionLevel.name;
                // Commands
                view.commands = [
                    ot.viewCloseCommand($scope.promotionLevel.branch.href)
                ];
            });
        }

        // Initialisation
        loadPromotionLevel();

    })
;