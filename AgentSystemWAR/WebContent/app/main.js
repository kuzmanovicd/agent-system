'use strict';

var app = angular.module('mainApp', ['ngRoute', 'ngWebSocket']).run(run);

app.config(function ($routeProvider) {

    $routeProvider
        .when('/home', {
            controller: 'MainCtrl',
            templateUrl: 'views/home.html'
        })
        .when('/agents', {
            controller: 'MainCtrl',
            templateUrl: 'views/agents.html'
        })
        .otherwise({
            redirectTo: '/home'
        })

});

function run($rootScope, $location) {

    //changeLocation();

    function changeLocation() {
        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            var restrictedPage = $.inArray($location.path(), ['/login', '/register']) == -1;
            if (restrictedPage && !$rootScope.loggedUser) {
                $location.path('/login');
            }
        });
    }

}