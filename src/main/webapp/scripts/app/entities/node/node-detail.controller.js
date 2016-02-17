'use strict';

angular.module('qyosbeApp')
    .controller('NodeDetailController', function ($scope, $rootScope, $stateParams, entity, Node, Cluster) {
        $scope.node = entity;
        $scope.load = function (id) {
            Node.get({id: id}, function(result) {
                $scope.node = result;
            });
        };
        var unsubscribe = $rootScope.$on('qyosbeApp:nodeUpdate', function(event, result) {
            $scope.node = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
