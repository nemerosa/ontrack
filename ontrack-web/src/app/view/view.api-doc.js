angular.module('ot.view.api-doc', [
    'ui.router',
    'ot.service.core',
    'ot.directive.api'
])
    .config(function ($stateProvider) {
        $stateProvider.state('api-doc', {
            url: '/api-doc',
            templateUrl: 'app/view/view.api-doc.tpl.html',
            controller: 'APIDocCtrl'
        });
    })
    .controller('APIDocCtrl', function ($http, $state, $scope, ot) {
        var view = ot.view();
        view.title = "API Documentation";
        view.commands = [
            ot.viewCloseCommand('home')
        ];

        // Loading the whole API
        function loadApi() {
            $scope.apiLoading = true;
            ot.pageCall($http.get('api/list'))
                .then(function (list) {
                    /**
                     * Preprocessing - collecting root access points
                     */
                    angular.forEach(list.resources, function (apiInfo) {
                        angular.forEach(apiInfo.methods, function (apiMethodInfo) {
                            apiMethodInfo.root = (apiMethodInfo.path.indexOf('{') < 0);
                            apiMethodInfo.getMethod = (apiMethodInfo.methods.indexOf('GET') >= 0);
                        });
                        apiInfo.root = apiInfo.methods.some(function (apiMethodInfo) {
                            return apiMethodInfo.root;
                        });
                    });
                    $scope.list = list;
                })
                .finally(function () {
                    $scope.apiLoading = false;
                });
        }

        loadApi();

        // Accessing the decsription of an API
        $scope.followApi = function (path) {
            var a = document.createElement('a');
            a.href = path;
            var link = a.href;
            $state.go(
                'api',
                {link: encodeURIComponent(link)}
            );
        };

    })
;