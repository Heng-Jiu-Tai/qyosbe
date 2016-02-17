'use strict';

angular.module('qyosbeApp')
	.controller('ClusterDeleteController', function($scope, $uibModalInstance, entity, Cluster) {

        $scope.cluster = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Cluster.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
