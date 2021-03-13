angular.module('ot.view.admin.global-acl', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-global-acl', {
            url: '/admin-global-acl',
            templateUrl: 'app/view/view.admin.global-acl.tpl.html',
            controller: 'AdminGlobalACLCtrl'
        });
    })

    .controller('AdminGlobalACLCtrl', function ($scope, $http, $interpolate, ot) {
        var view = ot.view();
        view.title = "Global permissions";

        $scope.form = {
        };

        // Loading the global permissions
        function load() {
            ot.pageCall($http.get("rest/accounts/permissions/globals")).then(function (globalPermissions) {
                $scope.globalPermissions = globalPermissions;
                view.commands = [
                    ot.viewCloseCommand('/admin-accounts')
                ];
                return ot.pageCall($http.get(globalPermissions._globalRoles));
            }).then(function (globalRoles) {
                $scope.globalRoles = globalRoles;
            });
        }

        // Loading the page
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

        $scope.saveGlobalPermission = function () {
            ot.call(
                $http.put(
                    $interpolate('rest/accounts/permissions/globals/{{type}}/{{id}}')($scope.form.permissionTarget),
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