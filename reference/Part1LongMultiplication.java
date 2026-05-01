import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

public class Part1LongMultiplication {
    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        List<String> lines = new ArrayList<>();

        // Add a new header with Result and PrimitiveOperations columns
        lines.add("n,Multiplicand,Multiplier,Product,Result(Part 1),PrimitiveOperations(Part 1)\n");

        // Read the data from the CSV file
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip header line
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                int n = Integer.parseInt(data[0]);
                BigInteger multiplicand = new BigInteger(data[1]);
                BigInteger multiplier = new BigInteger(data[2]);
                BigInteger product = new BigInteger(data[3]);

                // Perform multiplication and get the result with the operation count
                MultiplicationResult multiplicationResult = multiply(multiplier, multiplicand, n);

                // Add the result row to the lines list
                lines.add(String.format("%d,%s,%s,%s,%d,%d", n, multiplicand, multiplier, product, multiplicationResult.result, multiplicationResult.operationCount));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write the updated lines back to the CSV file
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Class to hold the result and operation count
    static class MultiplicationResult {
        BigInteger result;
        int operationCount;

        MultiplicationResult(BigInteger result, int operationCount) {
            this.result = result;
            this.operationCount = operationCount;
        }
    }

    // Multiply function to calculate the product and count operations
public static MultiplicationResult multiply(BigInteger multiplier, BigInteger multiplicand, int n) {
    if (n <= 10){
        // Regular formatted output for n <= 10
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LINE BREAK~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~·~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.println("Number of digits, n: " + n);
        System.out.println("Multiplicand: " + multiplicand);
        System.out.println("Multiplier: " + multiplier + "\n");
        System.out.println(String.format("%" + 25 + "s", multiplicand));
        System.out.print("x");
        System.out.println(String.format("%" + 24 + "s", multiplier));
        System.out.println("_____________________________");
    }

    // Result container for final assembly
    BigInteger result = BigInteger.ZERO;
    int operationCount = 0; // Initialize operation counter

    String multiplierStr = multiplier.toString();
    String multiplicandStr = multiplicand.toString();
    operationCount += 4; // For parsing multiplier and multiplicand

    int partialCount = 0;
    int carrierCount = 1;
    operationCount += 2; // For the initialization of partialCount and carrierCount

    /*
    For loop:
     * Initialization = 3
     * Condition check = n+1
     * Decrement = 2n
     */
    operationCount += 3; // For initialization of i
    for (int i = multiplicandStr.length() - 1; i >= 0; i--) {
        int digitM = Character.digit(multiplicandStr.charAt(i), 10);
        operationCount += 3; // For character access

        StringBuilder partials = new StringBuilder();
        StringBuilder carriers = new StringBuilder();
        operationCount +=4 ; // For two StringBuilder initializations

        /*
         * For loop:
         * Initialization = 3
         * Condition check = n+1
         * Decrement = 2n
         */
        operationCount += 3; // For initialization of j
        for (int j = multiplierStr.length() - 1; j >= 0; j--) {
            int digitN = Character.digit(multiplierStr.charAt(j), 10);
            operationCount += 3; // For character access, digit conversion, and assignment

            BigInteger product = BigInteger.valueOf(digitM).multiply(BigInteger.valueOf(digitN));
            operationCount += 4; // For multiplication and assignment

            partials.append(product.mod(BigInteger.TEN));
            carriers.append(product.divide(BigInteger.TEN));
            operationCount += 4; // For two append operations and arithmetic operations

            operationCount++; // For the loop condition check
            operationCount += 2; // For the decrement j--
        }
        operationCount++; // For the final condition check in the inner loop

        if (n <= 10) {
            // Print partial results
            System.out.print(String.format("%" + 25 + "s", partials.reverse().toString() + " ".repeat(partialCount)));
            System.out.println("\t" + "partial products for (" + multiplicand + " x " + multiplierStr.charAt(i) + ")");

            // Print carrier results
            if (i == 0) { // For the last carrier, print the plus sign
                System.out.print("+");
                System.out.print(String.format("%" + 24 + "s", carriers.reverse().toString() + " ".repeat(carrierCount)));
                System.out.println("\t" + "carriers for (" + multiplicand + " x " + multiplierStr.charAt(i) + ")");
            } else {
                System.out.print(String.format("%" + 25 + "s", carriers.reverse().toString() + " ".repeat(carrierCount)));
                System.out.println("\t" + "carriers for (" + multiplicand + " x " + multiplierStr.charAt(i) + ")");
            }
        }
        partialCount++;
        carrierCount++;

        // Accumulate the final result considering both partials and carries
        BigInteger partialValue = new BigInteger(partials.toString()).multiply(BigInteger.TEN.pow(multiplicandStr.length() - 1 - i));
        BigInteger carrierValue = new BigInteger(carriers.toString()).multiply(BigInteger.TEN.pow(multiplicandStr.length() - i));
        result = result.add(partialValue).add(carrierValue);
        operationCount += 15; // For partialValue and carrierValue calculation
        operationCount += 3; // For the addition and assignment

        operationCount++; // For the loop condition check
        operationCount += 2; // For the decrement i--
    }
    operationCount++; // For the final condition check in the outer loop

    if (n <= 10) {
        System.out.println("_____________________________");
        System.out.println(String.format("%" + 25 + "s", result));
        System.out.println("=============================\n");
        if (n == 10) {
            System.out.println("Check CSV file for the complete result. Please wait\n");
        }
    }

    if (n == 1000) {
        System.out.println("Done with the multiplication of 1000-digit numbers\n");
    }

    // Return both the result and the operation count using the new class
    operationCount++; // For the return statement
    return new MultiplicationResult(result, operationCount);
}
}
