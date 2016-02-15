'use strict';

angular.module('qyosbeApp')
    .factory('EntrySearch', function ($resource) {
        return $resource('api/_search/entrys/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
