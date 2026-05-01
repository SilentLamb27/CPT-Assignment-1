import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the logic for Part 1: Simple Multiplication Algorithm.
 * Implements an O(N^2) digit-by-digit multiplication matching the assignment's visual specs.
 * Tracks primitive operations specifically for algorithmic complexity mapping.
 */
public class SimpleMultiplication {

    /**
     * Inner utility class to neatly return both our calculated Total Product 
     * and the Count of Primitive Operations accrued.
     */
    static class Metrics {
        BigInteger finalResult;
        long primitiveOps;
        
        Metrics(BigInteger finalResult, long primitiveOps) {
            this.finalResult = finalResult;
            this.primitiveOps = primitiveOps;
        }
    }

    /**
     * Entry point to execute the Part 1 algorithm across our entire generated Dataset.csv.
     * @param args Command-line arguments (not dynamically used).
     */
    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        List<String> updatedLines = new ArrayList<>();
        
        System.out.println("Starting Part 1 - Simple Multiplication Process...");

        // 1. Reading our generated dataset sequentially
        try (Scanner scanner = new Scanner(new File(filePath))) {
            
            // Expected headers: n,Multiplicand,Multiplier,ExpectedProduct
            String header = scanner.nextLine();
            // Append header columns corresponding to our forthcoming calculations
            updatedLines.add(header + ",ResultPart1,OperationsPart1");
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] columns = line.split(",");
                if (columns.length < 4) continue;
                
                int n = Integer.parseInt(columns[0]);
                String multiplicandStr = columns[1];
                String multiplierStr = columns[2];
                
                // Track operations via the custom string multiplier iteration
                Metrics metrics = runMultiplication(multiplicandStr, multiplierStr, n);
                
                // Immediately bind our newly evaluated result row to the memory array 
                updatedLines.add(String.format("%s,%s,%d", line, metrics.finalResult.toString(), metrics.primitiveOps));
                
                if (n % 100 == 0) {
                    System.out.println("Calculated multiplications up to n=" + n);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error reading dataset: " + e.getMessage());
            return;
        }

        // 2. Writing back to Dataset.csv (Modifying the File)
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String l : updatedLines) {
                // Ensure correct newline encoding on output rows
                writer.append(l).append("\n");
            }
            System.out.println("Part 1 processing complete. Your Dataset.csv has been fully updated!");
        } catch (Exception e) {
            System.err.println("Error writing back to dataset: " + e.getMessage());
        }
    }

    /**
     * Executes digit-level manual simple multiplication (O(N^2)).
     * Follows strict partials & carriers spacing logic dictated by the assignment.
     * Counts primitives systematically (assignments, loops, maths, appends).
     * 
     * @param multiplicand String layout of the 'top' number argument.
     * @param multiplier   String layout of the 'bottom' number argument.
     * @param n            Count of total digits representing 'number scale' weight limits.
     * @return Metrics object encompassing numeric resolution & primitive count tracker.
     */
    public static Metrics runMultiplication(String multiplicand, String multiplier, int n) {
        long opsCounter = 0;
        
        int mLength = multiplicand.length();
        int nLength = multiplier.length();
        opsCounter += 2; 
        
        // Mathematical array to store final product digits. Max length is the sum of both lengths.
        int[] resultArr = new int[mLength + nLength];
        opsCounter += 1; 
        boolean showConsoleOutput = (n <= 10);
        if (showConsoleOutput) {
            System.out.println("---------------------------------------------------------");
            System.out.println("n = " + n);
            System.out.printf("%25s\n", multiplicand);
            System.out.print("x");
            System.out.printf("%24s\n", multiplier);
            System.out.println("-------------------------");
        }
        opsCounter += 1; 
        for (int i = nLength - 1; i >= 0; i--) {
            opsCounter += 1; 
            
            int multDigit = multiplier.charAt(i) - '0';
            opsCounter += 2; 
            
            // Only create memory-heavy StringBuilders if we actually need to print them
            StringBuilder tempPartials = showConsoleOutput ? new StringBuilder() : null;
            StringBuilder tempCarriers = showConsoleOutput ? new StringBuilder() : null;
            opsCounter += 2; 
            
            opsCounter += 1; 
            for (int j = mLength - 1; j >= 0; j--) {
                opsCounter += 1; 
                
                int baseDigit = multiplicand.charAt(j) - '0';
                opsCounter += 2; 
                
                int product = multDigit * baseDigit;
                opsCounter += 2; 
                
                int currentPartial = product % 10;
                int currentCarrier = product / 10;
                opsCounter += 4; 
                
                // Track mathematical results directly into the array based on positional offsets
                int position = i + j + 1;
                int sum = resultArr[position] + currentPartial;
                resultArr[position] = sum % 10;
                resultArr[position - 1] += sum / 10 + currentCarrier;
                opsCounter += 5; // Array assignments and shift maths
                
                if (showConsoleOutput) {
                    tempPartials.append(currentPartial);
                    tempCarriers.append(currentCarrier);
                }
                
                opsCounter += 1; 
            }
            opsCounter += 1; 
            
            // Only do String concatenation if we are printing the visual layout
            if (showConsoleOutput) {
                String partials = tempPartials.reverse().toString();
                String carriers = tempCarriers.reverse().toString();
                int indentationSpaces = (nLength - 1 - i); 
                
                String rightSidePadding = " ".repeat(indentationSpaces);
                String displayPartials = partials + rightSidePadding;
                String displayCarriers = carriers + rightSidePadding;
                
                System.out.printf("%25s   partial products for (=%s x %d)\n", displayPartials, multiplicand, multDigit);
                
                if (i == 0) {
                    System.out.print("+");
                    System.out.printf("%24s   carriers for (=%s x %d)\n", displayCarriers, multiplicand, multDigit);
                } else {
                    System.out.printf("%25s   carriers for (=%s x %d)\n", displayCarriers, multiplicand, multDigit);
                }
            }
            
            opsCounter += 1; 
        }
        opsCounter += 1; 
        
        // Finalize: Convert the array to a String, and then to BigInteger just ONCE at the end
        StringBuilder finalResultStr = new StringBuilder();
        for (int digit : resultArr) {
            // Ignore leading zeros
            if (!(finalResultStr.length() == 0 && digit == 0)) {
                finalResultStr.append(digit);
            }
        }
        BigInteger totalResult = finalResultStr.length() == 0 ? BigInteger.ZERO : new BigInteger(finalResultStr.toString());
        opsCounter += 2; 
        
        if (showConsoleOutput) {
            System.out.println("-------------------------");
            System.out.printf("%25s\n", totalResult.toString());
            System.out.println("=========================\n");
        }
        
        opsCounter += 1; 
        return new Metrics(totalResult, opsCounter);
    }
}
