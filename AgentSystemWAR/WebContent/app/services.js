'use strict';

app.factory('ConnectionService', ConnectionService);

function ConnectionService($websocket, $rootScope, $timeout, $location) {
	
	var link = document.createElement('a');
	link.setAttribute('href', window.location.href);

	var services = {};
	services.stream = $websocket('ws://' + link.hostname + ':' + link.port + '/UIChatApp/websocket');
	services.messages = [];
	
	services.stream.onMessage(function(message) {
		var msg = JSON.parse(message.data);
		console.log(msg);

		
		switch(msg.type) {
		case 'message':
			services.messages.push(msg);
			break;
		case 'online-users':
			$rootScope.users = msg.data;
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