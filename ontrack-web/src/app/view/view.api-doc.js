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
            {
                condition: function () {
                    return $scope.show != 'root+api';
                },
                name: "Show roots with accessible API only",
                cls: 'ot-command-api-show',
                action: function () {
                    $scope.show = 'root+api';
                }
            },
            {
                condition: function () {
                    return $scope.show != 'root';
                },
                name: "Show roots only",
                cls: 'ot-command-api-show',
                action: function () {
                    $scope.show = 'root';
                }
            },
            {
                condition: function () {
                    return $scope.show != 'all';
                },
                name: "Show all",
                cls: 'ot-command-api-show',
                action: function () {
                    $scope.show = 'all';
                }
            },
            {
                name: "Show/hide help",
                cls: 'ot-command-api-help',
                action: function () {
                    $scope.showHelp = !$scope.showHelp;
                }
            },
            ot.viewCloseCommand('home')
        ];

        // Display flags
        $scope.show = 'root+api';

        // Loading the whole API
        function loadApi() {
            $scope.apiLoading = true;
            ot.pageCall($http.get('rest/api/list'))
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
                        apiInfo.getMethod = apiInfo.methods.some(function (apiMethodInfo) {
                            return apiMethodInfo.getMethod;
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