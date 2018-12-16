/**
 * Gulp script used to package the web resources of an extension
 */

var gulp = require('gulp');
var concat = require('gulp-concat');
var inject = require('gulp-inject');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var templateCache = require('gulp-angular-templatecache');
var ngAnnotate = require('gulp-ng-annotate');
var ngFilesort = require('gulp-angular-filesort');
var debug = require('gulp-debug');
var minimist = require('minimist');
var babel = require("gulp-babel");

// Arguments

var knownOptions = {
    string: ['extension', 'version', 'src', 'target'],
    default: {version: 'snapshot'}
};

var options = minimist(process.argv.slice(2), knownOptions);

// Sources

var src = options.src;

var templateSources = src + '/**/*.html';
var jsSources = src + '/**/*.js';

// Targets

var build = options.target;

var buildConvertedJs = build + '/converted';
var buildPath = build + '/web';
var buildTemplates = buildPath + '/templates';
var buildDist = buildPath + '/dist';

// NG templates
// By default, the 'gulp-angular-templatecache' registers a `run` hook into the main application module. But when
// we load the extensions, it's already too late and this methid won't be run.
// We have to explicitly register a module and have its initialisation being run.

var TEMPLATE_HEADER = 'angular.module("<%= module %>"<%= standalone %>).run(["$log", "$templateCache", function($log, $templateCache) { ' +
    '$log.info("Loading templates for ' + options.extension + ' @ ' + options.version + '");';

gulp.task('js:templates', function () {
    return gulp.src(templateSources)
        .pipe(debug({title: 'templates:input:'}))
        .pipe(templateCache({module: 'ontrack-extension-' + options.extension + '-templates', standalone: true, root: '', templateHeader: TEMPLATE_HEADER}))
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

gulp.task('js:conversion', ['js:lint'], function () {
    return gulp.src(jsSources)
        .pipe(debug({title: 'js:conversion:input'}))
        .pipe(babel())
        .pipe(gulp.dest(buildConvertedJs))
        .pipe(debug({title: 'js:conversion:output'}));
});

// Sorted and annotated Angular files

gulp.task('js', ['js:lint', 'js:templates', 'js:conversion'], function () {
    return gulp.src([buildTemplates + '/*.js', buildConvertedJs  + '/*.js'])
        .pipe(debug({title: 'js:input'}))
        .pipe(ngAnnotate())
        .pipe(ngFilesort())
        .pipe(uglify())
        .pipe(concat('module.js'))
        .pipe(gulp.dest(buildDist))
        .pipe(debug({title: 'js:output'}));
});

// Default build

gulp.task('default', ['js']);
