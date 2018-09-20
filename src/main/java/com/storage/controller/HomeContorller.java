package com.storage.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.storage.entity.Setting;
import com.storage.entity.StOrder;
import com.storage.entity.custom.CustomCart;
import com.storage.entity.custom.CustomProduct;
import com.storage.entity.custom.OrderWrap;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.CarouselRemoteService;
import com.storage.remote.service.CartRemoteService;
import com.storage.remote.service.CategoryRemoteService;
import com.storage.remote.service.CustomerRemoteService;
import com.storage.remote.service.ProductRemoteService;
import com.storage.remote.service.SettingRemoteService;
import com.storage.remote.service.StOrderRemoteService;
import com.storage.utils.CookieUtils;
import com.storage.utils.JsonUtils;
import com.storage.utils.StringUtils;

@Controller
@PropertySource("classpath:myapp.properties")
public class HomeContorller {

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
	SettingRemoteService settingRemoteService;
	
	@Autowired
	CartRemoteService cartService;

	Logger logger = org.slf4j.LoggerFactory.getLogger(HomeContorller.class);

	@RequestMapping(value = { "/", "/index", "index.html" })
	public ModelAndView index(ModelAndView view) {

		// get carousel
		ResponseEntity<String> allCarousel = carouselService.getAllCarousel();
		if (allCarousel.getStatusCodeValue() == 200) {
			StorageResult<List<Carousel>> jsonToObject = JsonUtils.jsonToObject(allCarousel.getBody(),
					new TypeReference<StorageResult<List<Carousel>>>() {
					});
			if (jsonToObject.isSuccess()) {
				logger.info(jsonToObject.getResult().toString());
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
			StorageResult<List<Category>> jsonToObject = JsonUtils.jsonToObject(body,
					new TypeReference<StorageResult<List<Category>>>() {
					});
			if (jsonToObject.isSuccess()) {
				view.addObject("categories", jsonToObject.getResult());
			}
		} else {
			view.addObject("categories", new ArrayList<Category>());
		}
		view.setViewName("index");
		return view;
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = { "/user/register" })
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response, Customer customer,
			ModelAndView view) {

		if (customer == null) {
			view.addObject("error", "user information required");
			view.setViewName("register");
		} else {
			if (StringUtils.isEmpty(customer.getName())) {
				customer.setName(customer.getFirstName() + " " + customer.getLastName());
			}
			StorageResult<Customer> addCustomer = customerService.addCustomer(customer);
			if (addCustomer.isSuccess()) {
				Customer result = addCustomer.getResult();
				if (result.getId() != null) {
					request.getSession().setMaxInactiveInterval(3600 * 24);
					request.getSession().setAttribute("user", result);
					view.setViewName("redirect:/index");
				}
				if (false) {

					String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
					Customer user = addCustomer.getResult();

					if (!StringUtils.isEmpty(cookieValue)) {
						// add merged result to the user in database;
						Cart cart = new Cart();
						cart.setUserId(user.getId());
						cart.setItems(cookieValue);
						StorageResult<Cart> merge = cartService.merge(cart);

						if (merge.isSuccess()) {
							CookieUtils.setCookie(request, response, cartName, merge.getResult().getItems(),
									3600 * 24 * 31, true);
						}
					}
				}

			} else {
				view.addObject("user", customer);
				view.addObject("error", addCustomer.getMsg());
				view.setViewName("register");
			}

		}

		return view;
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = { "/userlogin" })
	public ModelAndView userlogin(HttpServletRequest request, HttpServletResponse response, Customer customer,
			ModelAndView view) {

		if (customer == null) {
			/* view.addObject("error","user information required"); */
			view.setViewName("redirect:/login?error=user information required");
		} else {

			StorageResult<Customer> addCustomer = customerService.login(customer);
			if (addCustomer.isSuccess()) {
				Customer result = addCustomer.getResult();
				result.setPassword("");
				if (result.getId() != null) {
					request.getSession().setMaxInactiveInterval(3600 * 24);
					request.getSession().setAttribute("user", result);
					view.setViewName("redirect:/index");
				}
				if (false) {

					// adding shopping cart to database and reset cookie
					String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
					Customer user = addCustomer.getResult();

					if (!StringUtils.isEmpty(cookieValue)) {
						// add merged result to the user in database;
						Cart cart = new Cart();
						cart.setUserId(user.getId());
						cart.setItems(cookieValue);
						StorageResult<Cart> merge = cartService.merge(cart);

						if (merge.isSuccess()) {
							CookieUtils.setCookie(request, response, cartName, merge.getResult().getItems(),
									3600 * 24 * 31, true);
						}
					} else {
						StorageResult<Cart> findByUserId = cartService.findByUserId(user.getId());
						if (findByUserId.isSuccess()) {
							CookieUtils.setCookie(request, response, cartName, findByUserId.getResult().getItems(),
									3600 * 24 * 31, true);
						}
					}
				}
			} else {
				view.addObject("user", customer);

				// view.addObject("error",addCustomer.getMsg());
				view.setViewName("redirect:/login?error=" + addCustomer.getMsg() + "&email=" + customer.getEmail());
			}

		}

		return view;
	}

	@RequestMapping(value = { "/my_logout" })
	public ModelAndView loginout(HttpServletRequest request, HttpServletResponse response, ModelAndView view) {
		// CookieUtils.deleteCookie(request, response, cartName);
		request.getSession().removeAttribute("user");
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
			model.addObject("product", jsonToPojo.getProduct());
			model.addObject("imgs", jsonToPojo.getImgs());
			model.setViewName("productdetail");
		}
		return model;

	}

	@RequestMapping(value = { "/checkout" })
	public ModelAndView checkout(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			ModelAndView model) {
		Object attribute = httpSession.getAttribute("user");
		if (attribute != null) {
			Customer user = (Customer) attribute;
			StorageResult<Customer> customer = customerService.getCustomer(user.getId());
			model.addObject("user", customer.getResult());
		}
		StorageResult<Setting> setting = settingRemoteService.getSetting();
		if(!setting.isSuccess())
		{
			model.setViewName("redirect:/error?error=server unavailiable");
			return model;
		}
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);

		if (!StringUtils.isEmpty(cookieValue)) {
			List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
			List<ProductDetail> products = new ArrayList<>(jsonToList.size());
			List<CustomCart> removal = new ArrayList<>();
			for (CustomCart customCart : jsonToList) {
				ResponseEntity<String> product = productService.getProduct(customCart.getProductid());
				if (product.getStatusCode().value() == 200) {
					ProductDetail detail = JsonUtils.jsonToPojo(product.getBody(), ProductDetail.class);
					Integer quantity = detail.getProduct().getQuantity();
					if (quantity == 0 || detail.getProduct().getStatus() == PRODUCT_DELETE) {
						removal.add(customCart);
					} else {
						if (customCart.getQuantity() > quantity)
							customCart.setQuantity(quantity);
						detail.getProduct().setSellingprice((int)(detail.getProduct().getSellingprice()*setting.getResult().getCurrencyRate()));
						products.add(detail);
					}
				} else {
					model.setViewName("error?msg=" + product.getBody());
					return model;
				}
			}

			if (!removal.isEmpty()) {
				jsonToList.removeAll(removal);
				String objectToJson = JsonUtils.objectToJson(jsonToList);
				CookieUtils.setCookie(request, response, cartName, objectToJson, true);
			}
			model.addObject("items", jsonToList);
			model.addObject("products", products);
			model.setViewName("checkout");
		} else {
			model.setViewName("redirect:/index");
		}

		return model;

	}

	@RequestMapping(value = { "/cart" })
	public ModelAndView pageCart(HttpServletRequest request, HttpServletResponse response, ModelAndView model) {
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);

		if (!StringUtils.isEmpty(cookieValue)) {
			StorageResult<Setting> setting = settingRemoteService.getSetting();
			if(!setting.isSuccess())
			{
				model.setViewName("redirect:/error?error=server unavailiable");
				return model;
			}
			Float currencyRate = setting.getResult().getCurrencyRate();
			
			List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
			List<ProductDetail> products = new ArrayList<>(jsonToList.size());
			List<CustomCart> removal = new ArrayList<>();
			for (CustomCart customCart : jsonToList) {
				ResponseEntity<String> product = productService.getProduct(customCart.getProductid());
				if (product.getStatusCode().value() == 200) {
					ProductDetail detail = JsonUtils.jsonToPojo(product.getBody(), ProductDetail.class);
					Integer quantity = detail.getProduct().getQuantity();
					if (quantity == 0 || detail.getProduct().getStatus() == PRODUCT_DELETE) {
						removal.add(customCart);
					} else {
						if (customCart.getQuantity() > quantity)
							customCart.setQuantity(quantity);
						detail.getProduct().setSellingprice((int)(detail.getProduct().getSellingprice()*currencyRate));
						products.add(detail);
					}
				} else {
					model.setViewName("error?msg=" + product.getBody());
					return model;
				}
			}

			if (!removal.isEmpty()) {
				jsonToList.removeAll(removal);
				String objectToJson = JsonUtils.objectToJson(jsonToList);
				CookieUtils.setCookie(request, response, cartName, objectToJson, true);
			}
			StorageResult<Setting> setting2 = settingRemoteService.getSetting();
			model.addObject("setting",setting2.getResult());
			model.addObject("items", jsonToList);
			model.addObject("products", products);
		}
		model.setViewName("cart");

		return model;

	}

	@RequestMapping(value = { "/userprofile" })
	public ModelAndView userprofile(HttpSession session,ModelAndView model) {
		Object attribute = session.getAttribute("user");
		model.addObject("user",attribute);
		model.setViewName("userprofile");
		return model;
	}

	@RequestMapping(value = { "/order" })

	public ModelAndView order(HttpServletRequest request, HttpServletResponse response, Customer customer,
			String comment, String paymentMethod, ModelAndView model) {
		String cookieValue = CookieUtils.getCookieValue(request, cartName, true);
		Object attribute = request.getSession().getAttribute("user");
		if (attribute != null) {
			Customer customer2 = (Customer) attribute;
			customer.setId(customer2.getId());
		} else {
			customer.setId(-1);
		}
		StorageResult<Setting> setting = settingRemoteService.getSetting();
		if(!setting.isSuccess())
		{
			model.setViewName("redirect:/error?error=server unavailiable");
			return model;
		}
		Float currencyRate = setting.getResult().getCurrencyRate();
		
		if (!StringUtils.isEmpty(cookieValue)) {
			List<CustomCart> jsonToList = JsonUtils.jsonToList(cookieValue, CustomCart.class);
			List<ProductDetail> products = new ArrayList<>(jsonToList.size());
			List<CustomCart> removal = new ArrayList<>();
			for (CustomCart customCart : jsonToList) {
				ResponseEntity<String> product = productService.getProduct(customCart.getProductid());
				if (product.getStatusCode().value() == 200) {
					ProductDetail detail = JsonUtils.jsonToPojo(product.getBody(), ProductDetail.class);
					Integer quantity = detail.getProduct().getQuantity();
					if (quantity == 0 || detail.getProduct().getStatus() == PRODUCT_DELETE) {
						removal.add(customCart);
					} else {
						if (customCart.getQuantity() > quantity)
							customCart.setQuantity(quantity);
						products.add(detail);
					}
				} else {
					model.setViewName("error?msg=" + product.getBody());
					return model;
				}
			}
			jsonToList.removeAll(removal);
			OrderWrap result = new OrderWrap();
			customer.setName(customer.getFirstName() + " " + customer.getLastName());
			result.setCustomer(customer);
			result.setDate(new Date());
			List<CustomProduct> list = new ArrayList<>();
			int index = 0;
			BigDecimal base = new BigDecimal(100);
			BigDecimal totalPrice = new BigDecimal(0);
			for (ProductDetail pd : products) {
				CustomProduct e = new CustomProduct();
				e.setProduct(pd.getProduct());
				CustomCart customCart = jsonToList.get(index);
				e.setQty(customCart.getQuantity());
				BigDecimal quan = new BigDecimal(customCart.getQuantity());
				Integer sellingprice = pd.getProduct().getSellingprice();
				sellingprice=(int) (currencyRate*sellingprice);
				BigDecimal sell = new BigDecimal(sellingprice);
				BigDecimal divide = sell.divide(base);
				pd.getProduct().setSellingprice(divide.intValue());
				BigDecimal multiply = divide.multiply(quan);
				totalPrice = totalPrice.add(multiply);
				e.setSubtotal(multiply.doubleValue());
				list.add(e);
				index++;
			}
			result.setList(list);
			StOrder order = new StOrder();
			order.setComment(comment);
			order.setPayment(paymentMethod);
			result.setOrder(order);

			result.setTotalPrice(totalPrice.doubleValue());

			OrderWrap creaOrder = remoteService.creaOrder(result);

			
			
			CookieUtils.deleteCookie(request, response, cartName);
			model.addObject("order", creaOrder);
			
	
			
			model.setViewName("order");
		} else {
			model.setViewName("redirect:/error?error=please dont re-submit");
		}

		return model;

	}

}
