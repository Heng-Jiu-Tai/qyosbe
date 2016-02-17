'use strict';

angular.module('qyosbeApp').controller('NodeDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Node', 'Cluster',
        function($scope, $stateParams, $uibModalInstance, entity, Node, Cluster) {

        $scope.node = entity;
        $scope.clusters = Cluster.query();
        $scope.load = function(id) {
            Node.get({id : id}, function(result) {
                $scope.node = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('qyosbeApp:nodeUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.node.id != null) {
                Node.update($scope.node, onSaveSuccess, onSaveError);
            } else {
                Node.save($scope.node, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
