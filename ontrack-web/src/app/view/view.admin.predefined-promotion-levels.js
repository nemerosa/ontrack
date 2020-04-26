angular.module('ot.view.admin.predefined-promotion-levels', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-predefined-promotion-levels', {
            url: '/admin-predefined-promotion-levels',
            templateUrl: 'app/view/view.admin.predefined-promotion-levels.tpl.html',
            controller: 'AdminPredefinedPromotionLevelsCtrl'
        });
    })

    .controller('AdminPredefinedPromotionLevelsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otStructureService) {
        var view = ot.view();
        view.title = "Predefined promotion levels";
        view.commands = [
            {
                id: 'admin-predefined-promotion-levels-new',
                name: "New predefined promotion level",
                cls: 'ot-command-new',
                action: newPredefinedPromotionLevel
            },
            ot.viewCloseCommand('/home')
        ];

        function loadPredefinedPromotionLevels() {
            ot.pageCall($http.get('rest/admin/predefinedPromotionLevels')).then(function (predefinedPromotionLevels) {
                $scope.predefinedPromotionLevels = predefinedPromotionLevels;
                $scope.predefinedPromotionLevelSortOptions = {
                    disabled: !$scope.predefinedPromotionLevels._reorderPromotionLevels,
                    stop: function (event, ui) {
                        var ids = $scope.predefinedPromotionLevels.resources.map(function (pl) {
                            return pl.id;
                        });
                        ot.call($http.put(
                            $scope.predefinedPromotionLevels._reorderPromotionLevels,
                            { ids: ids}
                        ));
                    }
                };
            });
        }

        loadPredefinedPromotionLevels();

        function newPredefinedPromotionLevel() {
            otFormService.create($scope.predefinedPromotionLevels._create, "New predefined promotion level")
                .then(loadPredefinedPromotionLevels);
        }

        // Editing a predefined promotion level
        $scope.editPromotionLevel = function (predefinedPromotionLevel) {
            otFormService.update(predefinedPromotionLevel._update, "Edit predefined promotion level")
                .then(loadPredefinedPromotionLevels);
        };

        // Deleting a predefined promotion level
        $scope.deletePromotionLevel = function (predefinedPromotionLevel) {
            otAlertService.confirm({
                title: "Predefined promotion level deletion",
                message: "Do you really want to delete this promotion level?"
            }).then(function () {
                ot.pageCall($http.delete(predefinedPromotionLevel._delete)).then(loadPredefinedPromotionLevels);
            });
        };

        // Updating the image for a predefined promotion level
        $scope.editPromotionLevelImage = function (predefinedPromotionLevel) {
            otStructureService.changeImage(predefinedPromotionLevel, {
                title: 'Image for predefined promotion level ' + predefinedPromotionLevel.name
            }).then(loadPredefinedPromotionLevels);
        };

    })

;