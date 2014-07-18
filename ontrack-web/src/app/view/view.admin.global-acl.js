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

    .controller('AdminGlobalACLCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "Global permissions";
        view.commands = [
            ot.viewCloseCommand('/admin-accounts')
        ];

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

    })

;