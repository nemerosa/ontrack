angular.module('ot.service.buildfilter', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otBuildFilterService', function (ot, $q, $http, otFormService, otNotificationService) {
        const self = {};

        /**
         * Merges the list of available filters from two sources:
         * - the server filters for the current user
         * - the local filters
         *
         * The remote filters have priority over the local ones.
         *
         * Additionally, fetches the available filter forms for the branch.
         */
        self.mergeRemoteAndLocalFilters = (branchId, remoteFilters) => {
            // Loads the local filters for this branch
            const store = self.getStoreForBranch(branchId);
            // Adding the remote filters
            angular.forEach(remoteFilters, buildFilter => {
                store[buildFilter.name] = buildFilter;
            });
            // Flatten the values for the store
            const flatBuildFilterResources = [];
            angular.forEach(store, function (value) {
                flatBuildFilterResources.push(value);
            });
            // OK
            return flatBuildFilterResources;
        };

        /**
         * Loads the list of available filters from two sources:
         * - the server filters for the current user (if logged)
         * - the local filters
         *
         * The remote filters have priority over the local ones.
         *
         * Additionally, fetches the available filter forms for the branch.
         *
         * @deprecated Not loading using REST any longer
         */
        self.loadFilters = function (branch) {
            const d = $q.defer();
            // Loads the local filters for this branch
            const store = self.getStoreForBranch(branch.id);
            // Loads the remote filters & the filter forms for this branch
            ot.call($http.get(branch._buildFilterResources)).then(function (buildFilterResources) {
                angular.forEach(buildFilterResources.resources, function (buildFilterResource) {
                    store[buildFilterResource.name] = buildFilterResource;
                });
                // Flatten the values for the store
                const flatBuildFilterResources = [];
                angular.forEach(store, function (value) {
                    flatBuildFilterResources.push(value);
                });
                // OK
                d.resolve(flatBuildFilterResources);
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
            if (config.buildFilterForm.isPredefined) {
                const d = $q.defer();
                const buildFilterResource = {
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
                        const name = data.name;
                        delete data.name;
                        const buildFilterResource = {
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

        /**
         * Editing a filter
         * @param config.branch Branch
         * @param config.buildFilterResource Build filter to edit
         * @param config.buildFilterForms List of available filter forms
         * @return Promise with the created/editer filter
         */
        self.editBuildFilter = function (config) {
            var buildFilterResource = config.buildFilterResource;
            // Looking for the edition form
            var resourceBuildFilterForm;
            var type = buildFilterResource.type;
            angular.forEach(config.buildFilterForms.resources, function (buildFilterForm) {
                if (buildFilterForm.type == type) {
                    resourceBuildFilterForm = buildFilterForm;
                }
            });
            // Checks for the form
            if (resourceBuildFilterForm) {
                // Copy of the fiter's form
                resourceBuildFilterForm = angular.copy(resourceBuildFilterForm);
                // Name
                buildFilterResource.data.name = buildFilterResource.name;
                // Filling in the form
                otFormService.updateForm(resourceBuildFilterForm.form, buildFilterResource.data);
                // Edit the form
                return self.createBuildFilter({
                    branchId: config.branch.id,
                    buildFilterForm: resourceBuildFilterForm
                }).then(function () {
                    // Loads the current filter
                    var currentFilter = self.getCurrentFilter(config.branch.id);
                    // Storing if saved under the same name
                    if (buildFilterResource._update && buildFilterResource.name == currentFilter.name) {
                        self.saveFilter(config.branch, currentFilter);
                    }
                    // Sharing if saved under the same name
                    if (buildFilterResource.shared && config.branch._buildFilterShare && buildFilterResource.name == currentFilter.name) {
                        self.shareFilter(config.branch, currentFilter);
                    }
                });
            } else {
                otNotificationService.error("The type of this filter appears not to be supported: " + type + ". " +
                    "Consider to delete it.");
                var d = $q.defer();
                d.reject();
                return d.promise;
            }
        };

        self.getCurrentFilter = function (branchId) {
            const json = localStorage.getItem('build_filter_' + branchId + '_current');
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
            // Gets the store for this branch
            var store = self.getStoreForBranch(branch.id);
            // Removes from the store
            delete store[buildFilterResource.name];
            // Saves the store back
            localStorage.setItem(self.getStoreIdForBranch(branch.id),
                JSON.stringify(store)
            );
            // What about the current filter?
            // If selected, stores only its content, not its name
            var currentBuildFilterResource = self.getCurrentFilter(branch.id);
            if (currentBuildFilterResource && currentBuildFilterResource.name && currentBuildFilterResource.name === buildFilterResource.name) {
                currentBuildFilterResource.name = '';
                self.storeCurrent(branch.id, currentBuildFilterResource);
            }
            // Remote delete
            if (buildFilterResource._delete) {
                return ot.call($http.delete(buildFilterResource._delete));
            } else {
                var d = $q.defer();
                d.resolve();
                return d.promise;
            }
        };

        self.saveFilter = function (branch, buildFilterResource) {
            return ot.call(
                $http.post(branch._buildFilterSave, {
                    name: buildFilterResource.name,
                    shared: buildFilterResource.isShared,
                    type: buildFilterResource.type,
                    data: buildFilterResource.data
                })
            );
        };

        self.shareFilter = (branch, buildFilterResource) => {
            buildFilterResource.shared = true;
            return ot.call(
                $http.post(branch._buildFilterShare, {
                    name: buildFilterResource.name,
                    shared: true,
                    type: buildFilterResource.type,
                    data: buildFilterResource.data
                })
            );
        };

        return self;
    })
;