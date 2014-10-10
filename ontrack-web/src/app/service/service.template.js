angular.module('ot.service.template', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.template.definition'
])
    .service('otTemplateService', function ($modal, $http, ot) {
        var self = {};

        /**
         * Displays and manages the template definition for a branch.
         * @param templateDefinitionUri URL to get the template definition.
         */
        self.templateDefinition = function (templateDefinitionUri) {
            return $modal.open({
                templateUrl: 'app/dialog/dialog.template.definition.tpl.html',
                controller: 'otDialogTemplateDefinition',
                resolve: {
                    templateDefinition: ot.call($http.get(templateDefinitionUri)),
                    config: function () {
                        return {
                            submit: function (template) {
                                return ot.call($http.put(templateDefinitionUri, template));
                            }
                        };
                    }
                }
            }).result;
        };

        return self;
    })
;
