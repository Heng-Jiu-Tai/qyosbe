'use strict';

angular.module('qyosbeApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('cluster', {
                parent: 'entity',
                url: '/clusters',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'qyosbeApp.cluster.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cluster/clusters.html',
                        controller: 'ClusterController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cluster');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('cluster.detail', {
                parent: 'entity',
                url: '/cluster/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'qyosbeApp.cluster.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cluster/cluster-detail.html',
                        controller: 'ClusterDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cluster');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Cluster', function($stateParams, Cluster) {
                        return Cluster.get({id : $stateParams.id});
                    }]
                }
            })
            .state('cluster.new', {
                parent: 'cluster',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cluster/cluster-dialog.html',
                        controller: 'ClusterDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    description: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('cluster', null, { reload: true });
                    }, function() {
                        $state.go('cluster');
                    })
                }]
            })
            .state('cluster.edit', {
                parent: 'cluster',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cluster/cluster-dialog.html',
                        controller: 'ClusterDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Cluster', function(Cluster) {
                                return Cluster.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('cluster', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('cluster.delete', {
                parent: 'cluster',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cluster/cluster-delete-dialog.html',
                        controller: 'ClusterDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Cluster', function(Cluster) {
                                return Cluster.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('cluster', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
