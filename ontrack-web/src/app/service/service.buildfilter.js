angular.module('ot.service.buildfilter', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otBuildFilterService', function (ot, $q, $http, otFormService) {
        var self = {};

        /**
         * Loads the list of available filters from two sources:
         * - the server filters for the current user (if logged)
         * - the local filters
         *
         * The remote filters have priority over the local ones.
         *
         * Additionally, fetches the available filter forms for the branch.
         */
        self.loadFilters = function (branch) {
            var d = $q.defer();
            // Result to define
            var filters = {
            };
            // Loads the local filters for this branch
            var store = self.getStoreForBranch(branch.id);
            // Loads the remote filters & the filter forms for this branch
            ot.call($http.get(branch._buildFilters)).then(function (buildFilters) {
                // List of forms
                filters.buildFilterForms = buildFilters.buildFilterForms;
                // Combine local & remote filters
                angular.forEach(store, function (filter) {
                    filter.local = true;
                });
                angular.forEach(buildFilters.buildFilterResources, function (buildFilterResource) {
                    store[buildFilterResource.name] = buildFilterResource;
                });
                // Gets the values for the store
                filters.store = [];
                angular.forEach(store, function (value) {
                    filters.store.push(value);
                });
                // OK
                d.resolve(filters);
            });
            // OK
            return d.promise;
        };

        /**
         * Creating a new build filter
         * @param config.branchId ID of the branch
         * @param config.buildFilterForm Build filter form
         * @return Promise with the created filter
         */
        self.createBuildFilter = function (config) {
            if (config.buildFilterForm.predefined) {
                var d = $q.defer();
                var buildFilterResource = {
                    name: config.buildFilterForm.typeName,
                    type: config.buildFilterForm.type,
                    data: {}
                };
                self.storeCurrent(config.branchId, buildFilterResource);
                d.resolve(buildFilterResource);
                return d.promise;
            } else {
                return otFormService.display({
                    title: "New filter",
                    form: config.buildFilterForm.form,
                    submit: function (data) {
                        var name = data.name;
                        delete data.name;
                        var buildFilterResource = {
                            name: name,
                            type: config.buildFilterForm.type,
                            data: data
                        };
                        // Stores locally the filter data if named
                        if (name) {
                            self.storeForBranch(config.branchId, buildFilterResource);
                        }
                        // Stores locally as current
                        self.storeCurrent(config.branchId, buildFilterResource);
                        // OK
                        return true;
                    }
                });
            }
        };

        self.getCurrentFilter = function (branchId) {
            var json = localStorage.getItem('build_filter_' + branchId + '_current');
            if (json) {
                return JSON.parse(json);
            } else {
                return undefined;
            }
        };

        self.storeCurrent = function (branchId, buildFilterResource) {
            localStorage.setItem('build_filter_' + branchId + '_current',
                JSON.stringify(buildFilterResource)
            );
        };

        self.eraseCurrent = function (branchId) {
            localStorage.removeItem('build_filter_' + branchId + '_current');
        };

        self.storeForBranch = function (branchId, buildFilterResource) {
            // Gets the store for this branch
            var store = self.getStoreForBranch(branchId);
            // Stores the resource in the store
            store[buildFilterResource.name] = buildFilterResource;
            // Saves the store back
            localStorage.setItem(self.getStoreIdForBranch(branchId),
                JSON.stringify(store)
            );
        };

        self.getStoreForBranch = function (branchId) {
            var json = localStorage.getItem(self.getStoreIdForBranch(branchId));
            if (json) {
                return JSON.parse(json);
            } else {
                return {};
            }
        };

        self.getStoreIdForBranch = function (branchId) {
            return 'build_filter_' + branchId;
        };

        self.removeFilter = function (branch, buildFilterResource) {
            if (buildFilterResource.local) {
                // Gets the store for this branch
                var store = self.getStoreForBranch(branch.id);
                // Removes from the store
                delete store[buildFilterResource.name];
                // Saves the store back
                localStorage.setItem(self.getStoreIdForBranch(branch.id),
                    JSON.stringify(store)
                );
            } else {
                // TODO Remote delete
            }
            // What about the current filter?
            // If selected, stores only its content, not its name
            var currentBuildFilterResource = self.getCurrentFilter(branch.id);
            if (currentBuildFilterResource && currentBuildFilterResource.name && currentBuildFilterResource.name == buildFilterResource.name) {
                currentBuildFilterResource.name = '';
                self.storeCurrent(branch.id, currentBuildFilterResource);
            }
        };

        return self;
    })
;