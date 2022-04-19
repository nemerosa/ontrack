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
    .controller('EntitySubscriptionsCtrl', function ($q, $scope, $stateParams, $http, ot, otAlertService, otFormService, otGraphqlService) {
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
                writeSubscriptionGranted
                pageItems {
                    id
                    channel
                    channelConfig
                    channelConfigText
                    events
                    keywords
                    disabled
                }
            }
        }`;

        const deleteSubscriptionQuery = `
            mutation(
                $entityType: ProjectEntityType!,
                $entityId: Int!,
                $id: String!,
            ) {
                deleteSubscription(input: {
                    projectEntity: {
                        type: $entityType,
                        id: $entityId
                    },
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const enableSubscriptionQuery = `
            mutation(
                $entityType: ProjectEntityType!,
                $entityId: Int!,
                $id: String!,
            ) {
                enableSubscription(input: {
                    projectEntity: {
                        type: $entityType,
                        id: $entityId
                    },
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const disableSubscriptionQuery = `
            mutation(
                $entityType: ProjectEntityType!,
                $entityId: Int!,
                $id: String!,
            ) {
                disableSubscription(input: {
                    projectEntity: {
                        type: $entityType,
                        id: $entityId
                    },
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const newSubscriptionQuery = `
            mutation(
                $type: ProjectEntityType!,
                $id: Int!,
                $channel: String!,
                $channelConfig: JSON!,
                $events: [String!]!,
                $keywords: String,
            ) {
              subscribeToEvents(input: {
                projectEntity: {
                    type: $type,
                    id: $id
                },
                channel: $channel,
                channelConfig: $channelConfig,
                events: $events,
                keywords: $keywords, 
              }) {
                errors {
                  message
                }
                subscription {
                  id
                  channel
                  channelConfig
                  channelConfigText
                  events
                  keywords
                  disabled
                }
              }
            }
        `;

        $scope.deleteSubscription = (subscription) => {
            otAlertService.confirm({
                title: "Subscription deletion",
                message: "Do you really want to delete this subscription?"
            }).then(() => {
                otGraphqlService.pageGraphQLCallWithPayloadErrors(deleteSubscriptionQuery, {
                    entityType: type,
                    entityId: id,
                    id: subscription.id
                }, "deleteSubscription").finally(loadSubscriptions);
            });
        };

        $scope.enableSubscription = (subscription) => {
            otGraphqlService.pageGraphQLCallWithPayloadErrors(enableSubscriptionQuery, {
                entityType: type,
                entityId: id,
                id: subscription.id
            }, "enableSubscription").finally(loadSubscriptions);
        };

        $scope.disableSubscription = (subscription) => {
            otGraphqlService.pageGraphQLCallWithPayloadErrors(disableSubscriptionQuery, {
                entityType: type,
                entityId: id,
                id: subscription.id
            }, "disableSubscription").finally(loadSubscriptions);
        };

        const newSubscription = (form) => {
            const newSubscriptionQueryVariables = {
                type: type,
                id: id,
                channel: form.channel.id,
                channelConfig: form.channel.data,
                events: form.events,
                keywords: form.keywords
            };
            const d = $q.defer();
            otGraphqlService.graphQLCall(newSubscriptionQuery, newSubscriptionQueryVariables).then(data => {
                if (data.subscribeToEvents.errors && data.subscribeToEvents.errors.messages) {
                    d.reject(messages[0]);
                } else {
                    $scope.subscriptions.splice(0, 0, data.subscribeToEvents.subscription);
                    d.resolve(true);
                }
            }, messages => {
                d.reject(messages[0]);
            });
            return d.promise;
        };

        const newSubscriptionDialog = () => {
            otFormService.display({
                uri: '/extension/notifications/subscription/create',
                title: "New subscription",
                submit: (data) => {
                    return newSubscription(data);
                }
            });
        };

        $scope.loadingSubscriptions = false;
        const loadSubscriptions = () => {
            $scope.loadingSubscriptions = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.entityInfo = data.entity;
                $scope.subscriptions = data.eventSubscriptions.pageItems;
                $scope.writeSubscriptionGranted = data.eventSubscriptions.writeSubscriptionGranted;
                if (!viewInitialized) {
                    view.title = `Subscriptions for ${data.entity.entityName}`;
                    const page = data.entity.entity.links._page;
                    // Breadcrumbs to the entity
                    let bc = ot.homeBreadcrumbs();
                    bc.push([data.entity.entityName, data.entity.entity.links._page]);
                    view.breadcrumbs = bc;
                    // Close command to the entity
                    view.commands = [];
                    if ($scope.writeSubscriptionGranted) {
                        view.commands.push({
                            id: 'notifications-subscription-create',
                            name: "New subscription",
                            cls: 'ot-command-new',
                            action: newSubscriptionDialog
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
    .config(function ($stateProvider) {
        $stateProvider.state('global-subscriptions', {
            url: '/extension/notifications/global-subscriptions',
            templateUrl: 'extension/notifications/entity-subscriptions.tpl.html',
            controller: 'GlobalSubscriptionsCtrl'
        });
    })
    .controller('GlobalSubscriptionsCtrl', function ($q, $scope, $http, ot, otAlertService, otFormService, otGraphqlService) {
        const view = ot.view();
        view.title = "Global subscriptions";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];
        let viewInitialized = false;

        const queryVariables = {};
        const query = `query GlobalSubscriptions {
            eventSubscriptions(filter: {}) {
                writeSubscriptionGranted
                pageItems {
                    id
                    channel
                    channelConfig
                    channelConfigText
                    events
                    keywords
                    disabled
                }
            }
        }`;

        const deleteSubscriptionQuery = `
            mutation(
                $id: String!,
            ) {
                deleteSubscription(input: {
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const enableSubscriptionQuery = `
            mutation(
                $id: String!,
            ) {
                enableSubscription(input: {
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const disableSubscriptionQuery = `
            mutation(
                $id: String!,
            ) {
                disableSubscription(input: {
                    id: $id
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const newSubscriptionQuery = `
            mutation(
                $channel: String!,
                $channelConfig: JSON!,
                $events: [String!]!,
                $keywords: String,
            ) {
              subscribeToEvents(input: {
                channel: $channel,
                channelConfig: $channelConfig,
                events: $events,
                keywords: $keywords, 
              }) {
                errors {
                  message
                }
                subscription {
                  id
                  channel
                  channelConfig
                  channelConfigText
                  events
                  keywords
                  disabled
                }
              }
            }
        `;

        $scope.deleteSubscription = (subscription) => {
            otAlertService.confirm({
                title: "Subscription deletion",
                message: "Do you really want to delete this subscription?"
            }).then(() => {
                otGraphqlService.pageGraphQLCallWithPayloadErrors(deleteSubscriptionQuery, {
                    id: subscription.id
                }, "deleteSubscription").finally(loadSubscriptions);
            });
        };

        $scope.enableSubscription = (subscription) => {
            otGraphqlService.pageGraphQLCallWithPayloadErrors(enableSubscriptionQuery, {
                id: subscription.id
            }, "enableSubscription").finally(loadSubscriptions);
        };

        $scope.disableSubscription = (subscription) => {
            otGraphqlService.pageGraphQLCallWithPayloadErrors(disableSubscriptionQuery, {
                id: subscription.id
            }, "disableSubscription").finally(loadSubscriptions);
        };

        const newSubscription = (form) => {
            const newSubscriptionQueryVariables = {
                channel: form.channel.id,
                channelConfig: form.channel.data,
                events: form.events,
                keywords: form.keywords
            };
            const d = $q.defer();
            otGraphqlService.graphQLCall(newSubscriptionQuery, newSubscriptionQueryVariables).then(data => {
                if (data.subscribeToEvents.errors && data.subscribeToEvents.errors.messages) {
                    d.reject(messages[0]);
                } else {
                    $scope.subscriptions.splice(0, 0, data.subscribeToEvents.subscription);
                    d.resolve(true);
                }
            }, messages => {
                d.reject(messages[0]);
            });
            return d.promise;
        };

        const newSubscriptionDialog = () => {
            otFormService.display({
                uri: '/extension/notifications/subscription/create',
                title: "New subscription",
                submit: (data) => {
                    return newSubscription(data);
                }
            });
        };

        $scope.loadingSubscriptions = false;
        const loadSubscriptions = () => {
            $scope.loadingSubscriptions = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.subscriptions = data.eventSubscriptions.pageItems;
                $scope.writeSubscriptionGranted = data.eventSubscriptions.writeSubscriptionGranted;
                if (!viewInitialized) {
                    if ($scope.writeSubscriptionGranted) {
                        view.commands.splice(0, 0, {
                            id: 'notifications-subscription-create',
                            name: "New subscription",
                            cls: 'ot-command-new',
                            action: newSubscriptionDialog
                        });
                    }
                    viewInitialized = true;
                }
            }).finally(() => {
                $scope.loadingSubscriptions = false;
            });
        };

        loadSubscriptions();

    })
    .config(function ($stateProvider) {
        $stateProvider.state('notification-recordings', {
            url: '/extension/notifications/notification-recordings',
            templateUrl: 'extension/notifications/notification-recordings.tpl.html',
            controller: 'NotificationRecordingsCtrl'
        });
    })
    .controller('NotificationRecordingsCtrl', function ($scope, ot, otAlertService, otGraphqlService) {
        const view = ot.view();
        view.title = "Notification recordings";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        const query = `
            query NotificationRecordings(
                $offset: Int!,
                $size: Int!,
                $resultType: NotificationResultType,
            ) {
                notificationResultType: __type(name: "NotificationResultType") {
                  enumValues {
                    name
                  }
                }
                notificationRecords(
                    offset: $offset,
                    size: $size,
                    resultType: $resultType,
                ) {
                    pageInfo {
                      totalSize
                      nextPage {
                        offset
                        size
                      }
                      previousPage {
                        offset
                        size
                      }
                    }
                    pageItems {
                        timestamp
                        channel
                        channelConfig
                        event
                        result {
                            type
                            id
                            message
                        }
                    }
                }
            }
        `;

        const pageSize = 10;
        const queryVars = {
            offset: 0,
            size: pageSize,
            resultType: null,
        };

        const clearRecordsQuery = `
            mutation {
                deleteNotificationRecords(input: {}) {
                    errors {
                        message
                    }
                }
            }
        `;

        $scope.notificationResultTypeValues = [];

        $scope.filter = {
            notificationResultType: null
        };

        $scope.loadingRecords = false;
        const loadRecords = () => {
            $scope.loadingRecords = true;
            if ($scope.filter.notificationResultType) {
                queryVars.resultType = $scope.filter.notificationResultType;
            } else {
                queryVars.resultType = null;
            }
            otGraphqlService.pageGraphQLCall(query, queryVars).then(data => {
                $scope.navigation = data.notificationRecords.pageInfo;
                $scope.records = data.notificationRecords.pageItems;
                if ($scope.notificationResultTypeValues.length === 0) {
                    data.notificationResultType.enumValues.forEach(value => {
                        $scope.notificationResultTypeValues.push(value.name);
                    });
                }
            }).finally(() => {
                $scope.loadingRecords = false;
            });
        };

        loadRecords();

        $scope.refresh = loadRecords;

        $scope.switchPage = (pageRequest) => {
            queryVars.offset = pageRequest.offset;
            queryVars.size = pageSize;
            loadRecords();
        };

        $scope.deleteRecords = () => {
            otAlertService.confirm({
                title: "Record deletion",
                message: "Are you sure to delete ALL notification records?"
            }).then(() => {
                otGraphqlService.pageGraphQLCall(clearRecordsQuery);
            }).then(loadRecords);
        };
    })
    .config(function ($stateProvider) {
        $stateProvider.state('webhooks', {
            url: '/extension/notifications/webhooks',
            templateUrl: 'extension/notifications/webhooks.tpl.html',
            controller: 'WebhooksCtrl'
        });
    })
    .controller('WebhooksCtrl', function ($q, $scope, $http, ot, otAlertService, otFormService, otGraphqlService) {
        const view = ot.view();
        view.title = "Webhooks";
        view.breadcrumbs = ot.homeBreadcrumbs();

        const query = `
            query Webhooks {
                webhooks {
                    name
                    enabled
                    url
                    timeoutSeconds
                    authenticationType
                }
            }
        `;

        const newWebhookQuery = `
            mutation CreateWebhook(
                $name: String!,
                $enabled: Boolean!,
                $url: String!,
                $timeoutSeconds: Long!,
                $authenticationType: String!,
                $authenticationConfig: JSON!,
            ) {
                createWebhook(input: {
                    name: $name,
                    enabled: $enabled,
                    url: $url,
                    timeoutSeconds: $timeoutSeconds,
                    authenticationType: $authenticationType,
                    authenticationConfig: $authenticationConfig,
                }) {
                    webhook {
                        name
                        enabled
                        url
                        timeoutSeconds
                        authenticationType
                    }
                    errors {
                        message
                    }
                }
            }
        `;

        const deleteWebhookQuery = `
            mutation DeleteWebhook(
                $name: String!,
            ) {
                deleteWebhook(input: {
                    name: $name,
                }) {
                    errors {
                        message
                    }
                }
            }
        `;

        const newWebhook = (form) => {
            const newWebhookQueryVariables = {
                name: form.name,
                enabled: form.enabled,
                url: form.url,
                timeoutSeconds: form.timeoutSeconds,
                authenticationType: form.authentication.id,
                authenticationConfig: form.authentication.data
            };
            const d = $q.defer();
            otGraphqlService.graphQLCall(newWebhookQuery, newWebhookQueryVariables).then(data => {
                if (data.createWebhook.errors && data.createWebhook.errors.messages) {
                    d.reject(messages[0]);
                } else {
                    $scope.webhooks.splice(0, 0, data.createWebhook.webhook);
                    d.resolve(true);
                }
            }, messages => {
                d.reject(messages[0]);
            });
            return d.promise;
        };

        const newWebhookDialog = () => {
            otFormService.display({
                uri: '/extension/notifications/webhook/create',
                title: "New webhook",
                submit: (data) => {
                    return newWebhook(data);
                }
            });
        };

        view.commands = [
            {
                id: 'webhook-create',
                name: "New webhook",
                cls: 'ot-command-new',
                action: newWebhookDialog
            },
            ot.viewCloseCommand('/home')
        ];

        $scope.loadingWebhooks = false;
        const loadWebhooks = () => {
            $scope.loadingWebhooks = true;
            otGraphqlService.pageGraphQLCall(query).then(data => {
                $scope.webhooks = data.webhooks;
            }).finally(() => {
                $scope.loadingWebhooks = false;
            });
        };

        loadWebhooks();

        $scope.deleteWebhook = (webhook) => {
            otAlertService.confirm({
                title: "Webhook deletion",
                message: `Are you sure to delete the [${webhook.name}] webhook?`
            }).then(() =>
                otGraphqlService.pageGraphQLCall(deleteWebhookQuery, {name: webhook.name})
            ).then(loadWebhooks);
        };
    })
    .config(function ($stateProvider) {
        $stateProvider.state('webhook-exchanges', {
            url: '/extension/notifications/webhooks/{name}',
            templateUrl: 'extension/notifications/webhook-exchanges.tpl.html',
            controller: 'WebhookExchangesCtrl'
        });
    })
    .controller('WebhookExchangesCtrl', function ($scope, $stateParams, $http, ot, otGraphqlService) {
        const view = ot.view();
        const name = $stateParams.name;

        view.title = `Webhook deliveries for ${name}`;
        let bc = ot.homeBreadcrumbs();
        bc.push(["webhooks", "#/extension/notifications/webhooks"]);
        view.breadcrumbs = bc;
        view.commands = [
            ot.viewCloseCommand("/extension/notifications/webhooks")
        ];

        $scope.filter = {
            payloadKeyword: '',
            responseCode: '',
        };

        const query = `
            query(
                $webhook: String!,
                $offset: Int!,
                $size: Int!,
                $payloadKeyword: String,
                $responseCode: Int,
            ) {
                webhooks(name: $webhook) {
                    exchanges(
                        offset: $offset,
                        size: $size,
                        filter: {
                            payloadKeyword: $payloadKeyword,
                            responseCode: $responseCode,
                        }
                    ) {
                        pageInfo {
                          totalSize
                          nextPage {
                            offset
                            size
                          }
                          previousPage {
                            offset
                            size
                          }
                        }
                        pageItems {
                            uuid
                            webhook
                            request {
                                timestamp
                                type
                                payload
                            }
                            response {
                                timestamp
                                code
                                payload
                            }
                            stack
                        }
                    }
                }
            }
        `;

        const pageSize = 10;
        const queryVariables = {
            webhook: name,
            offset: 0,
            size: pageSize,
            payloadKeyword: null,
            responseCode: null,
        };

        $scope.loadingDeliveries = false;

        const loadDeliveries = () => {
            $scope.loadingDeliveries = true;

            if ($scope.filter.responseCode) {
                queryVariables.responseCode = $scope.filter.responseCode;
            } else {
                queryVariables.responseCode = null;
            }

            if ($scope.filter.payloadKeyword) {
                queryVariables.payloadKeyword = $scope.filter.payloadKeyword;
            } else {
                queryVariables.payloadKeyword = null;
            }

            otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
                $scope.navigation = data.webhooks[0].exchanges.pageInfo;
                $scope.deliveries = data.webhooks[0].exchanges.pageItems;
            }).finally(() => {
                $scope.loadingDeliveries = false;
            });
        };

        loadDeliveries();

        $scope.toggleDeliveryDetails = (delivery) => {
            delivery.details = !delivery.details;
        };

        $scope.switchPage = (page) => {
            queryVariables.offset = page.offset;
            queryVariables.size = pageSize;
            loadDeliveries();
        };

        $scope.submitFilter = () => {
            loadDeliveries();
        };

        $scope.resetFilter = () => {
            $scope.filter.payloadKeyword = '';
            $scope.filter.responseCode = '';
            loadDeliveries();
        };
    })
;