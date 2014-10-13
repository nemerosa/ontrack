angular.module('ot.directive.field.serviceConfigurator', [
    'ot.service.form'
])
    .directive('otFieldServiceConfigurator', function (otFormService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.serviceConfigurator.tpl.html',
            scope: {
                field: '=',
                data: '=',
                formRoot: '='
            },
            controller: function ($scope) {

                // Loading of the form on selection
                $scope.$watch('sourceId', function (sourceId) {
                    console.log('sourceId=', sourceId);
                });

            }
        };
    })
;