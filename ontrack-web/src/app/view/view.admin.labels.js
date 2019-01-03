angular.module('ot.view.admin.labels', [
    'ui.router',
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-labels', {
            url: '/admin-labels',
            templateUrl: 'app/view/view.admin.labels.tpl.html',
            controller: 'AdminLabelsCtrl'
        });
    })
    .controller('AdminLabelsCtrl', function ($scope, $http, ot, otGraphqlService, otFormService, otAlertService) {
        const view = ot.view();
        view.title = "Labels";
        view.description = "Management of labels";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            {
                id: 'createLabel',
                name: "Create label",
                cls: 'ot-command-new',
                action: createLabel
            },
            ot.viewCloseCommand('/home')
        ];

        const query = `{
            labels {
                id
                category
                name
                description
                color
                computedBy {
                    id
                    name
                }
                links {
                    _update
                    _delete
                }
            }
        }`;

        $scope.loadingLabels = false;

        function loadLabels() {
            $scope.loadingLabels = true;
            otGraphqlService.pageGraphQLCall(query).then(data => {
                $scope.labels = data.labels;
            }).finally(() => {
                $scope.loadingLabels = false;
            });
        }

        loadLabels();

        function createLabel() {
            otFormService.create("/rest/labels/create", "New label").then(loadLabels);
        }

        $scope.updateLabel = function (label) {
            if (label.links._update) {
                otFormService.update(label.links._update, "Update label").then(loadLabels);
            }
        };

        $scope.deleteLabel = function (label) {
            otAlertService.confirm({
                title: "Label deletion",
                message: "Do you really want to delete this label?"
            }).then(function () {
                ot.pageCall($http.delete(label.links._delete)).then(loadLabels);
            });

        };
    })
;