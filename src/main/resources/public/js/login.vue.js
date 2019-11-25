_initFunctions.push( function() {
	
	// must be executed after page is loaded
	new Vue({
		
		el: '#vue-login',
		
		data: {
			msgErrorLogin: ''
		},
		
		methods: {
			
			submitLogin(event) {
				
				console.log( 'event', event );
				event.preventDefault();
				
				var formData = new FormData();
				formData.set('login', event.target.elements.login.value);
				formData.set('password', event.target.elements.password.value);
				
				axios('/ajax/login', {
					  method: 'post',
					  data: formData,
					  //config: { headers: {'Content-Type':'multipart/form-data' } }
				      })
			  	.then( response => { 
			  		
			  		console.log('response', response);
			  		   
			  		if ( response.data.login != null ) {
			  			
			  			// reload page, with logged in sessionId / user
			  			// https://stackoverflow.com/a/3715053/845117, https://developer.mozilla.org/en-US/docs/Web/API/Location/reload 
			  			location.reload(true);
			  			
			  		} else {
						
			  			// show error
						this.msgErrorLogin = 'Bad credentials.';
					}
			  		
			  	}, error => {
			  		
			  		this.msgErrorLogin = error;
			  		
			  	} )
			  	.catch(error => {
			  		
			  		console.error(error);
			  		this.msgErrorLogin = 'Fatal error: '+error;
			  	})
			  	.finally( () => {} );
			} 
		}
		
	});
	
} );