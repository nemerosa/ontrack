angular.module('ot.service.buildfilter', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otBuildFilterService', function (ot, $q, $http, otFormService) {
        var self = {};

        /**
         * Creating a new build filter
         * @param config.branchId ID of the branch
         * @param config.buildFilterForm Build filter form
         * @return Promise with the created filter
         */
        self.createBuildFilter = function (config) {
            var result = {};
            var d = $q.defer();
            otFormService.display({
                title: "New filter",
                form: config.buildFilterForm.form,
                submit: function (filterData) {
                    // Stores locally the filter data if named
                    if (filterData.name) {
                        self.storeForBranch(config, filterData);
                    }
                    // Stores locally as current
                    self.storeCurrent(config, filterData);
                    // Stores for the reuse
                    result.filterData = filterData;
                    // OK
                    return true;
                }
            }).then(function () {
                d.resolve(result.filterData);
            });
            return d.promise;
        };

        self.storeCurrent = function (config, filterData) {
            localStorage.setItem('build_filter_' + config.branchId + '_current',
                JSON.stringify(filterData)
            );
        };

        self.storeForBranch = function (config, filterData) {
            // Gets the store for this branch
            var store = self.getStoreForBranch(config.branchId);
            // Stores the resource in the store
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