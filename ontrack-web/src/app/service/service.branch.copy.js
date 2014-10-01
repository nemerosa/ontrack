angular.module('ot.service.branch.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.branch.copy',
    'ot.dialog.branch.clone'
])
    .service('otBranchCopyService', function ($modal, $http, ot) {
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
                            targetBranch: targetBranch
                        };
                    }
                }
            }).result.then(function (copy) {
                    var request = {
                        sourceBranchId: copy.branch.id,
                        propertyReplacements: copy.propertyReplacements,
                        promotionLevelReplacements: copy.promotionLevelReplacements,
                        validationStampReplacements: copy.validationStampReplacements
                    };
                    return ot.pageCall($http.put(targetBranch._copy, request));
                });
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
                            sourceBranch: sourceBranch
                        };
                    }
                }
            }).result.then(function (specs) {
                    var request = {
                        name: specs.name,
                        propertyReplacements: specs.propertyReplacements,
                        promotionLevelReplacements: specs.promotionLevelReplacements,
                        validationStampReplacements: specs.validationStampReplacements
                    };
                    return ot.pageCall($http.post(sourceBranch._clone, request));
                });
        };

        return self;
    })
;
