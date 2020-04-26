angular.module('ot.view.api', [
    'ui.router',
    'ot.service.core',
    'ot.directive.api'
])
    .config(function ($stateProvider) {
        $stateProvider.state('api', {
            url: '/api/:link',
            templateUrl: 'app/view/view.api.tpl.html',
            controller: 'APICtrl'
        });
    })
    .controller('APICtrl', function ($q, $http, $location, $scope, $state, $stateParams, ot) {
        $scope.showLinks = true;
        var view = ot.view();
        view.title = "API";
        view.commands = [
            {
                condition: function () {
                    return !$scope.showLinks;
                },
                id: 'api-show-links',
                name: "Show links",
                cls: 'ot-command-api-show',
                action: function () {
                    $scope.showLinks = true;
                }
            },
            {
                condition: function () {
                    return $scope.showLinks;
                },
                id: 'api-hide-links',
                name: "Hide links",
                cls: 'ot-command-api-hide',
                action: function () {
                    $scope.showLinks = false;
                }
            },
            ot.viewCloseCommand('home')
        ];

        // Link to display
        $scope.link = decodeURIComponent($stateParams.link);

        // Loading the API
        function loadAPI(link) {
            $scope.apiLoading = true;

            // Gets the relative path (neat!)
            var a = document.createElement('a');
            a.href = link;
            var path = a.pathname;

            // Calls

            ot.pageCall($http.get("rest/api/describe", {params: {path: path}}))
                .then(function (description) {
                    $scope.description = description;
                    var d = $q.defer();
                    $http.get(link)
                        .success(function (resource) {
                            d.resolve(resource);
                        })
                        .error(function (response) {
                            if (response.status == 403) {
                                $scope.message = "The API you tried to access is not authorised.";
                            } else {
                                $scope.message = "The API could not be accessed: " + response.message;
                            }
                            d.reject();
                        });
                    return d.promise;
                })
                .then(function (resource) {
                    $scope.resource = resource;
                }).finally(function () {
                    $scope.apiLoading = false;
                });
        }

        // Loads the API
        loadAPI($scope.link);
    })
;