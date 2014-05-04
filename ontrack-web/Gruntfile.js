module.exports = function (grunt) {

    /**
     * List of files, per type, provided via bower in the vendor/ directory.
     */
    var vendor = {
        js: [
            'vendor/jquery/dist/jquery.js',
            'vendor/angular/angular.js',
            'vendor/angular-ui-router/release/angular-ui-router.js',
            'vendor/angular-sanitize/angular-sanitize.js',
            'vendor/angular-bootstrap/ui-bootstrap-tpls.js'
        ],
        fonts: [
            'vendor/bootstrap/fonts/glyphicons-halflings-regular.*'
        ]
    };

    grunt.initConfig({

        /**
         * Reads the `package.json` so that its data is available to the Grunt tasks.
         */
        pkg: grunt.file.readJSON('package.json'),

        /**
         * Cleaning all directories
         */
        clean: [
            'target/dev'
        ],

        /**
         * Copy of files
         */
        copy: {
            /**
             * Direct copy of JS files in `dev` mode
             */
            dev_js: {
                files: [
                    {
                        cwd: 'src',
                        src: [ '**/*.js' ],
                        dest: 'target/dev',
                        expand: true
                    }
                ]
            },
            /**
             * Copy of application assets
             */
            dev_assets: {
                files: [
                    {
                        cwd: 'src/assets',
                        src: [ '**' ],
                        dest: 'target/dev/assets',
                        expand: true
                    }
                ]
            },
            /**
             * Copy of vendor JS files
             */
            dev_vendor_js: {
                files: [
                    {
                        src: vendor.js,
                        dest: 'target/dev',
                        cwd: '.',
                        expand: true
                    }
                ]
            },
            /**
             * Copy of vendor fonts
             */
            dev_vendor_fonts: {
                files: [
                    {
                        src: vendor.fonts,
                        dest: 'target/dev/app/fonts',
                        cwd: '.',
                        expand: true,
                        flatten: true
                    }
                ]
            }
        },

        /**
         * Less transformation.
         */
        less: {
            dev: {
                files: [
                    {
                        'target/dev/assets/main.css': 'src/less/**/*.less'
                    }
                ]
            },
            prod: {
                options: {
                    compress: true
                },
                files: [
                    {
                        'target/tmp/assets/main.css': 'src/less/**/*.less'
                    }
                ]
            }
        },

        /**
         * `ngmin` annotates the sources before minifying. That is, it allows us
         * to code without the array syntax in AngularJS.
         */
        ngmin: {
            prod: {
                files: [
                    {
                        src: [ '**/*.js' ],
                        cwd: 'src',
                        dest: 'target/tmp',
                        expand: true
                    }
                ]
            }
        },

        /**
         * Inclusion of sources
         */
        includeSource: {
            dev: {
                options: {
                    basePath: 'target/dev',
                    baseUrl: ''
                },
                files: {
                    'target/dev/index.html': 'src/index.html'
                }
            }
        }

    });

    /**
     * Loading the plug-ins.
     */

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-ngmin');
    grunt.loadNpmTasks('grunt-html2js');
    grunt.loadNpmTasks('grunt-include-source');

    /**
     * Registering the tasks
     */

    /**
     * The default task is to prod.
     */
    grunt.registerTask('default', [ 'prod' ]);

    /**
     * The `dev` task gets your app ready to run for development and testing.
     */
    grunt.registerTask('dev', [
        'clean',
        // TODO 'jshint',
        'less:dev',
        'copy:dev_assets',
        // TODO 'copy:dev_vendor_assets',
        'copy:dev_js',
        // TODO 'copy:dev_apptpl',
        'copy:dev_vendor_js',
        'copy:dev_vendor_fonts',
        // TODO 'copy:dev_vendorcss',
        // TODO 'html2js:dev',
        'includeSource:dev'
    ]);

    /**
     * The `prod` task gets your app ready for deployment by concatenating and
     * minifying your code.
     */
    grunt.registerTask('prod', [
        'clean',
        'less:prod',
        // TODO 'copy:prod_assets',
        // TODO 'html2js:prod',
        'ngmin:prod'
        // TODO 'concat:prod_js',
        // TODO 'concat:prod_css',
        // TODO 'uglify:prod',
        // TODO 'index:prod',
        // TODO 'copy:prod_vendor_fonts'
    ]);

};