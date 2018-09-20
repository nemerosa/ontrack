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
    .controller('PromotionLevelCtrl', function ($state, $scope, $stateParams, $http, ot, otStructureService, otAlertService) {
        var view = ot.view();
        // PromotionLevel's id
        var promotionLevelId = $stateParams.promotionLevelId;

        // Loading the promotion level
        function loadPromotionLevel() {
            otStructureService.getPromotionLevel(promotionLevelId).then(function (promotionLevel) {
                $scope.promotionLevel = promotionLevel;
                // View breadcrumbs
                view.breadcrumbs = ot.branchBreadcrumbs(promotionLevel.branch);
                // Commands
                view.commands = [
                    {
                        condition: function () {
                            return promotionLevel._update;
                        },
                        id: 'updatePromotionLevelImage',
                        name: "Change image",
                        cls: 'ot-command-promotion-level-image',
                        action: changeImage
                    },
                    {
                        condition: function () {
                            return promotionLevel._update;
                        },
                        id: 'updatePromotionLevel',
                        name: "Update promotion level",
                        cls: 'ot-command-promotion-level-update',
                        action: function () {
                            otStructureService.update(
                                promotionLevel._update,
                                "Update promotion level"
                            ).then(loadPromotionLevel);
                        }
                    },
                    {
                        condition: function () {
                            return promotionLevel._delete;
                        },
                        id: 'deletePromotionLevel',
                        name: "Delete promotion level",
                        cls: 'ot-command-promotion-level-delete',
                        action: function () {
                            otAlertService.confirm({
                                title: "Deleting a promotion level",
                                message: "Do you really want to delete the promotion level " + promotionLevel.name +
                                    " and all its associated data?"
                            }).then(function () {
                                return ot.call($http.delete(promotionLevel._delete));
                            }).then(function () {
                                $state.go('branch', {branchId: promotionLevel.branch.id});
                            });
                        }
                    },
                    {
                        condition: function () {
                            return promotionLevel._bulkUpdate;
                        },
                        id: 'bulkUpdatePromotionLevel',
                        name: "Bulk update",
                        cls: 'ot-command-update',
                        action: function () {
                            otAlertService.confirm({
                                title: "Promotion levels bulk update",
                                message: "Updates all other promotion levels with the same name?"
                            }).then(function () {
                                return ot.call($http.put(promotionLevel._bulkUpdate, {}));
                            }).then(loadPromotionLevel);
                        }
                    },
                    ot.viewApiCommand($scope.promotionLevel._self),
                    ot.viewCloseCommand('/branch/' + $scope.promotionLevel.branch.id)
                ];
                // Loads the runs
                return ot.call($http.get(promotionLevel._runs));
            }).then(function (promotionRunView) {
                $scope.promotionRunView = promotionRunView;
            });
        }

        // Initialisation
        loadPromotionLevel();

        // Changing the image
        function changeImage() {
            otStructureService.changePromotionLevelImage($scope.promotionLevel).then(loadPromotionLevel);
        }

    })
;