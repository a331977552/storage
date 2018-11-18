/**
 * 
 */

function showNotifaction(msg){
		$.notify({message:msg}, {
			animate: {
				enter: 'animated fadeInUp',
				exit: 'animated fadeOutDown'
			},
		placement:{
			from:'bottom',	
			align:'center'
		},
		offset:{
			y:150
		},
		element: 'body'
		,newest_on_top: true,
		allow_dismiss: true,
		delay: 1000,
		timer: 1000,
		type:'success',
		
		});		
	}

function showNotifactionInCenter(msg,dismissable,typ){
	$.notify({message:msg}, {
		animate: {
			enter: 'animated fadeInUp',
			exit: 'animated fadeOutDown'
		},
	placement:{
		from:'bottom',	
		align:'center'
	},
	offset:{
		y:150
	},
	element: 'body'
	,newest_on_top: true,
	allow_dismiss: dismissable,
	delay: 1000,
	timer: 1000,
	type:typ,
	
	});		
}