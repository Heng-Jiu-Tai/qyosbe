'use strict';

angular.module('qyosbeApp')
    .controller('BlogDetailController', function ($scope, $rootScope, $stateParams, entity, Blog, User, Entry) {
        $scope.blog = entity;
        $scope.load = function (id) {
            Blog.get({id: id}, function(result) {
                $scope.blog = result;
            });
        };
        var unsubscribe = $rootScope.$on('qyosbeApp:blogUpdate', function(event, result) {
            $scope.blog = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
