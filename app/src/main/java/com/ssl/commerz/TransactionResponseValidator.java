package com.ssl.commerz;

import java.util.Map;

public class TransactionResponseValidator {

    public boolean receiveSuccessResponse(Map<String, String> request, double expectedAmount) throws Exception {
        String trxId = request.get("tran_id");
        String currency = "BDT";

        SSLCommerz sslcz = new SSLCommerz("kindn689cad3c0e549", "kindn689cad3c0e549@ssl", true);

        return sslcz.orderValidate(trxId, String.valueOf(expectedAmount), currency, request);
    }
}
