angular.module('ontrack.extension.notifications', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('entity-subscriptions', {
            url: '/extension/notifications/entity-subscriptions/{type}/{id}',
            templateUrl: 'extension/notifications/entity-subscriptions.tpl.html',
            controller: 'EntitySubscriptionsCtrl'
        });
    })
    .controller('EntitySubscriptionsCtrl', function ($scope, $stateParams, $http, ot, otFormService, otGraphqlService) {
        const view = ot.view();
        const type = $stateParams.type;
        const id = $stateParams.id;

        let viewInitialized = false;

        const queryVariables = {
            type: type,
            id: id
        };
        const query = `query(
            $type: ProjectEntityType!,
            $id: Int!,
        ) {
            entity(type: $type, id: $id) {
                entityName
                entity {
                    ... on Project {
                        links {
                            _page
                        }
                    }
                    ... on Branch {
                        links {
                            _page
                        }
                    }
                    ... on PromotionLevel {
                        links {
                            _page
                        }
                    }
                    ... on ValidationStamp {
                        links {
                            _page
                        }
                    }
                }
            }
            eventSubscriptions(filter: {
                entity: {
                    type: $type,
                    id: $id
                }
            }) {
                createSubscriptionGranted
                pageItems {
                    id
                    channel
                    channelConfig
                    channelConfigText
                    events
                    keywords
                }
            }
        }`;

        const newSubscription = () => {
            otFormService.display({
                uri: '/extension/notifications/subscription/create',
                title: "New subscription",
                submit: (data) => {
                    console.log("data = ", data);
                    return true;
                }
            });
        };

        $scope.loadingSubscriptions = false;
        const loadSubscriptions = () => {
            $scope.loadingSubscriptions = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.entityInfo = data.entity;
                $scope.subscriptions = data.eventSubscriptions.pageItems;
                if (!viewInitialized) {
                    view.title = `Subscriptions for ${data.entity.entityName}`;
                    const page = data.entity.entity.links._page
                    // Breadcrumbs to the entity
                    let bc = ot.homeBreadcrumbs();
                    bc.push([data.entity.entityName, data.entity.entity.links._page]);
                    view.breadcrumbs = bc;
                    // Close command to the entity
                    view.commands = [];
                    if (data.eventSubscriptions.createSubscriptionGranted) {
                        view.commands.push({
                            id: 'notifications-subscription-create',
                            name: "New subscription",
                            cls: 'ot-command-new',
                            action: newSubscription
                        });
                    }
                    view.commands.push(ot.viewCloseCommand(page.substring(2)));
                    viewInitialized = true;
                }
            }).finally(() => {
                $scope.loadingSubscriptions = false;
            });
        };

        loadSubscriptions();

    })
;