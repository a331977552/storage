/**
 * 
 */



	$(document).ready(function() {
		    $("#locales").change(function () {
			
		        var selectedOption = $('#locales').val();
		        if (selectedOption != ''){
					var href = window.location.href;
					var lang=href.indexOf("lang=");
					if(lang!=-1){
					
						var reg=/lang=[a-zA-Z][a-zA-Z]/i;
						href=href.replace(reg,"lang="+selectedOption);
						window.location.replace(href);
					}else{
						window.location.replace(href+'?lang=' + selectedOption);						
					}
		           
		        }
		    });
		});	
