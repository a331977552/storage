/**
 * 
 */
function goToDesiredUrl(paramName,paramValue){
	
	

	var parameters=window.location.search;
			if(parameters==null|| parameters.trim() ==''){			
				window.location.href=u+"?"+paramName+"="+paramValue;
			}else{
				parameters=parameters.substring(1,parameters.length);
				if(parameters.indexOf(paramName)>=0){
					var params=parameters.split("&");
					parameters="";
					for(var i=0;i<params.length;i++){
						var pa=	params[i];
						var param=	pa.split("=");
						if(param[0].trim()== paramName.trim()){
							param[1]=paramValue;							
						}
						parameters+=(param[0]+"="+param[1]);
						if((i+1)!=params.length){
							parameters+="&";
						}
					}
				}else{
					parameters+=("&"+paramName+"="+paramValue);
				}
				var url=window.location.pathname+"?"+parameters;
				window.location.href=url;
				
			}
}