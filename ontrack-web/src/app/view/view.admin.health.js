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

        // Loads the statuses
        function loadHealth() {
            ot.call($http.get('admin/status')).then(function (health) {
                $scope.health = health;
            });
        }

        // Initialisation
        loadHealth();

    })
;