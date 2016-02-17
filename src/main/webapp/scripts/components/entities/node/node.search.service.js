'use strict';

angular.module('qyosbeApp')
    .factory('NodeSearch', function ($resource) {
        return $resource('api/_search/nodes/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
