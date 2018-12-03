package com.storage.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.security.MyAuthenticationProvider;
import com.storage.entity.*;
import com.storage.entity.custom.CustomCart;
import com.storage.entity.custom.CustomProduct;
import com.storage.entity.custom.OrderWrap;
import com.storage.entity.custom.StorageResult;
import com.storage.entity.utils.Constants;
import com.storage.remote.service.*;
import com.storage.utils.CookieUtils;
import com.storage.utils.JsonUtils;
import com.storage.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@PropertySource("classpath:myapp.properties")
public class HomeController {

	private static final int PRODUCT_DELETE = 1;
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
	StOrderRemoteService remoteService;
	
	@Autowired
	StOrderRemoteService orderService;
	
	
	@Autowired
	SettingRemoteService settingRemoteService;
	
	@Autowired
	AdRemoteService adService;
	
	@Autowired
	CartRemoteService cartService;

	Logger logger = org.slf4j.LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = { "/", "/index", "index.html" })
	public ModelAndView index(ModelAndView view) {
		// get carousel
		ResponseEntity<String> allCarousel = carouselService.getAllCarousel();
		if (allCarousel.getStatusCodeValue() == 200) {
			StorageResult<List<Carousel>> jsonToObject = JsonUtils.jsonToObject(allCarousel.getBody(),
					new TypeReference<StorageResult<List<Carousel>>>() {
					});
			if (jsonToObject.isSuccess()) {
				view.addObject("carousels", jsonToObject.getResult());
			} else {
				view.addObject("carousels", new ArrayList<Carousel>());
			}
		} else {
			view.addObject("carousels", new ArrayList<Carousel>());
		}
		// get category
		ResponseEntity<String> findAll = categoryService.findAll();
		if (findAll.getStatusCodeValue() == 200 && findAll.hasBody()) {
			String body = findAll.getBody();
			List<Category> jsonToObject = JsonUtils.jsonToObject(body,
					new TypeReference<List<Category>>() {
					});
			
				view.addObject("categories", jsonToObject);
		} else {
			view.addObject("categories", new ArrayList<Category>());
		}
		String allADS = adService.getAllCarousel().getBody();
		 List<Advertisement> jsonToList = JsonUtils.jsonToList(allADS, Advertisement.class);
		 
		view.addObject("allADS",jsonToList);
		
		view.setViewName("index");
		return view;
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = { "/user/register" })
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response, Customer customer,
			ModelAndView view) {

		if (customer == null|| StringUtils.isEmpty(customer.getEmail()) || StringUtils.isEmpty(customer.getPassword())) {
			view.addObject("error", "user information required");
			view.setViewName("redirect:/register");
		} else {
			if(!customer.getEmail().contains("@")){
				view.addObject("error", "please input correct email address");
				view.setViewName("redirect:/register");
				return view;
			}
			if (StringUtils.isEmpty(customer.getName())) {
				customer.setName(customer.getFirstName() + " " + customer.getLastName());
			}
			Object user = request.getAttribute("user");
			String userSession = CookieUtils.getCookieValue(request, Constants.USER_SESSION_ID);
			if( user instanceof Customer || StringUtils.isEmpty(userSession)){
				//登陆的用户又要注册.或者用户session为空
				Customer body = customerService.createEmptyUser().getBody();
				if(body==null)
				{
					view.addObject("error", "server busy, code:+"+10010);
					view.setViewName("redirect:/register");
					return view;
				}
				userSession =body.getSessionId();

				CookieUtils.setCookie(request,response, Constants.USER_SESSION_ID,userSession,Constants.USER_COOKIETIME);
			}

			customer.setSessionId(userSession);

			StorageResult<Customer> addCustomer = customerService.addCustomer(customer);
			if (addCustomer.isSuccess()) {
				Customer result = addCustomer.getResult();
				if (result.getId() != null) {
					//login interceptor 会自动拦截,设置登陆状态
					view.setViewName("redirect:/index");
				}


			} else {
				view.addObject("user", customer);
				view.addObject("error", addCustomer.getMsg());
				view.setViewName("redirect:/register");
			}

		}

		return view;
	}

	@Autowired
	MyAuthenticationProvider authManager;
	@SuppressWarnings("unused")
	@RequestMapping(value = { "/userlogin" })
	public ModelAndView userlogin(HttpServletRequest request, HttpServletResponse response, Customer customer,
			ModelAndView view) {
		if (customer == null) {
			/* view.addObject("error","user information required"); */
			view.setViewName("redirect:/login?error=user information required");
		} else {
			UsernamePasswordAuthenticationToken authReq
					= new UsernamePasswordAuthenticationToken(customer.getEmail(), customer.getPassword());
			Authentication auth = null;
			try {
				auth = authManager.authenticate(authReq);
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(auth);
				HttpSession session = request.getSession(true);
				session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
				Customer principal = (Customer) auth.getPrincipal();

				CookieUtils.setCookie(request,response,Constants.USER_SESSION_ID,principal.getSessionId(),Constants.USER_COOKIETIME);

				view.setViewName("redirect:/index");
			} catch (AuthenticationException e) {
				view.addObject("user", customer);
				// view.addObject("error",addCustomer.getMsg());
				view.setViewName("redirect:/login?error=" + e.getMessage() + "&email=" + customer.getEmail());
			}


		}

		return view;
	}

	@RequestMapping(value = { "/my_logout" })
	public ModelAndView loginout(HttpServletRequest request, HttpServletResponse response, ModelAndView view) {
		request.getSession().removeAttribute(Constants.USER);
		request.removeAttribute(Constants.USER);
		CookieUtils.setCookie(request,response,Constants.USER_SESSION_ID,null);
		view.setViewName("redirect:/login");
		return view;
	}

	@RequestMapping(value = { "/productdetail/{productid}" })
	public ModelAndView getProductDetail(@PathVariable(value = "productid", required = true) Integer productId,
			ModelAndView model) {

		ResponseEntity<String> productById = this.productService.getProduct(productId);
		
		if (productById.getBody() == null) {
			model.setViewName("redirect:/index");
		} else {

			String body = productById.getBody();
			ProductDetail jsonToPojo = JsonUtils.jsonToPojo(body, ProductDetail.class);
			Product product = jsonToPojo.getProduct();
			Integer category2 = product.getCategory();
			
			model.addObject("product",product );
			model.addObject("imgs", jsonToPojo.getImgs());
			Category category = jsonToPojo.getCategory();
		
			model.addObject("category",category);
			
			ResponseEntity<String> bestSellingProduct = this.productService.getBestSellingProduct(category2);
			model.addObject("recommendedProducts",JsonUtils.jsonToList(bestSellingProduct.getBody(),Product.class));
			model.setViewName("productdetail");
		}
		return model;

	}

	@RequestMapping(value = { "/checkout" })
	public ModelAndView checkout(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			ModelAndView model) {
		Object attribute = request.getAttribute("user");
		if (attribute != null) {
			Customer user = (Customer) attribute;
			StorageResult<Customer> customer = customerService.getCustomer(user.getId());
			model.addObject("user", customer.getResult());
		}
	
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
		System.out.println(cookieValue);
		if (!StringUtils.isEmpty(cookieValue)) {
			populateProductDetails(request, response, model, cookieValue);
			Integer currencyDisplay = settingRemoteService.getSetting().getResult().getCurrencyDisplay();
			model.addObject("currencySymbol",currencyDisplay==1?"£":"¥");
			model.setViewName("checkout");
		} else {
			model.setViewName("redirect:/index");
		}

		return model;

	}

	@RequestMapping(value = { "/cart" })
	public ModelAndView pageCart(HttpServletRequest request, HttpServletResponse response, ModelAndView model) {
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
		System.out.println(cookieValue);
		if (!StringUtils.isEmpty(cookieValue)) {
			populateProductDetails(request, response, model, cookieValue);
		} else {
			model.addObject("items", new ArrayList<>());
			model.addObject("products",new ArrayList<>());
		}
		StorageResult<Setting> setting2 = settingRemoteService.getSetting();
		model.addObject("setting",setting2.getResult());
		model.addObject("currencySymbol",setting2.getResult().getCurrencyDisplay()==1?"£":"¥");
		
		model.setViewName("cart");

		return model;

	}

	private void populateProductDetails(HttpServletRequest request, HttpServletResponse response, ModelAndView model, String cookieValue) {
		List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
		List<ProductDetail> products = new ArrayList<>();
		List<CustomCart> removal=new ArrayList<>();

		boolean touched=false;
		List<CustomProduct> productsByCarts = getProductsByCarts(jsonToList);
		System.out.println(cookieValue);
		for (CustomProduct customProduct : productsByCarts) {

			ProductDetail productDetail=new ProductDetail();
			productDetail.setProduct(customProduct.getProduct());
			productDetail.setImgs(customProduct.getImgs());
			Integer quantity = productDetail.getProduct().getQuantity();

			if (quantity == 0 || productDetail.getProduct().getStatus() == PRODUCT_DELETE) {
				touched=true;
				for (CustomCart customCart : jsonToList) {
					if(customCart.getProductid()==customProduct.getProduct().getId()){
						removal.add(customCart);
						break;
					}
				}
			} else {
					for (CustomCart customCart : jsonToList) {
						if(customCart.getProductid()==customProduct.getProduct().getId()){
						if (customCart.getQuantity() > quantity)
							customCart.setQuantity(quantity);
						break;
					}
				}
				products.add(productDetail);
			}
		}


		System.out.println(cookieValue);
		if (touched) {
			jsonToList.removeAll(removal);
			System.out.println(cookieValue);
			String objectToJson = JsonUtils.objectToJson(jsonToList);
			CookieUtils.setCookie(request, response, cartName, objectToJson, Constants.CART_COOKIE_TIME,true);
		}


		model.addObject("items", jsonToList);

		model.addObject("products", reOrderProductsAccordingToItems(jsonToList,products));
	}


	private List<ProductDetail>  reOrderProductsAccordingToItems(List<CustomCart> carts,List<ProductDetail> products){
		List<ProductDetail> list=new ArrayList<>();

		for (CustomCart cart : carts) {
			for (ProductDetail product : products) {
				if(product.getProduct().getId()==cart.getProductid()){
					list.add(product);
					break;
				}
			}

		}

		return list;

	}

	@RequestMapping(value = { "/userprofile" })
	public ModelAndView userprofile(HttpServletRequest request,ModelAndView model) {
		Object attribute = request.getAttribute("user");

		if(attribute==null) {
			model.setViewName("redirect:/login");			
		}else {
			model.addObject("user",attribute);
			model.setViewName("userprofile");			
		}
		
		
		
		return model;
	}

	@RequestMapping(value = { "/orderdetail" })
	public ModelAndView orderdetail(Integer orderId,ModelAndView model) {
		if(orderId==null) {
			model.addObject("error","no order id fount");
			model.setViewName("redirect:/error");			
		}else {
			OrderWrap stOrder = orderService.getInfoFromOrder(orderId);
			model.addObject("order",stOrder);
			model.setViewName("orderdetail");
		}
		return model;
		
	}
	@RequestMapping(value = { "/order" })
	public ModelAndView order(HttpServletRequest request, HttpServletResponse response, Customer customer,
			String comment, String paymentMethod, ModelAndView model) {
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
		Object attribute = request.getAttribute("user");
		if (attribute != null) {
			Customer customer2 = (Customer) attribute;
			customer.setId(customer2.getId());
		} else {
			String sessionId = CookieUtils.getCookieValue(request, Constants.USER_SESSION_ID);
			if(sessionId!=null)
			{
				ResponseEntity<Customer> customerbySessionId = customerService.getCustomerbySessionId(sessionId);
				if(customerbySessionId.getBody()!=null){
					customer.setId(customerbySessionId.getBody().getId());
				}else{
					customer.setId(-1);
				}
			}else{
				customer.setId(-1);
			}
		}
	
		
		if (!StringUtils.isEmpty(cookieValue)) {
			List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
			List<ProductDetail> products = new ArrayList<>();
			List<CustomCart> removal = new ArrayList<>();

			//get products
			List<CustomProduct> productsFromDB = getProductsByCarts(jsonToList);
			//populate products imgs into details
			//and correct quantity

			outer:for (CustomProduct customProduct : productsFromDB) {
				ProductDetail detail=new ProductDetail();
				detail.setImgs(customProduct.getImgs());
				detail.setProduct(customProduct.getProduct());
				for (CustomCart customCart : jsonToList) {
					if(customCart.getProductid()==customProduct.getProduct().getId()){
						boolean remove = correctProductQuantity(customCart, customProduct.getProduct());
						if(remove){
							removal.add(customCart);
							//if should be  removed,  then don't add product into products list
							continue outer;
						}
						break;
					}
				}
				products.add(detail);
			}

			jsonToList.removeAll(removal);

			OrderWrap result = new OrderWrap();
			customer.setName(customer.getFirstName() + " " + customer.getLastName());
			result.setCustomer(customer);
			result.setDate(new Date());
			List<CustomProduct> list = new ArrayList<>();

			BigDecimal totalPrice = new BigDecimal(0);
			for (ProductDetail pd : products) {
				CustomProduct e = new CustomProduct();
				e.setProduct(pd.getProduct());
				CustomCart customCart = null;
				for (CustomCart cc : jsonToList) {
					if(cc.getProductid()==pd.getProduct().getId()){
						customCart=cc;

					}
				}
				if(customCart==null){
					logger.error("cart --- product unmatch error");
					continue;
				}
				e.setQty(customCart.getQuantity());
				BigDecimal quan = new BigDecimal(customCart.getQuantity());
				BigDecimal sellingprice = pd.getProduct().getSellingprice_aftertax();	
				pd.getProduct().setSellingprice(sellingprice);
				BigDecimal multiply = quan.multiply(sellingprice);				
				totalPrice = totalPrice.add(multiply);				
				e.setSubtotal(multiply);
				list.add(e);

			}
			result.setList(list);
			StOrder order = new StOrder();
			order.setComment(comment);
			order.setPayment(paymentMethod);
			result.setOrder(order);

			result.setTotalPrice(totalPrice);

			OrderWrap creaOrder = remoteService.creaOrder(result);
			CookieUtils.deleteCookie(request, response, cartName);
			model.addObject("orderId",creaOrder.getOrder().getId());
			model.setViewName("redirect:/orderdetail");
		} else {
			model.setViewName("redirect:/error?error=please dont re-submit");
		}

		return model;

	}

	private List<CustomProduct> getProductsByCarts(List<CustomCart> jsonToList ){
		List<Integer> ids=new ArrayList<>();
		for (CustomCart customCart : jsonToList) {
			ids.add(customCart.getProductid());
		}

		ResponseEntity<String> productsByIds = productService.getProductsByIds(ids);
		if(productsByIds.getBody()==null || productsByIds.getStatusCode().value()!=200)
			return new ArrayList<>();
		return JsonUtils.jsonToList(productsByIds.getBody(), CustomProduct.class);

	}

	private boolean correctProductQuantity(CustomCart customCart,Product product){
		Integer quantity = product.getQuantity();
		if (quantity == 0 || product.getStatus() == PRODUCT_DELETE) {
			return true;
		} else {
			if (customCart.getQuantity() > quantity)
				customCart.setQuantity(quantity);
			return false;
		}
	}

}
