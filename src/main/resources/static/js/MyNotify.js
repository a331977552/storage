/**
 * 
 */
initializeSnacker();
function initializeSnacker(){



}

function showNotifaction(msg){
    var iDiv = document.createElement('div');
    iDiv.innerHTML=msg;
    iDiv.classList.add("show",'cody___snackbar');

    document.getElementsByTagName('body')[0].appendChild(iDiv);


    // Add the "show" class to DIV

    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        document.getElementsByTagName('body')[0].removeChild(iDiv);
        }, 2000);
		/*$.notify({message:msg}, {
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

		});		*/
	}

function showNotifactionInCenter(msg,dismissable,typ){
    var iDiv = document.createElement('div');
    iDiv.innerHTML=msg;
    iDiv.classList.add("show",'cody___snackbar');

    document.getElementsByTagName('body')[0].appendChild(iDiv);


    // Add the "show" class to DIV

    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        document.getElementsByTagName('body')[0].removeChild(iDiv);
    }, 2000);
	/*$.notify({message:msg}, {
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
	
	});		*/

}