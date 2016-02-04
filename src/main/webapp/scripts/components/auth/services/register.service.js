'use strict';

angular.module('qyosbeApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


