'use strict';

var app = angular.module('mainApp', ['ngRoute', 'ngWebSocket']).run(run);

app.config(function ($routeProvider) {

    $routeProvider
        .when('/home', {
        	controller: 'MainCtrl',
            templateUrl: 'views/home.html'
        })
        .when('/login', {
        	controller: 'MainCtrl',
        	templateUrl: 'views/login.html'
        })
        .when('/register', {
        	controller: 'MainCtrl',
        	templateUrl: 'views/register.html'
        })
        .when('/chat', {
        	controller: 'MainCtrl',
        	templateUrl: 'views/chat.html'
        })
        .otherwise({
            redirectTo: '/home'
        })

});

function run($rootScope, $location ) {

	changeLocation();
    
    function changeLocation() {
        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            var restrictedPage = $.inArray($location.path(), ['/login', '/register']) == -1;
            if (restrictedPage && !$rootScope.loggedUser) {
                $location.path('/login');
            }
        });
    }

}
