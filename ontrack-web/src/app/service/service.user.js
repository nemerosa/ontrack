angular.module('ot.service.user', [
    'ot.service.core',
    'ot.service.graphql'
])
    .service('otUserService', function (ot, $q, $state, $location, $log, $interval, $http, $rootScope, otNotificationService, otGraphqlService) {
        var self = {};

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[user] init');
            self.loadUser();
        };

        /**
         * Loads the user
         */
        self.getUser = function () {
            return ot.call($http.get('rest/user'));
        };

        /**
         * Reloading the user
         */
        self.loadUser = function () {
            self.getUser().then(
                function success(userResource) {
                    $log.debug('[user] load user: ', userResource);
                    $log.debug('[user] logged: ', userResource.present);
                    // Grouping the actions per groups
                    const ungrouped = [];
                    const groupIndex = {};
                    userResource.actions.forEach(action => {
                        const groupName = action.group;
                        if (groupName) {
                            let group = groupIndex[groupName];
                            if (!group) {
                                group = {
                                    name: groupName,
                                    actions: []
                                };
                                groupIndex[groupName] = group;
                            }
                            action.menuType = 'action';
                            group.actions.push(action);
                        } else {
                            ungrouped.push(action);
                        }
                    });
                    // Sorting the groups
                    const groups = [];
                    for (const [_, group] of Object.entries(groupIndex)) {
                        groups.push(group);
                    }
                    groups.sort((a, b) => {
                        const na = a.name;
                        const nb = b.name;
                        if (na < nb) {
                            return -1;
                        } else if (na > nb) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                    // Inside the groups, sort the actions alphabetically
                    groups.forEach(group => {
                        group.actions.sort((a, b) => {
                            const na = a.name;
                            const nb = b.name;
                            if (na < nb) {
                                return -1;
                            } else if (na > nb) {
                                return 1;
                            } else {
                                return 0;
                            }
                        });
                    });
                    // Building the final menu
                    const menu = {
                        groups: groups,
                        actions: ungrouped,
                        nextUIProperties: userResource.nextUIProperties,
                    };
                    // Puts the menu into the user object
                    userResource.menu = menu;

                    // Saves the user in the root scope
                    $rootScope.user = userResource;
                    // Clears the error
                    otNotificationService.clear();
                },
                function error(message) {
                    $log.debug('[user] load - no user', message);
                    // Removes the user from the scope
                    $rootScope.user = undefined;
                    // Displays a general error
                    otNotificationService.error('Cannot connect. Please try later');
                }
            );
        };

        /**
         * Sets the preferences of the user
         */
        self.setPreferences = (preferences) => {
            const input = {};
            if (preferences.branchViewVsNames !== undefined) {
                input.branchViewVsNames = preferences.branchViewVsNames;
            }
            if (preferences.branchViewVsGroups !== undefined) {
                input.branchViewVsGroups = preferences.branchViewVsGroups;
            }
            otGraphqlService.pageGraphQLCall(`
                mutation(
                  $branchViewVsNames: Boolean,
                  $branchViewVsGroups: Boolean,
                ) {
                  setPreferences(input: {
                    branchViewVsNames: $branchViewVsNames,
                    branchViewVsGroups: $branchViewVsGroups,
                  }) {
                    preferences {
                      branchViewVsNames
                      branchViewVsGroups
                    }
                    errors {
                      message
                    }
                  }
                }
            `, input).finally(() => {
                if (preferences.branchViewVsNames !== undefined) {
                    $rootScope.user.preferences.branchViewVsNames = preferences.branchViewVsNames;
                }
                if (preferences.branchViewVsGroups !== undefined) {
                    $rootScope.user.preferences.branchViewVsGroups = preferences.branchViewVsGroups;
                }
            });
        };

        return self;
    })
;