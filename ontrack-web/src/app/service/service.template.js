angular.module('ot.service.template', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otTemplateService', function ($http, ot, otFormService) {
        var self = {};

        /**
         * Displays and manages the template definition for a branch.
         * @param templateDefinitionUri URL to get the template definition.
         */
        self.templateDefinition = function (templateDefinitionUri) {
            return otFormService.display({
                uri: templateDefinitionUri,
                title: "Template configuration",
                submit: function (template) {
                    return ot.call($http.put(templateDefinitionUri, template));
                }
            });
        };

        return self;
    })
;
