'use strict';

angular.module('qyosbeApp')
    .factory('ClusterSearch', function ($resource) {
        return $resource('api/_search/clusters/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
