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
            'target/dev',
            'target/prod',
            'src/assets/main.css'
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
            },
            /**
             * PROD: Copy of application assets
             */
            prod_assets: {
                files: [
                    {
                        cwd: 'src/assets',
                        src: [ '**' ],
                        dest: 'target/prod/assets',
                        expand: true
                    }
                ]
            },
            /**
             * PROD: Copy of vendor JS files
             */
            prod_vendor_js: {
                files: [
                    {
                        src: vendor.js,
                        dest: 'target/include',
                        cwd: '.',
                        expand: true
                    }
                ]
            },
            /**
             * PROD: Copy of vendor fonts
             */
            prod_vendor_fonts: {
                files: [
                    {
                        src: vendor.fonts,
                        dest: 'target/prod/assets/fonts',
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
                        'target/include/assets/main.css': 'src/less/**/*.less'
                    }
                ]
            }
        },

        /**
         * `jshint` defines the rules of our linter as well as which files we
         * should check. This file, all javascript sources, and all our unit tests
         * are linted based on the policies listed in `options`. But we can also
         * specify exclusionary patterns by prefixing them with an exclamation
         * point (!); this is useful when code comes from a third party but is
         * nonetheless inside `src/`.
         */
        jshint: {
            src: [
                'src/app/**/*.js'
            ],
            gruntfile: [
                'Gruntfile.js'
            ],
            options: {
                curly: true,
                immed: true,
                newcap: true,
                noarg: true,
                sub: true,
                boss: true,
                eqnull: true
            },
            globals: {}
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
                        dest: 'target/include',
                        expand: true
                    }
                ]
            }
        },

        /**
         * `grunt concat` concatenates multiple source files into a single file.
         */
        concat: {
            /**
             * The `prod_js` target is the concatenation of our application source
             * code and all specified vendor source code into a single file.
             */
            prod_js: {
                src: [
                    'target/include/**/*.js'
                ],
                dest: 'target/prod/assets/<%= pkg.name %>-<%= pkg.version %>.js'
            },
            /**
             * The `prod_css` target is the concatenation of our application source
             * code and all specified vendor source code into a single file.
             */
            prod_css: {
                src: [
                    'target/include/**/*.css'
                ],
                dest: 'target/prod/assets/<%= pkg.name %>-<%= pkg.version %>.css'
            }
        },

        /**
         * Minify the sources!
         */
        uglify: {
            prod: {
                files: {
                    '<%= concat.prod_js.dest %>': '<%= concat.prod_js.dest %>'
                }
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
            },
            prod: {
                options: {
                    basePath: 'target/prod',
                    baseUrl: ''
                },
                files: {
                    'target/prod/index.html': 'src/index.html'
                }
            }
        },

        /**
         * And for rapid development, we have a watch set up that checks to see if
         * any of the files listed below change, and then to execute the listed
         * tasks when they do. This just saves us from having to type "grunt" into
         * the command-line every time we want to see what we're working on; we can
         * instead just leave "grunt watch" running in a background terminal. Set it
         * and forget it, as Ron Popeil used to tell us.
         *
         * But we don't need the same thing to happen for all the files.
         */
        delta: {
            /**
             * By default, we want the Live Reload to work for all tasks; this is
             * overridden in some tasks (like this file) where browser resources are
             * unaffected. It runs by default on port 35729, which your browser
             * plugin should auto-detect.
             */
            options: {
                livereload: true
            },

            /**
             * When the Gruntfile changes, we just want to lint it. In fact, when
             * your Gruntfile changes, it will automatically be reloaded!
             */
            gruntfile: {
                files: 'Gruntfile.js',
                tasks: [ 'jshint:gruntfile' ],
                options: {
                    livereload: false
                }
            },

            /**
             * When our JavaScript source files change, we want to run lint them and
             * run our unit tests.
             */
            jssrc: {
                files: [
                    'src/**/*.js '
                ],
                tasks: [ 'jshint:src', 'copy:dev_js' ]
            },

            /**
             * When assets are changed, copy them. Note that this will *not* copy new
             * files, so this is probably not very useful.
             */
            assets: {
                files: [
                    'src/assets/**/*'
                ],
                tasks: [ 'copy:dev_assets' ]
            },

            /**
             * When index.html changes, we need to compile it.
             */
            html: {
                files: [ 'src/index.html' ],
                tasks: [ 'includeSource:dev' ]
            },

            /**
             * When our templates change, we only rewrite the template cache.
             */
            tpls: {
                files: [
                    'src/**/*.tpl.html'
                ]
                // FIXME tasks: [ 'copy:dev_apptpl', 'html2js:dev' ]
            },

            /**
             * When the CSS files change, we need to compile and minify them.
             */
            less: {
                files: [ 'src/**/*.less' ],
                tasks: [ 'less:dev' ]
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
     * In order to make it safe to just compile or copy *only* what was changed,
     * we need to ensure we are starting from a clean, fresh build. So we rename
     * the `watch` task to `delta` (that's why the configuration var above is
     * `delta`) and then add a new task called `watch` that does a clean build
     * before watching for changes.
     */
    grunt.renameTask('watch', 'delta');
    grunt.registerTask('watch', [ 'dev', 'delta' ]);

    /**
     * The default task is to prod.
     */
    grunt.registerTask('default', [ 'prod' ]);

    /**
     * The `dev` task gets your app ready to run for development and testing.
     */
    grunt.registerTask('dev', [
        'clean',
        'jshint',
        'less:dev',
        'copy:dev_assets',
        'copy:dev_js',
        // TODO 'copy:dev_apptpl',
        'copy:dev_vendor_js',
        'copy:dev_vendor_fonts',
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
        'copy:prod_assets',
        // TODO 'html2js:prod',
        'copy:prod_vendor_fonts',
        'ngmin:prod',
        'copy:prod_vendor_js',
        'concat:prod_js',
        'concat:prod_css',
        'uglify:prod',
        'includeSource:prod'
    ]);

};