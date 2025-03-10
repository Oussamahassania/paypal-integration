package com.oussama.payment.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Slf4j
public class PaypalController {
    private final PaypalService paypalService;

    public PaypalController(PaypalService paypalService) {
        this.paypalService = paypalService;
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }
    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method")String method,
            @RequestParam("amount")String amount,
            @RequestParam("description")String description,
            @RequestParam("currency")String currency
    ) throws PayPalRESTException {

      String cancelUrl="http://localhost:8080/payment/cancel";
      String successUrl="http://localhost:8080/payment/success";
            Payment payment=paypalService.creatPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl,
                    successUrl
            );
            for(Links links:payment.getLinks()){
                if(links.getRel().equals(("approval_url"))){
                    return new RedirectView(links.getHref());
                }
            }
        return new RedirectView("/payment/error");
    }
    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ){
        try{
         Payment payment=paypalService.excutePayment(paymentId,payerId);
         if(payment.getState().equals("approved")){
             return "paymentSuccess";
         }
        } catch (com.paypal.base.rest.PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        return "paymentSuccess";
    }
    @GetMapping("/payment/cancel")
    public String paymentCancel(){
        return "paymentCancel";
    }
    @GetMapping("/payment/error")
    public String paymentError(){
        return "paymentError";
    }

}
