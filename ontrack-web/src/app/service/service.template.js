angular.module('ot.service.template', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otTemplateService', function ($http, $modal, ot, otFormService, otAlertService) {
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
         * Synchronises the template definition with branches.
         * @param templateSyncUri URL to get the template definition.
         */
        self.templateSync = function (templateSyncUri) {
            otAlertService.displayProgressDialog({
                title: "Template synchronisation",
                promptMessage: "Synchronisation of this template is about to start. This might change the linked " +
                    "instances. Do you want to continue?",
                waitingMessage: "Synchronising the template...",
                endMessage: "Synchronisation has been done.",
                resultUri: 'app/service/service.template.syncResult.tpl.html',
                task: function() {
                    return ot.call($http.post(templateSyncUri));
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
                    var name = data.name;
                    var manual = data.manual;
                    delete data.name;
                    delete data.manual;
                    var request = {
                        name: name,
                        manual: manual,
                        parameters: data
                    };
                    return ot.call($http.put(templateInstanceUri, request));
                }
            });
        };

        return self;
    })
;
