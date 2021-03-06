 'use strict';

angular.module('qyosbeApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-qyosbeApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-qyosbeApp-params')});
                }
                return response;
            }
        };
    });
