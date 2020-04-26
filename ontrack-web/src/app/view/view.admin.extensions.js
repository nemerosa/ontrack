angular.module('ot.view.admin.extensions', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-extensions', {
            url: '/admin-extensions',
            templateUrl: 'app/view/view.admin.extensions.tpl.html',
            controller: 'AdminExtensionsCtrl'
        });
    })
    .controller('AdminExtensionsCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "System extensions";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loads the extensions
        function loadExtensions() {
            ot.call($http.get('rest/extensions')).then(function (extensions) {
                $scope.extensions = extensions;
            });
        }

        // Initialisation
        loadExtensions();

    })
;