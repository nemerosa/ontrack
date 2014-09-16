angular.module('ot.service.branch.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.dialog.branchSelection'
])
    .service('otBranchCopyService', function ($modal) {
        var self = {};

        function branchCopyFrom() {

        }

        /**
         * Copies another branch's configuration to the given target branch.
         *
         * The user must first select a project and a branch.
         */
        self.copyFrom = function (targetBranch) {
            $modal.open({
                templateUrl: 'app/dialog/dialog.branchSelection.tpl.html',
                controller: 'otDialogBranchSelection'
            });
        };

        return self;
    })
;
