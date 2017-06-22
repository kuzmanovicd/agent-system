'use strict';

app.controller('MainCtrl', function	($scope, $rootScope, ConnectionService, RestService) {
	$scope.messages = ConnectionService.messages;
	$scope.notification = ConnectionService.notification;

	RestService.getRunningAgents();
	RestService.getSupportedAgents();

	$scope.sendMessage = function() {
		var msg = {
				data: {
					content: $scope.newMessage,
					from: $rootScope.loggedUser,
					//to: null,
					subject: ""
					
				},
				type: "message"
				
		};

		if($scope.selectedUser != null) {
			msg.data.to = $scope.selectedUser;
		}
		
		ConnectionService.stream.send(angular.toJson(msg));
		$scope.newMessage = "";
	}

	$scope.selectUser = function(u) {
		$scope.selectedUser = u;
	}

	$scope.resetUser = function() {
		$scope.selectedUser = null;
	} 

	$scope.isSelected = function(username) {
		console.log('isSelected');
		if(username == $scope.selectedUser) {
			return true;
		}
		console.log('prvi user');
		console.log(username);

		console.log('drugi user');
		console.log($scope.selectedUser);
		return false;
	}
	
	$scope.register = function() {
		var user = {
				type: "register",
				data: $scope.user
		}
		
		console.log
		ConnectionService.stream.send(angular.toJson(user));
		$scope.user = {};
		$scope.password2 = "";
	}
	
	$scope.login = function() {
		var user = {
				type: "login",
				data: $scope.user
		}
		
		console.log(user);

		ConnectionService.stream.send(angular.toJson(user));
		$scope.user = {};
	}
	
	$scope.logout = function() {

		var user = {
				type: "logout",
				data: $rootScope.loggedUser
		}
		console.log('Pozvan logout');
		console.log(user);

		ConnectionService.stream.send(angular.toJson(user));
		$scope.user = {};
	}
});