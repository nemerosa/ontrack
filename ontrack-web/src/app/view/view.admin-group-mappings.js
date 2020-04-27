angular.module('ot.view.admin-group-mappings', [
    'ui.router',
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-group-mappings', {
            url: '/admin-group-mappings',
            templateUrl: 'app/view/view.admin-group-mappings.tpl.html',
            controller: 'AdminGroupMappingsCtrl'
        });
    })

    .controller('AdminGroupMappingsCtrl', function ($scope, $http, ot, otGraphqlService) {
        let view = ot.view();
        view.title = "Account group mappings";
        view.description = "Allows to link some groups managed by Ontrack to some groups made available by external authentication providers.";
        view.commands = [
            ot.viewCloseCommand('/admin-accounts')
        ];

        let query = `
            query Mappings($provider: String, $mapping: String, $group: String) {
                authenticationSourceProviders(groupMappingSupported: true) {
                    enabled
                    source {
                      id
                      name
                      allowingPasswordChange
                      groupMappingSupported
                    }
                  }
                  accountGroupMappings(type: $provider, name: $mapping, group: $group) {
                    id
                    name
                    group {
                      id
                      name
                      description
                    }
                    type
                  }
            }
        `;

        let queryVariables = {
            provider: "",
            mapping: "",
            group: ""
        };

        $scope.filterMapping = {
            provider: "",
            mapping: "",
            group: ""
        };

        let loadMappings = () => {
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.mappings = data.accountGroupMappings;
                $scope.sourceProviders = data.authenticationSourceProviders;
                $scope.sourceProvidersIndex = {};
                $scope.sourceProviders.forEach((sourceProvider) => {
                    $scope.sourceProvidersIndex[sourceProvider.source.id] = sourceProvider;
                });
                $scope.mappings.forEach((mapping) => {
                    mapping.provider =  $scope.sourceProvidersIndex[mapping.type];
                });
            });
        };

        loadMappings();

        $scope.filterClear = () => {
            $scope.filterMapping.mapping = "";
            $scope.filterMapping.provider = "";
            $scope.filterMapping.group = "";
            $scope.filterLaunch();
        };

        $scope.filterLaunch = () => {
            queryVariables.provider = $scope.filterMapping.provider;
            queryVariables.mapping = $scope.filterMapping.mapping;
            queryVariables.group = $scope.filterMapping.group;
            loadMappings();
        };
    })
;