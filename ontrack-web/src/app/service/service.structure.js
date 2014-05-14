angular.module('ot.service.structure', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otStructureService', function (ot, $http, otFormService) {
        var self = {};

        /**
         * Loading the projects
         */
        self.getProjects = function () {
            return ot.call($http.get('structure/projects'));
        };

        /**
         * Creating a project
         * @param href URI to the creation URL
         */
        self.createProject = function (href) {
            return otFormService.display({
                uri: href,
                title: 'New project'
                // TODO Creation of the form for the submission
            });
        };

        return self;
    })
;