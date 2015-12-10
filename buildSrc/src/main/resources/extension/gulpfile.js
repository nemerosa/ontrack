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

// Arguments

var knownOptions = {
    string: ['version', 'src'],
    default: {version: 'snapshot'}
};

var options = minimist(process.argv.slice(2), knownOptions);

// Sources

var src = options.src;

var templateSources = src + '/**/*.html';
var jsSources = src + '/**/*.js';

// Targets

var build = '.'; // Current directory

var buildPath = build + '/dev';
var buildTemplates = buildPath + '/templates';
var buildAngular = buildPath + '/angular';

// NG templates

gulp.task('templates', function () {
    return gulp.src(templateSources)
        .pipe(debug({title: 'templates:input:'}))
        .pipe(templateCache({module: 'ontrack', root: ''}))
        .pipe(gulp.dest(buildTemplates))
        .pipe(debug({title: 'templates:output:'}))
        ;
});

// JS Linting

gulp.task('js:lint', function () {
    return gulp.src(jsSources)
        .pipe(debug({title: 'lint:'}))
        .pipe(jshint())
        .pipe(jshint.reporter('default'))
        ;
});

// Sorted and annotated Angular files

gulp.task('js:angular', ['js:lint', 'templates'], function () {
    return gulp.src([buildTemplates + '/*.js', jsSources])
        .pipe(debug({title: 'js:angular:input'}))
        .pipe(ngAnnotate())
        .pipe(ngFilesort())
        .pipe(concat('ci-angular.js'))
        .pipe(gulp.dest(buildAngular))
        .pipe(debug({title: 'js:angular:output'}));
});

// Default build

//gulp.task('default', ['templates']);
gulp.task('default', ['js:angular']);
