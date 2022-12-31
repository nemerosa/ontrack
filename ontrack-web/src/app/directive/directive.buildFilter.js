angular.module('ot.directive.buildFilter', [
    'ot.service.graphql',
])
    .directive('otBuildFilter', function (otGraphqlService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.buildFilter.tpl.html',
            scope: {
            },
            controller: ($scope) => {
                $scope.loadingFilters = true;
            }
        };
    })
;