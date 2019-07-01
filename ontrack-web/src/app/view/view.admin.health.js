angular.module('ot.view.admin.health', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-health', {
            url: '/admin-health',
            templateUrl: 'app/view/view.admin.health.tpl.html',
            controller: 'AdminHealthCtrl'
        });
    })
    .controller('AdminHealthCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "System health";
        view.description = "Status of Ontrack system and configured resources";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        $scope.loadingStatus = false;

        // Loads the statuses
        function loadHealth() {
            $scope.loadingStatus = true;
            ot.call($http.get('admin/status')).then(status => {
                $scope.status = status;
            }).finally(() => {
                $scope.loadingStatus = false;
            });
        }

        // Initialisation
        loadHealth();

    })
;