angular.module('ot.service.branch.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.branch.copy'
])
    .service('otBranchCopyService', function ($modal) {
        var self = {};

        /**
         * Copies another branch's configuration to the given target branch.
         *
         * The user must first select a project and a branch.
         */
        self.copyFrom = function (targetBranch) {
            $modal.open({
                templateUrl: 'app/dialog/dialog.branch.copy.tpl.html',
                controller: 'otDialogBranchCopy',
                resolve: {
                    config: function () {
                        return {
                            targetBranch: targetBranch
                        };
                    }
                }
            }).result.then(function (sourceBranch) {
                    // TODo Does something with the source branch
                });
        };

        return self;
    })
;
