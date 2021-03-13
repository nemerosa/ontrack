angular.module('ot.view.admin.project-acl', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-project-acl', {
            url: '/admin-project-acl/{projectId}',
            templateUrl: 'app/view/view.admin.project-acl.tpl.html',
            controller: 'AdminProjectACLCtrl'
        });
    })

    .controller('AdminProjectACLCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otStructureService) {

        var projectId = $stateParams.projectId;
        var view = ot.view();
        view.title = "Project permissions";

        // Target/role association form
        $scope.form = {
        };

        // Loading the project
        function load() {
            otStructureService.getProject(projectId).then(function (project) {
                view.breadcrumbs = ot.projectBreadcrumbs(project);
                return ot.pageCall($http.get('rest/accounts/permissions/projects/' + projectId));
            }).then(function (projectPermissions) {
                view.commands = [
                    ot.viewCloseCommand('/project/' + projectId)
                ];
                $scope.projectPermissions = projectPermissions;
                return ot.pageCall($http.get(projectPermissions._projectRoles));
            }).then(function (projectRoles) {
                $scope.projectRoles = projectRoles;
            });
        }
        load();

        // Loading the permission targets
        $scope.loadPermissionTargets = function (token) {
            return ot.call($http.get('rest/accounts/permissions/search/' + token)).then(function (permissionTargets) {
                return permissionTargets.resources;
            });
        };

        $scope.formatPermissionTarget = function (target) {
            if (target) {
                return target.name + (target.type == 'ACCOUNT' ? '' : ' (group)');
            } else {
                return '';
            }
        };

        $scope.saveProjectPermission = function () {
            ot.call(
                $http.put(
                    $interpolate('rest/accounts/permissions/projects/{{projectId}}/{{type}}/{{id}}')({
                        projectId: projectId,
                        type: $scope.form.permissionTarget.type,
                        id: $scope.form.permissionTarget.id
                    }),
                    {
                        role: $scope.form.role.id
                    }
                )
            ).then(function () {
                    load();
                    delete $scope.form.permissionTarget;
                });
        };

        $scope.removeProjectPermission = function (projectPermission) {
            ot.call($http.delete(projectPermission._delete)).then(load);
        };

    })

;