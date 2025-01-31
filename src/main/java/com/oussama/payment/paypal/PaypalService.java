package com.oussama.payment.paypal;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.api.payments.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service

public class PaypalService {
private final APIContext apiContext;

    public PaypalService(com.paypal.base.rest.APIContext apiContext) {
        this.apiContext = apiContext;
    }

    public Payment creatPayment(
        Double total,
        String currency,
        String method,
        String intent,
        String description,
        String cancelUrl,
        String successUrl
) throws PayPalRESTException {
   Amount amount=new Amount();
   amount.setCurrency(currency);
   amount.setTotal(String.format(Locale.forLanguageTag(currency),"%.2f",total));

   Transaction transaction=new Transaction();
   transaction.setAmount(amount);
   transaction.setDescription(description);

   List<Transaction> transactions=new ArrayList<>();
   transactions.add((transaction));


   Payer payer=new Payer();
   payer.setPaymentMethod(method);

   Payment payment=new Payment();
   payment.setIntent(intent);
   payment.setPayer(payer);
   payment.setTransactions(transactions);

   RedirectUrls redirectUrls=new RedirectUrls();
   redirectUrls.setCancelUrl(cancelUrl);
   redirectUrls.setReturnUrl(successUrl);

   payment.setRedirectUrls(redirectUrls);
   return payment.create(apiContext);
}
public Payment excutePayment(
        String paymentId,
        String payerId
) throws PayPalRESTException {
   Payment payment=new Payment();
   payment.setId(paymentId);

   PaymentExecution execution=new PaymentExecution();
   execution.setPayerId(payerId);

   return payment.execute(apiContext,execution);
}
}
