angular.module('ot.extension.svn.changelog', [
    'ui.router'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-changelog', {
            url: '/extension/svn/changelog',
            templateUrl: 'app/extension/svn/svn.changelog.tpl.html',
            controller: 'SVNChangeLogCtrl'
        });
    })
    .controller('SVNChangeLogCtrl', function () {

    })
;