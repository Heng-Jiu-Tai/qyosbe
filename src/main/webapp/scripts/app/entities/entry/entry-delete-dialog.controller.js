'use strict';

angular.module('qyosbeApp')
	.controller('EntryDeleteController', function($scope, $uibModalInstance, entity, Entry) {

        $scope.entry = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Entry.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
