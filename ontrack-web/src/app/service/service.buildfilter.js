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
                        // TODO self.storeForBranch(config, filterData);
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

        return self;
    })
;