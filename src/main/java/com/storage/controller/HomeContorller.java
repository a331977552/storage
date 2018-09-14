package com.storage.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.entity.Carousel;
import com.storage.entity.Cart;
import com.storage.entity.Category;
import com.storage.entity.Customer;
import com.storage.entity.ProductDetail;
import com.storage.entity.custom.CustomCart;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CarouselRemoteService;
import com.storage.remote.service.CartRemoteService;
import com.storage.remote.service.CategoryRemoteService;
import com.storage.remote.service.CustomerRemoteService;
import com.storage.remote.service.ProductRemoteService;
import com.storage.utils.CookieUtils;
import com.storage.utils.JsonUtils;
import com.storage.utils.StringUtils;

@Controller
@PropertySource("classpath:myapp.properties")
public class HomeContorller {
	
	@Value("${cookie.cart.name}")
	String cartName;
	@Autowired
	CarouselRemoteService carouselService;
	
	@Autowired
	CategoryRemoteService categoryService;
	
	@Autowired
	CustomerRemoteService customerService;
	@Autowired
	ProductRemoteService productService;
	

	@Autowired
	CartRemoteService cartService;

	Logger logger = org.slf4j.LoggerFactory.getLogger(HomeContorller.class);
	
	@RequestMapping(value= {"/","/index","index.html"})
	public ModelAndView index(ModelAndView view) {

	
		//get carousel
		ResponseEntity<String> allCarousel = carouselService.getAllCarousel();	
		if(allCarousel.getStatusCodeValue()==200) {
			StorageResult<List<Carousel>> jsonToObject = JsonUtils.jsonToObject(allCarousel.getBody(),new TypeReference<StorageResult<List<Carousel>>>() {
			});			
			if(jsonToObject.isSuccess()) {
				logger.info(jsonToObject.getResult().toString());
				view.addObject("carousels", jsonToObject.getResult());							
			}else {				
				view.addObject("carousels",new ArrayList<Carousel>());							
			}
		}else {
			view.addObject("carousels",new ArrayList<Carousel>());
		}
		//get category
		ResponseEntity<String> findAll = categoryService.findAll();
		if(findAll.getStatusCodeValue()==200&& findAll.hasBody())
		{
			String body = findAll.getBody();
			StorageResult<List<Category>> jsonToObject = JsonUtils.jsonToObject(body,new TypeReference<StorageResult<List<Category>>>() {
			});		
			if(jsonToObject.isSuccess()) {
				view.addObject("categories",jsonToObject.getResult());							
			}
		}else {
			view.addObject("categories",new ArrayList<Category>());	
		}	
		view.setViewName("index");
		return view;
	}
	
	@RequestMapping(value= {"/user/register"})
	public ModelAndView register(HttpServletRequest request,HttpServletResponse response,Customer customer, ModelAndView view) {
		
		if(customer==null)
			{
				view.addObject("error","user information required");
				view.setViewName("register");
			}else {
				if(StringUtils.isEmpty(customer.getName())) {
					customer.setName(customer.getFirstName()+" "+customer.getLastName());
				}
				StorageResult<Customer> addCustomer = customerService.addCustomer(customer);
				if(addCustomer.isSuccess()) {
					Customer result = addCustomer.getResult();
					if(result.getId()!=null) {
						request.getSession().setMaxInactiveInterval(3600*24);
						request.getSession().setAttribute("user", result);
						view.setViewName("redirect:/index");
					}
					String cookieValue = CookieUtils.getCookieValue(request, cartName,true);
					Customer user=addCustomer.getResult();
					
					if(!StringUtils.isEmpty(cookieValue)) {	
						//add merged result to the user in database;
						Cart cart=new Cart();
						cart.setUserId(user.getId());
						cart.setItems(cookieValue);
						cartService.addCart(cart);
						StorageResult<Cart> findByUserId = cartService.findByUserId(user.getId());
					
						if(findByUserId.isSuccess()) {
							CookieUtils.setCookie(request, response, cartName, findByUserId.getResult().getItems(),3600*24*31,true);					
						}				
					}
					
					
				}else {
					view.addObject("user",customer);
					view.addObject("error",addCustomer.getMsg());
					view.setViewName("register");
				}
			
			}		
		
		
		return view;
	}
	
	@RequestMapping(value= {"/userlogin"})
	public ModelAndView userlogin(HttpServletRequest request,HttpServletResponse response,Customer customer, ModelAndView view) {
		
		if(customer==null)
		{
		/*	view.addObject("error","user information required");*/
			view.setViewName("redirect:/login?error=user information required");
		}else {
			
			StorageResult<Customer> addCustomer = customerService.login(customer);
			if(addCustomer.isSuccess()) {
				Customer result = addCustomer.getResult();
				if(result.getId()!=null) {
					request.getSession().setMaxInactiveInterval(3600*24);
					request.getSession().setAttribute("user", result);
					view.setViewName("redirect:/index");
				}
				//adding shopping cart to database and reset cookie
				String cookieValue = CookieUtils.getCookieValue(request, cartName,true);
				Customer user=addCustomer.getResult();
				
				if(!StringUtils.isEmpty(cookieValue)) {	
					//add merged result to the user in database;
					Cart cart=new Cart();
					cart.setUserId(user.getId());
					cart.setItems(cookieValue);
					cartService.addCart(cart);
					StorageResult<Cart> findByUserId = cartService.findByUserId(user.getId());
				
					if(findByUserId.isSuccess()) {
						CookieUtils.setCookie(request, response, cartName, findByUserId.getResult().getItems(),3600*24*31,true);					
					}				
				}
			}else {
				view.addObject("user",customer);
//				view.addObject("error",addCustomer.getMsg());
				view.setViewName("redirect:/login?error="+addCustomer.getMsg());
			}
			
		}		
		
		
		return view;
	}
	@RequestMapping(value= {"/loginout"})
	public ModelAndView loginout(HttpServletRequest request,HttpServletResponse response, ModelAndView view) {
		 CookieUtils.deleteCookie(request, response, cartName);
		 request.getSession().removeAttribute("user");
		 view.setViewName("index");
		 return view;
	}
	
	@RequestMapping(value= {"/productdetail/{productid}"})
	public ModelAndView getProductDetail(@PathVariable(value="productid",required=true)Integer productId, ModelAndView model) {
		
		ResponseEntity<String> productById = this.productService.getProduct(productId);
		if (productById.getBody() == null) {
			model.setViewName("redirect:/index");
		} else {
			
			String body = productById.getBody();
			ProductDetail jsonToPojo = JsonUtils.jsonToPojo(body, ProductDetail.class);
			model.addObject("product", jsonToPojo.getProduct());
			model.addObject("imgs", jsonToPojo.getImgs());			
			model.setViewName("productdetail");
		}
		return model;
	
	}
	
	@RequestMapping(value= {"/cart"})
	public ModelAndView pageCart(HttpServletRequest request, ModelAndView model) {
		String cookieValue = CookieUtils.getCookieValue(request, cartName,true);
		
		if(!StringUtils.isEmpty(cookieValue)) {
			List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
			List<ProductDetail> products=new ArrayList<>(jsonToList.size());
			for (CustomCart customCart : jsonToList) {
				ResponseEntity<String> product = productService.getProduct(customCart.getProductid());
				if(product.getStatusCode().value()==200) {
					ProductDetail detail=JsonUtils.jsonToPojo(product.getBody(),ProductDetail.class);
					Integer quantity = detail.getProduct().getQuantity();
					if(customCart.getQuantity()>quantity)
						customCart.setQuantity(quantity);
					products.add(detail);
				}else {
					model.setViewName("error?msg="+product.getBody());
					return model;
				}				
			}			
			model.addObject("items",jsonToList);
			model.addObject("products",products);
		}
		model.setViewName("cart");
		
		return model;
		
	}
	
	
	
	
}
