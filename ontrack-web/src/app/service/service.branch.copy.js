angular.module('ot.service.branch.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.branch.copy'
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

        return self;
    })
;
