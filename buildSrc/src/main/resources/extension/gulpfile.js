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
var debug = require('gulp-debug');
