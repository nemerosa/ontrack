angular.module('ot.service.structure', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.image'
])
    .service('otStructureService', function (ot, $q, $interpolate, $http, $modal, otFormService, otAlertService) {
        var self = {};

        /**
         * Loading the projects
         */
        self.getProjects = function () {
            return ot.call($http.get('rest/structure/projects'));
        };

        /**
         * Creating from a form, using POST
         */
        self.create = otFormService.create;

        /**
         * Updating from a form, using PUT
         */
        self.update = otFormService.update;

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
            return ot.call($http.get('rest/structure/projects/' + id));
        };

        /**
         * Deleting a branch
         */
        self.deleteBranch = function (branch) {
            return otAlertService.confirm({
                title: "Deleting a branch",
                message: "Do you really want to delete the branch " + branch.name +
                " and all its associated data?"
            }).then(function () {
                return ot.call($http.delete(branch._delete));
            });
        };

        /**
         * Getting a branch
         */
        self.getBranch = function (id) {
            return ot.call($http.get('rest/structure/branches/' + id));
        };

        /**
         * Getting a promotion level
         */
        self.getPromotionLevel = function (id) {
            return ot.call($http.get('rest/structure/promotionLevels/' + id));
        };

        /**
         * Getting a validation stamp
         */
        self.getValidationStamp = function (id) {
            return ot.call($http.get('rest/structure/validationStamps/' + id));
        };

        /**
         * Creating a build
         */
        self.createBuild = function (uri) {
            return self.create(uri, 'New build');
        };

        /**
         * Getting a build
         */
        self.getBuild = function (id) {
            return ot.call($http.get('rest/structure/builds/' + id));
        };

        /**
         * Changing the image
         * @param config.title Title for the dialog
         */
        self.changeImage = function (entity, config) {
            var d = $q.defer();
            $modal.open({
                templateUrl: 'app/dialog/dialog.image.tpl.html',
                controller: 'otDialogImage',
                resolve: {
                    config: function () {
                        return {
                            title: config.title,
                            image: {
                                present: entity.image,
                                href: entity._image
                            },
                            submit: function (file) {
                                var fd = new FormData();
                                fd.append('file', file);
                                return ot.call($http.post(
                                    entity._image,
                                    fd, {
                                        transformRequest: angular.identity,
                                        headers: {'Content-Type': undefined}
                                    }
                                ));
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

        /**
         * Changing the image of a promotion level
         */
        self.changePromotionLevelImage = function (promotionLevel) {
            return self.changeImage(promotionLevel, {
                title: 'Image for promotion level ' + promotionLevel.name
            });
        };

        /**
         * Changing the image of a validation stamp
         */
        self.changeValidationStampImage = function (validationStamp) {
            return self.changeImage(validationStamp, {
                title: 'Image for validation stamp ' + validationStamp.name
            });
        };

        return self;
    })
;