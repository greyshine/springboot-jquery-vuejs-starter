_initFunctions.push( function() {
	
	new Vue({
		
		el: '#app',
		
		data: {
			message: 'Vue dynamic generated text'
		},
	
		methods: {
			
			logout: function() {
				axios( '/ajax/logout' )
					  .finally( () => { window.location = '/'; } );
			}
		}
	});
});