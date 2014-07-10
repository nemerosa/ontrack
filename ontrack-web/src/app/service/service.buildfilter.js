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
            return otFormService.display({
                title: "New filter",
                form: config.buildFilterForm.form,
                submit: function (filterData) {
                    // Stores locally the filter data if named
                    if (filterData.name) {
                        self.storeForBranch(config, filterData);
                    }
                    // Stores locally as current
                    self.storeCurrent(config.branchId, filterData);
                    // OK
                    return true;
                }
            });
        };

        self.getCurrentFilter = function (branchId) {
            var json = localStorage.getItem('build_filter_' + branchId + '_current');
            if (json) {
                return JSON.parse(json);
            } else {
                return undefined;
            }
        };

        self.storeCurrent = function (branchId, filterData) {
            localStorage.setItem('build_filter_' + branchId + '_current',
                JSON.stringify(filterData)
            );
        };

        self.eraseCurrent = function (branchId) {
            localStorage.removeItem('build_filter_' + branchId + '_current');
        };

        self.storeForBranch = function (config, filterData) {
            // Gets the store for this branch
            var store = self.getStoreForBranch(config.branchId);
            // Stores the resource in the store
            filterData.type = config.buildFilterForm.type;
            store[filterData.name] = {
                name: filterData.name,
                type: config.buildFilterForm.type,
                filter: filterData
            };
            // Saves the store back
            localStorage.setItem(self.getStoreIdForBranch(config.branchId),
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

        return self;
    })
;