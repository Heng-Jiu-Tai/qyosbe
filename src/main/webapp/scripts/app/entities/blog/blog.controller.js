'use strict';

angular.module('qyosbeApp')
    .controller('BlogController', function ($scope, $state, Blog, BlogSearch) {

        $scope.blogs = [];
        $scope.loadAll = function() {
            Blog.query(function(result) {
               $scope.blogs = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            BlogSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.blogs = result;
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
            $scope.blog = {
                name: null,
                handle: null,
                id: null
            };
        };
    });
