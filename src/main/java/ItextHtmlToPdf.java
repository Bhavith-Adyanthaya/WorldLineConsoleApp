import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.*;

public class ItextHtmlToPdf {

    public static void main(String[] args) {
        String html = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <style>\n    @page {\n      size: A4;\n      margin: 20mm;\n      @bottom-center {\n        content: element(footer);\n      }\n    }\n\n    body {\n      font-size: 11px;\n      margin: 0;\n      padding: 0;\n    }\n\n    table {\n      width: 100%;\n      border-collapse: collapse;\n      font-size: 8px;\n    }\n\n    th, td {\n      border: 0.1px solid black;\n      padding: 4px;\n      word-break: break-word;\n      white-space: pre-wrap;\n    }\n\n    thead {\n      display: table-header-group;\n    }\n\n    /* This is the footer to be rendered at the bottom */\n    #footer {\n      display: block;\n      position: running(footer);\n    }\n\n    /* Footer table with no borders */\n    .footer-table {\n      width: 100%;\n      border-collapse: collapse;\n      font-size: 9px;\n    }\n\n    .footer-table td {\n      border: none !important;\n      padding: 6px 4px;\n    }\n  </style>\n</head>\n<body>\n\n<!-- Packing List Table (kept unchanged) -->\n<table>\n  <thead>\n    <tr>\n      <th colspan=\"8\" style=\"text-align: center; font-size: 11px;\">Packing List</th>\n    </tr>\n    <tr>\n      <th style=\"width:35%;\">Barcode</th>\n      <th style=\"width:15%;\">Customer Name</th>\n      <th style=\"width:15%;\">Payment Status</th>\n      <th style=\"width:15%;\">Shipment Number</th>\n      <th style=\"width:15%;\">Tracking Number</th>\n      <th style=\"width:15%;\">Delivery Method</th>\n      <th style=\"width:15%;\">Preferred Delivery Date</th>\n      <th style=\"width:5%;\">Number of Packages</th>\n    </tr>\n  </thead>\n\n  {{#$target/shipments/hits}}\n  {{#./hits}}\n  {{#./_source/$uri/makeLink()}}\n  <tbody>\n    <tr>\n      <td style=\"text-align: center;\">\n        <div style=\"padding: 10px 0;\">\n          <img src=\"https://xypop-oms.xyretail.com/_ra{{./trackingNumberBarcodeImage/AbsoluteURL}}\" alt=\"Barcode\" />\n        </div>\n      </td>\n      <td>{{./shipTo/address/firstName}} {{./shipTo/address/lastName}}</td>\n      <td>{{./paymentStatus}}</td>\n      <td>{{./id}}</td>\n      <td>{{./shipmentTracking/trackingNumber}}</td>\n      <td>{{./shipmentOrderLine/shipOption/name}}</td>\n      <td>{{./shipmentOrderLine/preferredDeliveryDate}} {{./shipmentOrderLine/preferredDeliveryHours}}</td>\n      <td>{{./popOMSHandlingUnits/size()}}</td>\n    </tr>\n  </tbody>\n  {{/}}\n  {{/}}{{/}}\n</table>\n\n<!-- Footer Element: Will be picked up by running(footer) -->\n<div id=\"footer\">\n  <table class=\"footer-table\">\n    <tr>\n      <td style=\"width: 33%;\">Date</td>\n      <td style=\"width: 33%;\">Number Of Parcels</td>\n      <td style=\"width: 33%;\">Signature</td>\n    </tr>\n  </table>\n</div>\n\n</body>\n</html>\n";

        try {

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();

            XMLWorkerHelper.getInstance().parseXHtml(
                    writer,
                    document,
                    new ByteArrayInputStream(html.getBytes()),
                    null,
                    null,
                    new XMLWorkerFontProvider()
            );
            document.close();


            byte[] pdfBytes = os.toByteArray();

            if (true) {
                PdfReader reader = new PdfReader(pdfBytes);
                ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
                PdfStamper stamper = new PdfStamper(reader, finalOutput);

                int lastPage = reader.getNumberOfPages();
                Rectangle pageSize = reader.getPageSize(lastPage);
                PdfContentByte cb = stamper.getOverContent(lastPage);

                Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
                Phrase footer = new Phrase("Date        Number of Parcels        Signature", font);

                ColumnText.showTextAligned(cb,
                        Element.ALIGN_CENTER,
                        footer,
                        (pageSize.getLeft() + pageSize.getRight()) / 2,
                        pageSize.getBottom() + 20, // 20 points above bottom
                        0
                );

                stamper.close();
                reader.close();

                try (OutputStream fos = new FileOutputStream("sample-output.pdf")) {
                    fos.write(finalOutput.toByteArray());
                }
            } else {
                try (OutputStream fos = new FileOutputStream("sample-output.pdf")) {
                    fos.write(pdfBytes);
                }
            }

            System.out.println("PDF created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

