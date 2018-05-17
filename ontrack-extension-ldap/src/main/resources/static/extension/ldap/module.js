angular.module('ontrack.extension.ldap', [])
    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('ldap-mapping', {
            url: '/extension/ldap/ldap-mapping',
            templateUrl: 'extension/ldap/ldap.mapping.tpl.html',
            controller: 'LDAPMappingCtrl'
        });
    })
    .controller('LDAPMappingCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        var view = ot.view();
        view.title = 'LDAP Mappings';
        view.description = 'Mapping from LDAP groups to Ontrack groups.';

        // Loading the mappings
        function loadMappings() {
            ot.pageCall($http.get('extension/ldap/ldap-mapping')).then(function (mappingResources) {
                $scope.mappingResources = mappingResources;
                // Commands
                view.commands = [
                    {
                        id: 'ldap-mapping-create',
                        name: "Create mapping",
                        cls: 'ot-command-new',
                        action: function () {
                            otFormService.create($scope.mappingResources._create, "Mapping creation").then(loadMappings);
                        }
                    },
                    ot.viewApiCommand(mappingResources._self),
                    ot.viewCloseCommand('/admin-accounts')
                ];
            });
        }

        loadMappings();

        // Updating a mapping
        $scope.updateMapping = function (mapping) {
            otFormService.update(mapping._update, "Updating mapping").then(loadMappings);
        };

        // Deleting a mapping
        $scope.deleteMapping = function (mapping) {
            otAlertService.confirm({
                title: "Mapping deletion",
                message: "Do you really want to delete this mapping?"
            }).then(function () {
                ot.pageCall($http.delete(mapping._delete)).then(loadMappings);
            });
        };
    })
;