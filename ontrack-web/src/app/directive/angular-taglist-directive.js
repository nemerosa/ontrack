/**
 * Copied from the deprecated (and not available in NPM) repository
 *
 * at https://github.com/chrispittman/angular-taglist/tree/master
 */

var angular_taglist_directive = angular.module('angular_taglist_directive', []);

angular_taglist_directive.directive('taglist', ['$timeout', function ($timeout) {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            tagData: '=',
            taglistBlurTimeout: '='
        },
        transclude: true,
        template: `<div class="taglist">
        <span class="tag" data-ng-repeat="tag in tagData">
        <a href data-ng-click="tagData.splice($index, 1)">x</a> <span>{{tag}}</span></span>
        <div class="tag-input" ng-transclude></div><div class="tags_clear"></div></div>`,
        compile: function (tElement, tAttrs, transcludeFn) {
            return function (scope, element, attrs) {
                element.bind('click', function () {
                    element[0].getElementsByTagName('input')[0].focus();
                });

                var input = angular.element(element[0].getElementsByTagName('div')[0].getElementsByTagName('input')[0]);

                input.bind('blur', function () {
                    if (scope.taglistBlurTimeout) {
                        $timeout(function() {
                            addTag(input[0]);
                        }, scope.taglistBlurTimeout);
                    } else {
                        addTag(input[0]);
                    }
                });
                input.bind('keydown', function (evt) {
                    if (evt.altKey || evt.metaKey || evt.ctrlKey || evt.shiftKey) {
                        return;
                    }
                    if (evt.which == 188 || evt.which == 13) { // 188 = comma, 13 = return
                        evt.preventDefault();
                        addTag(this);
                    } else if (evt.which == 8 && /* 8 = delete */
                        this.value.trim().length == 0 &&
                        element[0].getElementsByClassName('tag').length > 0) {
                        evt.preventDefault();
                        scope.$apply(function () {
                            scope.tagData.splice(scope.tagData.length - 1, 1);
                        });
                    }
                });

                function addTag(element) {
                    if (!scope.tagData) {
                        scope.tagData = [];
                    }
                    var val = element.value.trim();
                    if (val.length === 0) {
                        return;
                    }
                    if (scope.tagData.indexOf(val) >= 0) {
                        return;
                    }
                    scope.$apply(function () {
                        scope.tagData.push(val);
                        element.value = "";
                    });
                }
            };
        }
    };
}]);