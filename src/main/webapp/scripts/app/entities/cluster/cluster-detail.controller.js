'use strict';

angular.module('qyosbeApp')
    .controller('ClusterDetailController', function ($scope, $rootScope, $stateParams, entity, Cluster, Node, User) {
        $scope.cluster = entity;
        $scope.load = function (id) {
            Cluster.get({id: id}, function(result) {
                $scope.cluster = result;
            });
        };
        var unsubscribe = $rootScope.$on('qyosbeApp:clusterUpdate', function(event, result) {
            $scope.cluster = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
