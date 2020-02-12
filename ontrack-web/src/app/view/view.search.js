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
            query Search($token: String!, $type: String, $offset: Int!, $size: Int!) {
                search(token: $token, type: $type, offset: $offset, size: $size) {
                    pageInfo {
                        nextPage { offset size }
                    }
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
        view.title = "";
        view.commands = [ot.viewCloseCommand('/home')];
        view.disableSearch = true; // Managed in the page itself

        // Search configuration
        $scope.searchConfig = {
            token: $location.search().token,
            type: $location.search().type
        };

        // Initial list of results
        $scope.results = [];

        // Initial request
        let request = {
            offset: 0,
            size: 10
        };

        let search = () => {

            // Search token
            let token = $location.search().token;

            // Search type
            let type = $location.search().type;

            // Offset reset
            if (request.token !== token || request.type !== type) {
                request.offset = 0;
            }

            // Request
            request.token = token;
            request.type = type;

            // Launching the search
            otGraphqlService.pageGraphQLCall(query, request).then(function (data) {
                $scope.searchDone = true;
                $scope.pageInfo = data.search.pageInfo;
                if (request.offset > 0) {
                    $scope.results = $scope.results.concat(data.search.pageItems);
                } else {
                    $scope.results = data.search.pageItems;
                }
                // If only one result, switches directly to the correct page
                if ($scope.results.length === 1) {
                    let result = $scope.results[0];
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

        // Load more results
        $scope.searchMore = (page) => {
            request.offset = page.offset;
            request.size = page.size;
            search();
        };

        // Listening for the location
        $scope.$watch(() => $location.search(), (newValue, oldValue) => {
            if (oldValue !== newValue && newValue.token) {
                search();
            }
        });

    })
;