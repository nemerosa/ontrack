angular.module('ot.service.chart', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otChartService', function (ot, $q, $http, otFormService) {
        const self = {};

        self.editChartOptions = (initialOptions) => {
            return otFormService.display({
                form: {
                    fields: [{
                        name: 'interval',
                        type: 'selection',
                        label: "Interval",
                        help: "How far to get the data.",
                        required: true,
                        value: initialOptions.interval,
                        items: [{
                            id: '1w',
                            name: 'One week',
                        }, {
                            id: '2w',
                            name: 'Two weeks',
                        }, {
                            id: '4w',
                            name: 'Four weeks',
                        }, {
                            id: '1m',
                            name: 'One month',
                        }, {
                            id: '2m',
                            name: 'Two months',
                        }, {
                            id: '3m',
                            name: 'Three months',
                        }, {
                            id: '4m',
                            name: 'Four months',
                        }, {
                            id: '6m',
                            name: 'Six months',
                        }, {
                            id: '1y',
                            name: 'One year',
                        }, {
                            id: '2y',
                            name: 'Two years',
                        }],
                        itemId: 'id',
                        itemName: 'name',
                    }, {
                        name: 'period',
                        type: 'selection',
                        label: "Period",
                        help: "Granularity of the statistics.",
                        required: true,
                        value: initialOptions.period,
                        items: [{
                            id: '1d',
                            name: 'One day',
                        }, {
                            id: '2d',
                            name: 'Two days',
                        }, {
                            id: '3d',
                            name: 'Three days',
                        }, {
                            id: '1w',
                            name: 'One week',
                        }, {
                            id: '2w',
                            name: 'Two weeks',
                        }, {
                            id: '4w',
                            name: 'Four weeks',
                        }, {
                            id: '1m',
                            name: 'One month',
                        }, {
                            id: '2m',
                            name: 'Two months',
                        }, {
                            id: '3m',
                            name: 'Three months',
                        }, {
                            id: '4m',
                            name: 'Four months',
                        }, {
                            id: '6m',
                            name: 'Six months',
                        }, {
                            id: '1y',
                            name: 'One year',
                        }, {
                            id: '2y',
                            name: 'Two years',
                        }],
                        itemId: 'id',
                        itemName: 'name',
                    }
                    ]
                },
                title: "Chart options",
                submit: (submitData) => {
                    return submitData;
                }
            });
        };

        return self;
    })
;