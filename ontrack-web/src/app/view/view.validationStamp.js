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
    .controller('ValidationStampCtrl', function ($scope, $stateParams, $http, ot, otStructureService) {
        var view = ot.view();
        // ValidationStamp's id
        var validationStampId = $stateParams.validationStampId;

        // Loading the promotion level
        function loadValidationStamp() {
            otStructureService.getValidationStamp(validationStampId).then(function (validationStamp) {
                $scope.validationStamp = validationStamp;
                // View title
                view.title = $scope.validationStamp.name;
                view.description = $scope.validationStamp.description;
                view.breadcrumbs = ot.branchBreadcrumbs(validationStamp.branch);
                // Commands
                view.commands = [
                    ot.viewCloseCommand('/branch/' + $scope.validationStamp.branch.id)
                ];
            });
        }

        // Initialisation
        loadValidationStamp();

        // Changing the image
        $scope.changeImage = function () {
            otStructureService.changeValidationStampImage($scope.validationStamp).then(loadValidationStamp);
        };

    })
;