angular.module('ot.service.structure', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otStructureService', function (ot, $interpolate, $http, otFormService) {
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
                title: 'New project',
                submit: function (data) {
                    return ot.call($http.post(href, data));
                }
            });
        };

        /**
         * Getting a project
         */
        self.getProject = function (id) {
            return ot.call($http.get('structure/projects/' + id));
        };

        /**
         * Getting the branches for a project
         */
        self.getProjectBranches = function (projectId) {
            return ot.call(
                $http.get(
                    $interpolate('structure/projects/{{projectId}}/branches')({projectId: projectId})
                )
            );
        };

        return self;
    })
;