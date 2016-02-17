'use strict';

describe('Controller Tests', function() {

    describe('Cluster Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCluster, MockNode, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCluster = jasmine.createSpy('MockCluster');
            MockNode = jasmine.createSpy('MockNode');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Cluster': MockCluster,
                'Node': MockNode,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ClusterDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'qyosbeApp:clusterUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
