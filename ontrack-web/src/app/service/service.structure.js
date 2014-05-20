angular.module('ot.service.structure', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.image'
])
    .service('otStructureService', function (ot, $q, $interpolate, $http, $modal, otFormService) {
        var self = {};

        /**
         * Loading the projects
         */
        self.getProjects = function () {
            return ot.call($http.get('structure/projects'));
        };

        /**
         * Creating from a form, using POST
         */
        self.create = function (uri, title) {
            return otFormService.display({
                uri: uri,
                title: title,
                submit: function (data) {
                    return ot.call($http.post(uri, data));
                }
            });
        };

        /**
         * Creating a project
         * @param uri URI to the creation URL
         */
        self.createProject = function (uri) {
            return self.create(uri, 'New project');
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

        /**
         * Creating a branch
         */
        self.createBranch = function (uri) {
            return self.create(uri, 'New branch');
        };

        /**
         * Getting a branch
         */
        self.getBranch = function (id) {
            return ot.call($http.get('structure/branches/' + id));
        };

        /**
         * Creating a build
         */
        self.createBuild = function (uri) {
            return self.create(uri, 'New build');
        };

        /**
         * Creating a promotion level
         */
        self.createPromotionLevel = function (uri) {
            return self.create(uri, 'New promotion level');
        };

        /**
         * Changing the image of a promotion level
         */
        self.changePromotionLevelImage = function (promotionLevel) {
            var d = $q.defer();
            $modal.open({
                templateUrl: 'app/dialog/dialog.image.tpl.html',
                controller: 'otDialogImage',
                resolve: {
                    config: function () {
                        return {
                            title: 'Image for promotion level ' + promotionLevel.name,
                            image: {
                                present: promotionLevel.image,
                                href: promotionLevel.image.href
                            }
                        };
                    }
                }
            }).result.then(
                function success() {
                    d.resolve();
                },
                function error() {
                    d.reject();
                }
            );
            return d.promise;
        };

        return self;
    })
;