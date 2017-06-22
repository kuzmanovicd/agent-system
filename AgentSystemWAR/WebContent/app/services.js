'use strict';

app.factory('ConnectionService', ConnectionService);

app.factory('RestService', RestService);

function ConnectionService($websocket, $rootScope, $timeout, $location) {
	
	var link = document.createElement('a');
	link.setAttribute('href', window.location.href);

	var services = {};
	services.stream = $websocket('ws://' + link.hostname + ':' + link.port + '/AgentSystemWAR/websocket');
	services.messages = [];
	
	services.stream.onMessage(function(message) {
		var msg = JSON.parse(message.data);
		//console.log(msg);

		
		switch(msg.type) {
		case 'message':
			console.log(msg.data);
			services.messages.push(msg.data);
			break;
		case 'running':
			$rootScope.runningAgents = msg.data;
			break;
		case 'notification':
			$rootScope.notification = msg.data;
			$timeout(removeNotification, 8000);
			break;
		case 'logged':
			console.log('logged: ');
			$location.path('/chat');
			$rootScope.loggedUser = msg.data;
			break;
		case 'logout':
			$rootScope.loggedUser = null;
			services.messages = [];
			$location.path('/login');
			break;
		default:
			console.log("NEPOZNAT TIP PORUKE");
			console.log(message);
		}
		
	});

	services.stream.onError(function(message) {
		$rootScope.connection = { 
			data: {
				success: false,
				message: "Desio se error: " + message
			}
		};
		console.log('Konekcija prekinuta ' + $rootScope.notification);
	});

	services.stream.onClose(function(message) {
		$rootScope.connection = { 
			data: {
				success: false,
				message: "Veza sa serverom je prekinuta."
			}
		};
		console.log('Konekcija prekinuta ' + $rootScope.notification);
	});

	services.stream.onOpen(function(message) {
		$rootScope.connection = { 
			data: {
				success: true,
				message: "Veza sa serverom je ostvarena."
			}
		};
		console.log('Konekcija ostvarena ' + $rootScope.notification);
	});

	function removeNotification() {
		console.log('removeNotification');
		$rootScope.notification = null;
	}
	
	return services;
}

function RestService($rootScope, $timeout, $location, $http) {
	var services = {};
	var url = '/AgentSystemWAR/api/';
	services.getRunningAgents = getRunningAgents;
	services.getSupportedAgents = getSupportedAgents;
	services.getPerformatives = getPerformatives;
	services.sendACL = sendACL;

	return services;

	function getRunningAgents() {
		$http.get(url + 'agents/running').then(function(response) {
			var agents = [];
			for (var key in response.data){
				console.log(response.data[key]);
				agents.push(response.data[key]);
			}
			$rootScope.runningAgents = agents;
		});
	}

	function getSupportedAgents() {
		$http.get(url + 'agents/classes/my').then(function(response) {
			$rootScope.supportedAgents = response.data;
		});
	}

	function getPerformatives() {
		$http.get(url + 'agents/messages').then(function(response) {
			$rootScope.performatives = response.data;
		});
	}

	function sendACL(acl) {
		var notification = {};
		$http.post(url + 'agents/message', angular.toJson(acl)).then(function (response) {
			notification.success = true;
			notification.message = "Poruka uspesno poslata!";
			$rootScope.notification = notification;
			$timeout(removeNotification, 8000);
		});
	}

	function removeNotification() {
		$rootScope.notification = null;
	}

	
}