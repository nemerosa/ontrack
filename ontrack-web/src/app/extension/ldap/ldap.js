angular.module('ontrack.extension.ldap', [
])
    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('ldap-mapping', {
            url: '/extension/ldap/ldap-mapping',
            templateUrl: 'app/extension/ldap/ldap-mapping.tpl.html',
            controller: 'LDAPMappingCtrl'
        });
    })
    .controller('LDAPMappingCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        var view = ot.view();
        view.title = 'LDAP Mappings';
        view.description = 'Mapping from LDAP groups to Ontrack groups.';
    })
;