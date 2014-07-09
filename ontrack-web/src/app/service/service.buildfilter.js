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
                    // TODO Stores locally the filter data if named
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

        return self;
    })
;