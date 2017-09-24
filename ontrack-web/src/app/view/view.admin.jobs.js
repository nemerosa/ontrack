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
    .controller('AdminJobsCtrl', function ($scope, $http, ot, otAlertService, otTaskService, otNotificationService) {
        const view = ot.view();
        view.title = "System jobs";
        view.description = "Tools for the management of system background jobs";

        // Current filter
        $scope.jobFilter = {
            state: undefined
        };

        // Loads the jobs
        function loadJobs() {
            $scope.loadingJobs = true;
            ot.pageCall($http.get('admin/jobs/filter'))
                .then(jobFilterResources => {
                    $scope.jobFilterResources = jobFilterResources;
                    // TODO Current job filter and pagination
                    return ot.pageCall($http.get('admin/jobs'));
                })
                .then(jobs => {
                    $scope.jobs = jobs;
                    view.commands = [
                        ot.viewApiCommand(jobs._self),
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
        $scope.pauseJobs = function() {
            if ($scope.jobs && $scope.jobs._pause) {
                ot.pageCall($http.put($scope.jobs._pause)).then(loadJobs);
            }
        };

        // Resuming all jobs
        $scope.resumeJobs = function() {
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

    })
;