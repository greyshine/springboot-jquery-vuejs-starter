var jq = null;
var _initFunctions = [];

$().ready( function() {
	
	_initFunctions.forEach( function( item ) { item.bind(jq).call(); } );
	
} );
