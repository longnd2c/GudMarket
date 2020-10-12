package com.gudmarket.web.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gudmarket.web.config.PaypalPaymentIntent;
import com.gudmarket.web.config.PaypalPaymentMethod;
import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.SocialAccount;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.SocialAccountRepository;
import com.gudmarket.web.service.PaypalService;
import com.gudmarket.web.utils.WebUtils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

@Controller
public class PaymentController {
	public static final String URL_PAYPAL_SUCCESS = "pay/success";
	public static final String URL_PAYPAL_CANCEL = "pay/cancel";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaypalService paypalService;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private SocialAccountRepository socialAccRepo;
	
	
	@PostMapping("/pay")
	public String pay(HttpServletRequest request,@RequestParam("price") double price ){
		String cancelUrl = WebUtils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
		String successUrl = WebUtils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
		try {
			Payment payment = paypalService.createPayment(
					price, 
					"USD", 
					PaypalPaymentMethod.paypal, 
					PaypalPaymentIntent.sale,
					"payment description", 
					cancelUrl, 
					successUrl);		
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					return "redirect:" + links.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}

	@GetMapping(URL_PAYPAL_CANCEL)
	public String cancelPay(){
		return "cancelPayPal";
	}

	@GetMapping(URL_PAYPAL_SUCCESS)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, Principal principal){
		try {
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if(payment.getState().equals("approved")){
				List<Transaction> listTrans=payment.getTransactions();
				for(Transaction t:listTrans) {
					 User loginedUser = (User) ((Authentication) principal).getPrincipal();
					String username=loginedUser.getUsername();
					if(username.contains("@")) {
						SocialAccount socialAcc=socialAccRepo.findByEmail(username);	
						socialAcc.setMoney(Double.parseDouble(t.getAmount().getTotal()));
						socialAccRepo.save(socialAcc);
					}
					else {
						Account user= accRepo.findByUsername(username);
						user.setMoney(Double.parseDouble(t.getAmount().getTotal()));
						accRepo.save(user);
					}
					
				}
				return "successPayPal";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}
}
