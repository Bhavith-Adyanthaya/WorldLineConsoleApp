package org.worldlineConsole;

import com.six.timapi.*;
import com.six.timapi.constants.*;

public class App
{
    public static void main ( String[] args ) throws TimException
    {
        TerminalSettings settings = new TerminalSettings();
        settings.setTerminalId("25496444");
        settings.setAutoCommit(false);
        // More comment
        // test comment
        settings.setIntegratorId("0e6b1705-ab96-455b-9ba3-a77dd919d7a5");
        Terminal terminal = new com.six.timapi.Terminal(settings);

        // Check if the terminal is ready to receive a transaction
        if(terminal.getTerminalStatus().getTransactionStatus() == TransactionStatus.IDLE)
        {
            TransactionResponse response = terminal.transaction(TransactionType.PURCHASE, new Amount( 14.00, Currency.CHF));
            //terminal.transactionAsync(TransactionType.PURCHASE, new Amount(14.00, Currency.CHF));
        }
    }
}
