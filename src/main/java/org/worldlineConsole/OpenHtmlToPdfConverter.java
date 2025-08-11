package org.worldlineConsole;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.*;

public class OpenHtmlToPdfConverter {

    public static void convertHtmlToPdf(String html, String outputPath) {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null); // HTML content and base URI (null if not using relative paths)
            builder.toStream(os);
            builder.run();
            System.out.println("PDF created at: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String html = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <style>\n    @page {\n      size: A4;\n      margin: 20mm;\n      @bottom-center {\n        content: element(footer);\n      }\n    }\n\n    body {\n      font-size: 11px;\n      margin: 0;\n      padding: 0;\n    }\n\n    table {\n      width: 100%;\n      border-collapse: collapse;\n      font-size: 8px;\n    }\n\n    th, td {\n      border: 0.5px solid black;\n      padding: 4px;\n      word-break: break-word;\n      white-space: pre-wrap;\n    }\n\n    thead {\n      display: table-header-group;\n    }\n\n    /* This is the footer to be rendered at the bottom */\n    #footer {\n      display: block;\n      position: running(footer);\n    }\n\n    /* Footer table with no borders */\n    .footer-table {\n      width: 100%;\n      border-collapse: collapse;\n      font-size: 9px;\n    }\n\n    .footer-table td {\n      border: none !important;\n      padding: 6px 4px;\n    }\n  </style>\n</head>\n<body>\n\n<!-- Packing List Table (kept unchanged) -->\n<table>\n  <thead>\n    <tr>\n      <th colspan=\"8\" style=\"text-align: center; font-size: 11px;\">Packing List</th>\n    </tr>\n    <tr>\n      <th style=\"width:35%;\">Barcode</th>\n      <th style=\"width:15%;\">Customer Name</th>\n      <th style=\"width:15%;\">Payment Status</th>\n      <th style=\"width:15%;\">Shipment Number</th>\n      <th style=\"width:15%;\">Tracking Number</th>\n      <th style=\"width:15%;\">Delivery Method</th>\n      <th style=\"width:15%;\">Preferred Delivery Date</th>\n      <th style=\"width:5%;\">Number of Packages</th>\n    </tr>\n  </thead>\n\n  {{#$target/shipments/hits}}\n  {{#./hits}}\n  {{#./_source/$uri/makeLink()}}\n  <tbody>\n    <tr>\n      <td style=\"text-align: center;\">\n        <div style=\"padding: 10px 0;\">\n          <img src=\"https://xypop-oms.xyretail.com/_ra{{./trackingNumberBarcodeImage/AbsoluteURL}}\" alt=\"Barcode\" />\n        </div>\n      </td>\n      <td>{{./shipTo/address/firstName}} {{./shipTo/address/lastName}}</td>\n      <td>{{./paymentStatus}}</td>\n      <td>{{./id}}</td>\n      <td>{{./shipmentTracking/trackingNumber}}</td>\n      <td>{{./shipmentOrderLine/shipOption/name}}</td>\n      <td>{{./shipmentOrderLine/preferredDeliveryDate}} {{./shipmentOrderLine/preferredDeliveryHours}}</td>\n      <td>{{./popOMSHandlingUnits/size()}}</td>\n    </tr>\n  </tbody>\n  {{/}}\n  {{/}}{{/}}\n</table>\n\n<!-- Footer Element: Will be picked up by running(footer) -->\n<div id=\"footer\">\n  <table class=\"footer-table\">\n    <tr>\n      <td style=\"width: 33%;\">Date</td>\n      <td style=\"width: 33%;\">Number Of Parcels</td>\n      <td style=\"width: 33%;\">Signature</td>\n    </tr>\n  </table>\n</div>\n\n</body>\n</html>\n";

        String outputFilePath = "output.pdf";
        convertHtmlToPdf(html, outputFilePath);
    }
}

