'use strict';

app.controller('MainCtrl', function	($scope, $rootScope, ConnectionService, RestService) {
	$scope.messages = ConnectionService.messages;
	$scope.notification = ConnectionService.notification;

	RestService.getRunningAgents();
	RestService.getSupportedAgents();
	RestService.getPerformatives();

	$scope.sendACL = function() {
		$scope.acl.receivers = [$scope.acl.receivers];
		$rootScope.info = $scope.acl;
		var msg = {
				data: $scope.acl,
				type: "acl"		
		};
		
		RestService.sendACL($scope.acl);
		//ConnectionService.stream.send(angular.toJson(msg));
	};

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
	};
	
	$scope.createAgent = function() {
		var user = {
				type: "create",
				data: $scope.newAgent
		}
		
		console.log(user);
		ConnectionService.stream.send(angular.toJson(user));

		notification.success = true;
		notification.message = "Agent Uspesno kreiran!";
		$rootScope.notification = notification;
		$timeout(removeNotification, 8000);

	};

	$scope.select = function(agent) {
		$rootScope.stopAgent = agent;
	}

	$scope.clearSelection = function(agent) {
		$rootScope.stopAgent = undefined;
	}


	$scope.stop = function() {
		console.log('briseeeem');
		RestService.stopAgent($rootScope.stopAgent);
	}

});