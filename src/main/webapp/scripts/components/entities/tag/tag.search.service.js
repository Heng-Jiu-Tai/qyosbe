'use strict';

angular.module('qyosbeApp')
    .factory('TagSearch', function ($resource) {
        return $resource('api/_search/tags/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
