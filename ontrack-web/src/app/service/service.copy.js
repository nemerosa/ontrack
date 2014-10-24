angular.module('ot.service.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.branch.copy',
    'ot.dialog.branch.bulk',
    'ot.dialog.branch.clone',
    'ot.dialog.project.clone'
])
    .service('otCopyService', function ($modal, $http, ot, otFormService) {
        var self = {};

        /**
         * Copies another branch's configuration to the given target branch.
         *
         * The user must first select a project and a branch.
         */
        self.copyFrom = function (targetBranch) {
            return $modal.open({
                templateUrl: 'app/dialog/dialog.branch.copy.tpl.html',
                controller: 'otDialogBranchCopy',
                resolve: {
                    config: function () {
                        return {
                            targetBranch: targetBranch,
                            submit: function (copy) {
                                var request = {
                                    sourceBranchId: copy.branch.id,
                                    replacements: copy.replacements
                                };
                                return ot.call($http.put(targetBranch._copy, request));
                            }
                        };
                    }
                }
            }).result;
        };

        /**
         * Bulk update of the branch.
         */
        self.bulkUpdate = function (branch) {
            return $modal.open({
                templateUrl: 'app/dialog/dialog.branch.bulk.tpl.html',
                controller: 'otDialogBranchBulk',
                resolve: {
                    config: function () {
                        return {
                            branch: branch,
                            submit: function (data) {
                                var request = {
                                    replacements: data.replacements
                                };
                                return ot.call($http.put(branch._bulkUpdate, request));
                            }
                        };
                    }
                }
            }).result;
        };

        /**
         * Clones a branch into another one.
         */
        self.cloneBranch = function (sourceBranch) {
            return $modal.open({
                templateUrl: 'app/dialog/dialog.branch.clone.tpl.html',
                controller: 'otDialogBranchClone',
                resolve: {
                    config: function () {
                        return {
                            sourceBranch: sourceBranch,
                            submit: function (specs) {
                                var request = {
                                    name: specs.name,
                                    replacements: specs.replacements
                                };
                                return ot.call($http.post(sourceBranch._clone, request));
                            }
                        };
                    }
                }
            }).result;
        };

        /**
         * Clones a project into another one.
         */
        self.cloneProject = function (sourceProject) {
            return $modal.open({
                templateUrl: 'app/dialog/dialog.project.clone.tpl.html',
                controller: 'otDialogProjectClone',
                resolve: {
                    config: function () {
                        return {
                            sourceProject: sourceProject,
                            submit: function (specs) {
                                var request = {
                                    name: specs.name,
                                    sourceBranchId: specs.branch.id,
                                    replacements: specs.replacements
                                };
                                return ot.call($http.post(sourceProject._clone, request));
                            }
                        };
                    }
                }
            }).result;
        };

        return self;
    })
;
