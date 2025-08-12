package org.worldlineConsole;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.JsonNode;

// Imports needed for the SSL configuration
import kong.unirest.json.JSONObject;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import javax.net.ssl.SSLContext;

import java.util.HashMap;
import java.util.Map;

public class PlanetService {

    public PlanetService(){}

    // CONFIGURE UNIREST FOR QA/DEV ENVIRONMENT BEFORE MAKING THE CALL
    public static void configureUnirestForDevelopment() throws Exception {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustAllStrategy()) // Keep this for QA server certs
                .setProtocol("TLSv1.2") // <-- ADD THIS LINE
                .build();

        Unirest.config()
                .sslContext(sslContext)
                .hostnameVerifier(NoopHostnameVerifier.INSTANCE);
    }

    public static String getToken() throws Exception{

        // CONFIGURE UNIREST FOR QA/DEV ENVIRONMENT BEFORE MAKING THE CALL
        configureUnirestForDevelopment();

        String url = "https://auth.qa-tax.planetpayment.ae/auth/realms/planet/protocol/openid-connect/token";

        Map<String, Object> formData = new HashMap<>();
        formData.put("client_id","4985d611-4312-4e42-9d18-1522c617a98a");
        formData.put("client_secret","z2fgfWLVl8xxb628OHF7176s2YxJ7xhl");
        formData.put("grant_type","client_credentials");

        // Send the POST request with form fields
        //HttpResponse<JsonNode> response = Unirest.post(url)
        //        .fields(formData)
        //        .asJson();

        HttpResponse<String> response = Unirest.post(url)
                .header("Accept", "application/json")
                .fields(formData)
                .asString();

        // Planet feature 1

        JSONObject test = new JSONObject(response.getBody());
        return test.optString("access_token");
    }

    public static void TaxFreeRequest(String token){
        String url = "https://frontoffice.qa-tax.planetpayment.ae/services/transactions/api/v2/new-transaction";

        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(getRequestBody())
                .asJson();

        JSONObject rootObject = response.getBody().getObject();

        String number = rootObject.optJSONObject("taxRefundResponse").optString("taxRefundTagNumber");
        String buffer = rootObject.optJSONObject("taxRegisterResponse").optString("digitalReceipt");
        String test = response.getBody().toString();
    }


    public static void main(String[] args) {

        try {
            String token = getToken();
            TaxFreeRequest(token);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Unirest.shutDown();
        }
    }

    public static String getRequestBody(){
        String json = "{\n" +
                "    \"issueTaxRefundTag\": true,\n" +
                "    \"date\": \"2025-08-01\",\n" +
                "    \"receiptNumber\": \"Planet__Test___06\",\n" +
                "    \"terminal\": \"1991069\",\n" +
                "    \"taxFreeId\": \"\",\n" +
                "    \"type\": \"RECEIPT\",\n" +
                "    \"order\": {\n" +
                "        \"totalBeforeVAT\": 1400000,\n" +
                "        \"vatIncl\": 70000,\n" +
                "        \"total\": 1470000,\n" +
                "        \"items\": [\n" +
                "            {\n" +
                "                \"unitPrice\": \"200000\",\n" +
                "                \"quantity\": 3,\n" +
                "                \"netAmount\": 600000,\n" +
                "                \"vatAmount\": 30000,\n" +
                "                \"grossAmount\": 630000,\n" +
                "                \"description\": \"Item 1 description  عقد ذهب عيار 21\",\n" +
                "                \"code\": \"\",\n" +
                "                \"serialNumber\": \"\",\n" +
                "                \"vatRate\": 5,\n" +
                "                \"vatCode\": \"5\",\n" +
                "                \"merchandiseGroup\": \"104\",\n" +
                "                \"taxRefundEligible\": true\n" +
                "            },\n" +
                "            {\n" +
                "                \"unitPrice\": \"400000\",\n" +
                "                \"quantity\": 2,\n" +
                "                \"netAmount\": 800000,\n" +
                "                \"vatAmount\": 40000,\n" +
                "                \"grossAmount\": 840000,\n" +
                "                \"description\": \"Item 2 description عقد ذهب عيار 21\",\n" +
                "                \"code\": \"\",\n" +
                "                \"serialNumber\": \"\",\n" +
                "                \"vatRate\": 5,\n" +
                "                \"vatCode\": \"5\",\n" +
                "                \"merchandiseGroup\": \"104\",\n" +
                "                \"taxRefundEligible\": true\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"shopper\": {\n" +
                "        \"firstName\": \"John\",\n" +
                "        \"lastName\": \"Smith\",\n" +
                "        \"nationality\": \"IN\",\n" +
                "        \"countryOfResidence\": \"IN\",\n" +
                "        \"phoneNumber\": \"00971500000000\",\n" +
                "        \"birth\": {\n" +
                "            \"date\": \"1998-01-01\"\n" +
                "        },\n" +
                "        \"shopperIdentityDocument\": {\n" +
                "            \"type\": \"PASSPORT\",\n" +
                "            \"issuedBy\": \"IN\",\n" +
                "            \"number\": \"U2471418\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        return json;
    }
}