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
            query Mappings($provider: String, $source: String, $mapping: String, $group: String) {
              authenticationSources(groupMappingSupported: true) {
                ...sourceFields
              }
              accountGroupMappings(provider: $provider, source: $source, name: $mapping, group: $group) {
                id
                name
                group {
                  id
                  name
                  description
                }
                authenticationSource {
                  ...sourceFields
                }
              }
              accountGroups {
                id
                name
              }
            }
            
            fragment sourceFields on AuthenticationSource {
              provider
              key
              name
              enabled
              allowingPasswordChange
              groupMappingSupported
            }
        `;

        let queryVariables = {
            provider: "",
            source: "",
            mapping: "",
            group: ""
        };

        $scope.filterMapping = {
            source: "",
            mapping: "",
            group: ""
        };

        $scope.mappingForm = {
            source: "",
            mapping: "",
            group: ""
        };

        let viewInitialized = false;

        let loadMappings = () => {
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                if (!viewInitialized) {
                    $scope.groups = data.accountGroups;
                    $scope.sources = data.authenticationSources;
                    viewInitialized = true;
                }
                $scope.mappings = data.accountGroupMappings;
            });
        };

        loadMappings();

        $scope.sourceDisplayName = (source) => {
            if (source.key) {
                return `${source.name} (${source.provider})`;
            } else {
                return source.name;
            }
        };

        $scope.filterClear = () => {
            $scope.filterMapping.mapping = "";
            $scope.filterMapping.source = "";
            $scope.filterMapping.group = "";
            $scope.filterLaunch();
        };

        $scope.filterLaunch = () => {
            if ($scope.filterMapping.source) {
                queryVariables.provider = $scope.filterMapping.source.provider;
                queryVariables.source = $scope.filterMapping.source.key;
            } else {
                queryVariables.provider = "";
                queryVariables.source = "";
            }
            queryVariables.mapping = $scope.filterMapping.mapping;
            queryVariables.group = $scope.filterMapping.group;
            loadMappings();
        };

        $scope.formClear = () => {
            $scope.mappingForm.mapping = "";
            $scope.mappingForm.source = "";
            $scope.mappingForm.group = "";
        };

        $scope.createMapping = () => {
            let data = $scope.mappingForm;
            if (data.mapping && data.source && data.group) {
                ot.pageCall($http.post(`rest/group-mappings/${data.source.provider}/${data.source.key}`, {
                    name: data.mapping,
                    group: data.group
                })).then((groupMapping) => {
                    loadMappings();
                    $scope.mappingForm.mapping = "";
                    $scope.mappingForm.group = "";
                });
            }
        };

        $scope.getSuggestedMappings = (token) => {
            if ($scope.mappingForm.source) {
                let source = $scope.mappingForm.source;
                return ot.call($http.get(`/rest/group-mappings/${source.provider}/${source.key}/search/${token}`)).then(names => names);
            } else {
                return [];
            }
        };

        $scope.deleteMapping = (mapping) => {
            ot.pageCall($http.delete(`rest/group-mappings/${mapping.authenticationSource.provider}/${mapping.authenticationSource.key}/${mapping.id}`)).then(loadMappings);
        };
    })
;