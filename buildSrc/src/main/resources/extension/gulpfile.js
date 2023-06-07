/**
 * Gulp script used to package the web resources of an extension
 */
const gulp = require('gulp');
const concat = require('gulp-concat');
const jshint = require('gulp-jshint');
const uglify = require('gulp-uglify');
const templateCache = require('gulp-angular-templatecache');
const ngAnnotate = require('gulp-ng-annotate');
const ngFilesort = require('gulp-angular-filesort');
const debug = require('gulp-debug');
const babel = require("gulp-babel");

// Arguments

const options = {
    version: process.env.VERSION,
    src: process.env.SRC,
    extension: process.env.EXTENSION,
    target: process.env.TARGET,
}

// Sources

const src = options.src;

const templateSources = src + '/**/*.html';
const jsSources = src + '/**/*.js';

// Targets

const build = options.target;

const buildConverted = build + '/converted';
const buildPath = build + '/web';
const buildTemplates = buildPath + '/templates';
const buildDist = buildPath + '/dist';

// NG templates
// By default, the 'gulp-angular-templatecache' registers a `run` hook into the main application module. But when
// we load the extensions, it's already too late and this methid won't be run.
// We have to explicitly register a module and have its initialisation being run.

const TEMPLATE_HEADER = `angular.module("<%= module %>"<%= standalone %>).run(["$log", "$templateCache", function($log, $templateCache) { $log.info("Loading templates for ${options.extension} @ ${options.version}");`;

gulp.task('js:templates', function () {
    return gulp.src(templateSources)
        .pipe(debug({title: 'templates:input:'}))
        .pipe(templateCache({
            module: 'ontrack-extension-' + options.extension + '-templates',
            standalone: true,
            root: '',
            templateHeader: TEMPLATE_HEADER
        }))
        .pipe(gulp.dest(buildTemplates))
        .pipe(debug({title: 'templates:output:'}))
        ;
});

// JS Linting

gulp.task('js:lint', function () {
    return gulp.src(jsSources)
        .pipe(debug({title: 'lint:'}))
        .pipe(jshint({esversion: 6}))
        .pipe(jshint.reporter('default'))
        .pipe(jshint.reporter('fail'))
        ;
});

/**
 * Converted files
 */

gulp.task('js:conversion', gulp.series(
    'js:lint',
    () => gulp.src(jsSources)
        .pipe(debug({title: 'js:conversion:input'}))
        .pipe(babel({
            "presets": [
                "env"
            ],
            "plugins": [
                "transform-es2015-template-literals"
            ]
        }))
        .pipe(gulp.dest(buildConverted))
        .pipe(debug({title: 'js:conversion:output'}))
))

// Sorted and annotated Angular files

gulp.task('js', gulp.series(
    'js:lint',
    'js:templates',
    'js:conversion',
    function () {
        return gulp.src([buildTemplates + '/*.js', buildConverted + '/**/*.js'])
            .pipe(debug({title: 'js:input'}))
            .pipe(ngAnnotate())
            .pipe(ngFilesort())
            .pipe(uglify())
            .pipe(concat('module.js'))
            .pipe(gulp.dest(buildDist))
            .pipe(debug({title: 'js:output'}))
    }
))

// Default build

gulp.task('default', gulp.series('js'));
