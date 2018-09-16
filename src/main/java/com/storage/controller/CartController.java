package com.storage.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storage.entity.Cart;
import com.storage.entity.Customer;
import com.storage.entity.custom.CustomCart;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CartRemoteService;
import com.storage.utils.CookieUtils;
import com.storage.utils.JsonUtils;
import com.storage.utils.StringUtils;

@RestController()
@RequestMapping("/cart")
@PropertySource("classpath:myapp.properties")
public class CartController {

	@Value("${cookie.cart.name}")
	String cartName;
	@Autowired
	CartRemoteService service;
	
	@SuppressWarnings("unused")
	@RequestMapping("/add")
	public Object addCart(HttpServletRequest request,HttpServletResponse response,@RequestBody List<CustomCart> list) {
		Cart cart=new Cart();
		cart.setItems(JsonUtils.objectToJson(list));
		Object attribute = request.getSession().getAttribute("user");		
		String cookieValue = CookieUtils.getCookieValue(request, cartName,true);
		if(true) {
			if(StringUtils.isEmpty(cookieValue)) {				
				cookieValue=cart.getItems();
				CookieUtils.setCookie(request, response, cartName, cookieValue,true);	
			}else {
				String items = cart.getItems();				
				List<CustomCart> jsonToList = JsonUtils.jsonToList(items, CustomCart.class);
				List<CustomCart> jsonToList2 = JsonUtils.jsonToList(cookieValue, CustomCart.class);
				String merge = merge(jsonToList,jsonToList2);
				CookieUtils.setCookie(request, response, cartName, merge,3600*24*31,true);	
			}
			return StorageResult.succeed(cart);
		}else 
		//logged in
		{
			Customer user=(Customer) attribute;
			cart.setUserId(user.getId());
			StorageResult<Cart> result;
			if(StringUtils.isEmpty(cookieValue)) {
				result = service.merge(cart);
				
			}else {
				String items = cart.getItems();	
				//merge locally
				
				cart.setItems(items);
				//add merged result to the user in database;
				result=service.merge(cart);
				//get the result	
			}			
		
			if(result.isSuccess()) {
				CookieUtils.setCookie(request, response, cartName, result.getResult().getItems(),3600*24*31,true);					
			}		
			return StorageResult.succeed(cart);
		}
		
		
	}
	
	private String merge(List<CustomCart> itemList1,List<CustomCart> itemList2) {

		Map<Integer, CustomCart> map = new HashMap<>();
		for (CustomCart customCart : itemList1) {
			map.put(customCart.getProductid(), customCart);
		}
		for (CustomCart customCart : itemList2) {
			CustomCart customCart2 = map.get(customCart.getProductid());
			if (customCart2 != null) {
				customCart2.setQuantity(customCart2.getQuantity() + customCart.getQuantity());
			} else {
				map.put(customCart.getProductid(), customCart);
			}

		}
		Collection<CustomCart> values = map.values();
		String objectToJson = JsonUtils.objectToJson(values);
		

		return objectToJson;
	}
	
	@RequestMapping("/empty/{id}")
	public Object emptyCart(@PathVariable(name="id") Integer userId) {
		
		return service.emptyCart(userId);
	}
	@RequestMapping("/merge")
	public Object merge(@RequestBody Cart cart) {
		
		return service.merge(cart);
	}
	
	@RequestMapping("/findByUserId/{id}")
	public Object findByUserId(@PathVariable(name="id")  Integer userId) {
		
		return service.findByUserId(userId);
	}
	
}
