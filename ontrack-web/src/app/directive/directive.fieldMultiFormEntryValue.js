angular.module("ot.directive.fieldMultiFormEntryValue", [])
    .directive("otFieldMultiFormEntryValue", function ($compile) {
        return {
            restrict: "E",
            // We need a dynamically compiled template because recursive directives are not allowed
            // See also directive.field.multiForm.js
            template: '<div></div>',
            scope: {
                item: "=",
                form: "="
            },
            link: function (scope, element) {
                if (angular.isDefined(scope.item) && angular.isDefined(scope.form)) {
                    // Copy of the form definition
                    const localForm = angular.copy(scope.form);
                    // Assign values to the local form
                    localForm.fields.forEach((field) => {
                        // Gets the value from the item
                        field.value = scope.item[field.name];
                    });
                    scope.localForm = localForm;
                    const template = `
                        <table class="ot-form-view">
                            <tbody>
                            <tr ng-repeat="field in localForm.fields">
                                <td>
                                    {{ field.label }}
                                </td>
                                <td>
                                    <ot-field-value field="field"></ot-field-value>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    `;
                    $compile(template)(scope, function (cloned, scope) {
                        element.append(cloned);
                    });
                }
            }
        };
    })
;