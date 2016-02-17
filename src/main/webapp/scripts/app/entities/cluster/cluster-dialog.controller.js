'use strict';

angular.module('qyosbeApp').controller('ClusterDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Cluster', 'Node', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Cluster, Node, User) {

        $scope.cluster = entity;
        $scope.nodes = Node.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Cluster.get({id : id}, function(result) {
                $scope.cluster = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('qyosbeApp:clusterUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.cluster.id != null) {
                Cluster.update($scope.cluster, onSaveSuccess, onSaveError);
            } else {
                Cluster.save($scope.cluster, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
