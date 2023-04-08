angular.module('ontrack.extension.recordings', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('recordings-list', {
            url: '/extension/recordings/list/{extension}',
            templateUrl: 'extension/recordings/list.tpl.html',
            controller: 'RecordingsCtrl'
        });
    })
    .controller('RecordingsCtrl', function ($http, $scope, $stateParams, ot, otAlertService, otGraphqlService) {
        const extension = $stateParams.extension;
        $scope.extension = extension;
    })
;