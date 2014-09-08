angular.module('ot.view.admin.project-acl', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-project-acl', {
            url: '/admin-project-acl/{projectId}',
            templateUrl: 'app/view/view.admin.project-acl.tpl.html',
            controller: 'AdminProjectACLCtrl'
        });
    })

    .controller('AdminProjectACLCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var projectId = $stateParams.branchId;

        var view = ot.view();
        view.title = "Project permissions"; // TODO Project name
        view.commands = [
            ot.viewCloseCommand('/project/' + projectId)
        ];

        $scope.form = {
        };

        // Loading the global permissions
        function load() {
            ot.pageCall($http.get("accounts/permissions/globals")).then(function (globalPermissions) {
                $scope.globalPermissions = globalPermissions;
                return ot.pageCall($http.get(globalPermissions._globalRoles));
            }).then(function (globalRoles) {
                $scope.globalRoles = globalRoles;
            });
        }

        // Loading the page
        load();

        // Loading the permission targets
        $scope.loadPermissionTargets = function (token) {
            return ot.call($http.get('accounts/permissions/search/' + token)).then(function (permissionTargets) {
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

        $scope.saveGlobalPermission = function () {
            ot.call(
                $http.put(
                    $interpolate('accounts/permissions/globals/{{type}}/{{id}}')($scope.form.permissionTarget),
                    {
                        role: $scope.form.role.id
                    }
                )
            ).then(function () {
                    load();
                    delete $scope.form.permissionTarget;
                });
        };

        $scope.removeGlobalPermission = function (globalPermission) {
            ot.call($http.delete(globalPermission._delete)).then(load);
        };

    })

;