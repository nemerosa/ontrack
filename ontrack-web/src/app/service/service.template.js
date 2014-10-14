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
                size: 'lg',
                submit: function (template) {
                    return ot.call($http.put(templateDefinitionUri, template));
                }
            });
        };

        /**
         * Gets a single branch name and creates a template instance from it.
         */
        self.createTemplateInstance = function (templateInstanceUri) {
            return otFormService.display({
                uri: templateInstanceUri,
                title: "Template instance creation",
                submit: function (data) {
                    return ot.call($http.put(templateInstanceUri, {name: data.name}));
                }
            });
        };

        return self;
    })
;
