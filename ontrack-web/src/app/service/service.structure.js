angular.module('ot.service.structure', [
    'ot.service.core'
])
    .service('otStructureService', function (ot, $http) {
        var self = {};

        /**
         * Loading the projects
         */
        self.getProjects = function () {
            return ot.call($http.get('structure/projects'));
        };

        return self;
    })
;