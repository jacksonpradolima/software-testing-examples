package vending;

import java.io.*;
import java.util.StringTokenizer;

public class TestDriver {

    static public void main(String args[ ]) throws Exception {

        BufferedReader drvInput;
        String tcLine = new String();
        
        String methodName = new String();
        
        VendingMachine machine = new VendingMachine();

        if (args.length < 1)
            drvInput = new BufferedReader(new InputStreamReader(System.in));
        else
            drvInput = new BufferedReader(new FileReader(args[0]));

        System.out.println("VendingMachine ON");
        // Machine is ready. Reading input...
        while ((tcLine = drvInput.readLine()) != null) {
            StringTokenizer tcTokens = new StringTokenizer(tcLine);

            if (tcTokens.hasMoreTokens())
                methodName = tcTokens.nextToken();
			
            if (methodName.equals("insertCoin"))
                machine.insertCoin();
            else if (methodName.equals("returnCoin"))
                machine.returnCoin();
            else if (methodName.equals("vendItem")) {
                Integer selection = new Integer(tcTokens.nextToken());

                machine.vendItem(selection.intValue());
            }
        }
        System.out.println("VendingMachine OFF");
    }
}
