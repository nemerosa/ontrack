angular.module('ot.directive.form', [
    'ot.directive.field'
])
    .directive('otForm', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.form.tpl.html',
            scope: {
                data: '=',
                form: '=',
                formRoot: '='
            },
            controller: function ($scope) {
                // Checking if a field is visible or not
                $scope.isFieldVisible = isFieldVisible;
                function isFieldVisible(data, field) {
                    if (field.visibleIf) {
                        var pos = field.visibleIf.indexOf('.');
                        if (pos > 0) {
                            var fieldName = field.visibleIf.substring(0, pos);
                            var fieldProperty = field.visibleIf.substring(pos + 1);
                            var dataField = data[fieldName];
                            return dataField && isFieldVisible(dataField, {
                                    visibleIf: fieldProperty
                                });
                        } else {
                            return data[field.visibleIf];
                        }
                    } else {
                        return true;
                    }
                }
            }
        };
    })
;