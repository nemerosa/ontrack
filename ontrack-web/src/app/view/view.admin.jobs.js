angular.module('ot.view.admin.jobs', [
    'ui.router',
    'ot.service.core',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-jobs', {
            url: '/admin-jobs',
            templateUrl: 'app/view/view.admin.jobs.tpl.html',
            controller: 'AdminJobsCtrl'
        });
    })
    .controller('AdminJobsCtrl', function ($scope, $http, $location, ot, otAlertService, otTaskService, otNotificationService) {
        const view = ot.view();
        view.title = "System jobs";
        view.description = "Tools for the management of system background jobs";

        // Current filter
        $scope.jobFilter = {
            state: undefined,
            category: undefined,
            type: undefined,
            description: '',
            errorOnly: false,
            timeoutOnly: false,
        };

        // Current page
        $scope.page = {};

        // Category filter selection
        $scope.setJobFilterCategory = (category) => {
            if ($scope.jobFilter.category !== category) {
                $scope.jobFilter.category = category;
                $scope.jobFilter.type = undefined;
            }
        };

        // Clearing the filter
        $scope.clearJobFilter = () => {
            $scope.jobFilter = {
                state: undefined,
                category: undefined,
                type: undefined,
                description: '',
                errorOnly: false,
                timeoutOnly: false,
            };
            $location.hash('');
            loadJobs();
        };

        // Navigation: previous page
        $scope.previousPage = () => {
            if ($scope.jobs.pagination.prev) {
                $scope.loadingJobs = true;
                ot.pageCall($http.get($scope.jobs.pagination.prev))
                    .then(jobs => {
                        $scope.jobs = jobs;
                        $scope.page= jobs.pagination;
                    })
                    .finally(() => {
                        $scope.loadingJobs = false;
                    });
            }
        };

        // Navigation: next page
        $scope.nextPage = () => {
            if ($scope.jobs.pagination.next) {
                $scope.loadingJobs = true;
                ot.pageCall($http.get($scope.jobs.pagination.next))
                    .then(jobs => {
                        $scope.jobs = jobs;
                        $scope.page= jobs.pagination;
                    })
                    .finally(() => {
                        $scope.loadingJobs = false;
                    });
            }
        };

        // Search button
        $scope.filterJobs = () => {
            // Filter in the page hash
            const filter = {};
            if ($scope.jobFilter.state) filter.state = $scope.jobFilter.state.name;
            if ($scope.jobFilter.category) filter.category = $scope.jobFilter.category.name;
            if ($scope.jobFilter.type) filter.type = $scope.jobFilter.type.name;
            if ($scope.jobFilter.description) filter.category = $scope.jobFilter.description;
            if ($scope.jobFilter.errorOnly) filter.errorOnly = $scope.jobFilter.errorOnly;
            if ($scope.jobFilter.timeoutOnly) filter.timeoutOnly = $scope.jobFilter.timeoutOnly;
            $location.hash(JSON.stringify(filter));
            // Loading the jobs
            loadJobs();
        };

        // Loads the jobs
        $scope.loadJobs = () => {
            $scope.page = {};
            loadJobs();
        };

        let hashFilterLoaded = false;

        function loadJobs() {
            $scope.loadingJobs = true;
            ot.pageCall($http.get('rest/admin/jobs/filter'))
                .then(jobFilterResources => {
                    $scope.jobFilterResources = jobFilterResources;

                    // Preloads the filter from the hash
                    if (!hashFilterLoaded) {
                        hashFilterLoaded = true;
                        const hashFilter = $location.hash();
                        if (hashFilter) {
                            const filter = JSON.parse(hashFilter);
                            if (filter) {
                                $scope.jobFilter.state = jobFilterResources.states.find(state => state.name === filter.state);
                                $scope.jobFilter.category = jobFilterResources.categories.find(category => category.name === filter.category);
                                if ($scope.jobFilter.category) {
                                    $scope.jobFilter.type = jobFilterResources.types[$scope.jobFilter.category.name].find(type => type.name === filter.type);
                                }
                                $scope.jobFilter.errorOnly = filter.errorOnly === true;
                                $scope.jobFilter.timeoutOnly = filter.timeoutOnly === true;
                            }
                        }
                    }

                    // Parameters
                    const params = {};
                    params.state = $scope.jobFilter.state ? $scope.jobFilter.state.name : undefined;
                    params.category = $scope.jobFilter.category ? $scope.jobFilter.category.name : undefined;
                    params.type = $scope.jobFilter.type ? $scope.jobFilter.type.name : undefined;
                    params.description = $scope.jobFilter.description ? $scope.jobFilter.description : undefined;
                    params.errorOnly = $scope.jobFilter.errorOnly ? $scope.jobFilter.errorOnly : undefined;
                    params.timeoutOnly = $scope.jobFilter.timeoutOnly ? $scope.jobFilter.timeoutOnly : undefined;
                    params.offset = $scope.page.offset ? $scope.page.offset : 0;
                    params.count = $scope.page.count ? $scope.page.count : 30;
                    // Call
                    return ot.pageCall($http.get('rest/admin/jobs', {
                        params: params
                    }));
                })
                .then(jobs => {
                    $scope.jobs = jobs;
                    $scope.page= jobs.pagination;
                    view.commands = [
                        ot.viewCloseCommand('/home')
                    ];
                })
                .finally(() => {
                    $scope.loadingJobs = false;
                });
        }

        // Initialisation
        loadJobs();

        const interval = 10 * 1000; // 10 seconds
        otTaskService.register('Admin Console Load Jobs', loadJobs, interval);

        // Duration formatting
        $scope.jobDuration = function (ms) {
            return moment.duration(ms, 'ms').humanize();
        };

        // Launching a job
        $scope.launchJob = function (job) {
            ot.pageCall($http.post(job._launch)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was launched.");
                } else {
                    otNotificationService.warning("Job is already running or is disabled and could not be launched.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Pausing all jobs
        $scope.pauseJobs = function () {
            if ($scope.jobs && $scope.jobs._pause) {
                ot.pageCall($http.put($scope.jobs._pause)).then(loadJobs);
            }
        };

        // Resuming all jobs
        $scope.resumeJobs = function () {
            if ($scope.jobs && $scope.jobs._resume) {
                ot.pageCall($http.put($scope.jobs._resume)).then(loadJobs);
            }
        };

        // Pausing a job
        $scope.pauseJob = function (job) {
            ot.pageCall($http.post(job._pause)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was paused.");
                } else {
                    otNotificationService.warning("Job could not be paused.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Resuming a job
        $scope.resumeJob = function (job) {
            ot.pageCall($http.post(job._resume)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job schedule was resumed.");
                } else {
                    otNotificationService.warning("Job could not be resumed.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Deletes a job
        $scope.deleteJob = function (job) {
            ot.pageCall($http.delete(job._delete)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job schedule was removed.");
                } else {
                    otNotificationService.warning("Job could not be removed.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Stops a job
        $scope.stopJob = function (job) {
            ot.pageCall($http.delete(job._stop)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was stopped.");
                } else {
                    otNotificationService.warning("Job could not be stopped.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Pause & resume all jobs

        $scope.pauseAllSelected = function () {
            $scope.jobs.resources.forEach(function (job) {
                if ($scope.jobFilter(job) && job._pause) {
                    $scope.pauseJob(job);
                }
            });
        };

        $scope.resumeAllSelected = function () {
            $scope.jobs.resources.forEach(function (job) {
                if ($scope.jobFilter(job.id) && job._resume) {
                    $scope.resumeJob(job);
                }
            });
        };

        $scope.jobKeysVisible = false;

        $scope.toggleJobKeys = () => {
            $scope.jobKeysVisible = !$scope.jobKeysVisible;
        };

    })
;