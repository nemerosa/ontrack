angular.module('ot.view.promotionLevel', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('promotionLevel', {
            url: '/promotionLevel/{promotionLevelId}',
            templateUrl: 'app/view/view.promotionLevel.tpl.html',
            controller: 'PromotionLevelCtrl'
        });
    })
    .controller('PromotionLevelCtrl', function ($state, $scope, $stateParams, $http, ot, otStructureService, otAlertService, otGraphqlService) {
        const view = ot.view();
        // PromotionLevel's id
        const promotionLevelId = $stateParams.promotionLevelId;
        // GraphQL query
        const query = `query PromotionLevel($id: Int!) {
            promotionLevel(id: $id) {
                id
                name
                description
                annotatedDescription
                image
                _image
                promotionRuns {
                    description
                    annotatedDescription
                    build {
                        name
                        links {
                            _page
                        }
                        decorations {
                          decorationType
                          error
                          data
                          feature {
                            id
                          }
                        }
                    }
                    creation {
                        user
                        time
                    }
                }
                decorations {
                    decorationType
                    data
                    error
                    feature {
                      id
                    }
                }
                branch {
                    id
                    name
                    project {
                        id
                        name
                    }
                }
                links {
                    _self
                    _update
                    _delete
                    _bulkUpdate
                    _runs
                    _properties
                    _events
                }
            }
        }`;

        // Loading the promotion level
        function loadPromotionLevel() {
            otGraphqlService.pageGraphQLCall(query, {id: promotionLevelId}).then((data) => {
                let promotionLevel = data.promotionLevel;
                $scope.promotionLevel = promotionLevel;
                // View breadcrumbs
                view.breadcrumbs = ot.branchBreadcrumbs(promotionLevel.branch);
                // Commands
                view.commands = [
                    {
                        condition: function () {
                            return promotionLevel.links._update;
                        },
                        id: 'updatePromotionLevelImage',
                        name: "Change image",
                        cls: 'ot-command-promotion-level-image',
                        action: changeImage
                    },
                    {
                        condition: function () {
                            return promotionLevel.links._update;
                        },
                        id: 'updatePromotionLevel',
                        name: "Update promotion level",
                        cls: 'ot-command-promotion-level-update',
                        action: function () {
                            otStructureService.update(
                                promotionLevel.links._update,
                                "Update promotion level"
                            ).then(loadPromotionLevel);
                        }
                    },
                    {
                        condition: function () {
                            return promotionLevel.links._delete;
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
                                return ot.call($http.delete(promotionLevel.links._delete));
                            }).then(function () {
                                $state.go('branch', {branchId: promotionLevel.branch.id});
                            });
                        }
                    },
                    {
                        condition: function () {
                            return promotionLevel.links._bulkUpdate;
                        },
                        id: 'bulkUpdatePromotionLevel',
                        name: "Bulk update",
                        cls: 'ot-command-update',
                        action: function () {
                            otAlertService.confirm({
                                title: "Promotion levels bulk update",
                                message: "Updates all other promotion levels with the same name?"
                            }).then(function () {
                                return ot.call($http.put(promotionLevel.links._bulkUpdate, {}));
                            }).then(loadPromotionLevel);
                        }
                    },
                    ot.viewApiCommand($scope.promotionLevel.links._self),
                    ot.viewCloseCommand('/branch/' + $scope.promotionLevel.branch.id)
                ];
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