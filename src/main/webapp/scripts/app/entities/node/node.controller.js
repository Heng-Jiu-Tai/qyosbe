'use strict';

angular.module('qyosbeApp')
    .controller('NodeController', function ($scope, $state, Node, NodeSearch) {

        $scope.nodes = [];
        $scope.loadAll = function() {
            Node.query(function(result) {
               $scope.nodes = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            NodeSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.nodes = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.node = {
                name: null,
                id: null
            };
        };
    });
