angular.module('ot.view.search', [
    'ui.router',
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('search', {
            url: '/search',
            templateUrl: 'app/view/view.search.tpl.html',
            controller: 'SearchCtrl'
        });
    })
    .controller('SearchCtrl', function ($location, $stateParams, $scope, $rootScope, $http, $log, ot, otGraphqlService) {

        // GraphQL query
        let query = `
            query Search($token: String!, $type: String) {
                search(token: $token, type: $type, offset: 0, size: 20) {
                    pageItems {
                        title
                        description
                        accuracy
                        uri
                        page
                        type {
                            id
                            name
                            feature {
                                id
                            }
                        }
                    }
                }
            }
        `;

        // View definition
        let view = ot.view();
        view.commands = [ot.viewCloseCommand('/home')];

        let search = () => {

            // Search token
            let token = $location.search().token;

            // Search type
            let type = $location.search().type;

            // Request
            let request = {
                token: token
            };
            if (type) {
                request.type = type;
            }

            // View definition
            view.title = `Search results for "${token}"`;

            // Launching the search
            otGraphqlService.pageGraphQLCall(query, request).then(function (data) {
                $scope.searchDone = true;
                $scope.results = data.search.pageItems;
                // If only one result, switches directly to the correct page
                if ($scope.results.length === 1) {
                    var result = $scope.results[0];
                    $log.info('[search] Autoredirect for 1 result: ', result);
                    if (result.page) {
                        window.location = result.page;
                    } else {
                        $log.error('[search] Could not find any page in the result:', result);
                    }
                }
            });

        };

        // Initial loading
        search();

        // Listening for the location
        $scope.$watch(() => $location.search(), (newValue, oldValue) => {
            if (oldValue !== newValue) {
                search();
            }
        });

    })
;