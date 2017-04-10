angular.module('ot.view.validationStamp', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('validationStamp', {
            url: '/validationStamp/{validationStampId}',
            templateUrl: 'app/view/view.validationStamp.tpl.html',
            controller: 'ValidationStampCtrl'
        });
    })
    .controller('ValidationStampCtrl', function ($state, $scope, $stateParams, $http, ot, otStructureService, otAlertService) {
        var view = ot.view();
        // ValidationStamp's id
        var validationStampId = $stateParams.validationStampId;

        // Loading the promotion level
        function loadValidationStamp() {
            otStructureService.getValidationStamp(validationStampId).then(function (validationStamp) {
                $scope.validationStamp = validationStamp;
                // View title
                view.breadcrumbs = ot.branchBreadcrumbs(validationStamp.branch);
                // Commands
                view.commands = [
                    {
                        condition: function () {
                            return validationStamp._update;
                        },
                        id: 'updateValidationStampImage',
                        name: "Change image",
                        cls: 'ot-command-validation-stamp-image',
                        action: changeImage
                    },
                    {
                        condition: function () {
                            return validationStamp._update;
                        },
                        id: 'updateValidationStamp',
                        name: "Update validation stamp",
                        cls: 'ot-command-validation-stamp-update',
                        action: function () {
                            otStructureService.update(
                                validationStamp._update,
                                "Update validation stamp"
                            ).then(loadValidationStamp);
                        }
                    },
                    {
                        condition: function () {
                            return validationStamp._delete;
                        },
                        id: 'deleteValidationStamp',
                        name: "Delete validation stamp",
                        cls: 'ot-command-validation-stamp-delete',
                        action: function () {
                            otAlertService.confirm({
                                title: "Deleting a validation stamp",
                                message: "Do you really want to delete the validation stamp " + validationStamp.name +
                                    " and all its associated data?"
                            }).then(function () {
                                return ot.call($http.delete(validationStamp._delete));
                            }).then(function () {
                                $state.go('branch', {branchId: validationStamp.branch.id});
                            });
                        }
                    },
                    {
                        condition: function () {
                            return validationStamp._bulkUpdate;
                        },
                        id: 'bulkUpdateValidationStamp',
                        name: "Bulk update",
                        cls: 'ot-command-update',
                        action: function () {
                            otAlertService.confirm({
                                title: "Validation stamps bulk update",
                                message: "Updates all other validation stamps with the same name?"
                            }).then(function () {
                                return ot.call($http.put(validationStamp._bulkUpdate));
                            }).then(loadValidationStamp);
                        }
                    },
                    ot.viewApiCommand($scope.validationStamp._self),
                    ot.viewCloseCommand('/branch/' + $scope.validationStamp.branch.id)
                ];
                // Loading the validation runs
                return ot.pageCall($http.get(validationStamp._runs));
            }).then(function (validationRunResources) {
                $scope.validationRunResources = validationRunResources;
            });
        }

        // Initialisation
        loadValidationStamp();

        // Changing the image
        function changeImage () {
            otStructureService.changeValidationStampImage($scope.validationStamp).then(loadValidationStamp);
        }

        // Switching the page
        $scope.switchPage = function (pageLink) {
            ot.pageCall($http.get(pageLink)).then(function (validationRunResources) {
                $scope.validationRunResources = validationRunResources;
            });
        };

    })
;