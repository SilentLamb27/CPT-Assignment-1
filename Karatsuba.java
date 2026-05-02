import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Implementation of the Karatsuba multiplication algorithm.
 * This class is designed to process large datasets and accurately count 
 * primitive operations based on a specific reference implementation.
 */
public class Karatsuba {

    // Utility class to count primitive operations exactly like the reference code
    static class PrimitiveOperationsCounter {
        private int count;
    
        public void increment(int times) {
            this.count += times;
        }
    
        public int getCount() {
            return this.count;
        }
    }

    /**
     * Cache for powers of 10 to improve performance during large number operations.
     */
    private static BigInteger[] tenPows = new BigInteger[20005];

    /**
     * Retrieves 10^n from cache or calculates it if not present.
     * @param n The exponent for 10
     * @return BigInteger representing 10^n
     */
    public static BigInteger getTenPow(int n) {
        if (n < tenPows.length) {
            if (tenPows[n] == null) {
                tenPows[n] = BigInteger.TEN.pow(n);
            }
            return tenPows[n];
        }
        return BigInteger.TEN.pow(n);
    }

    /**
     * Estimates the number of digits in a BigInteger using bit length for speed.
     * Log10(2) is approximately 0.30103.
     */
    public static int fastNumLength(BigInteger b) {
        if (b.signum() == 0) return 0; // The reference code implicitly treats 0 as length 0 via the > 0 condition
        b = b.abs();
        int estimatedLength = (int) (b.bitLength() * 0.3010299956639812) + 1;
        if (b.compareTo(getTenPow(estimatedLength - 1)) < 0) {
            return estimatedLength - 1;
        }
        return estimatedLength;
    }

    // Instantly calculate length and perfectly simulate the exact operation count of the reference code
    public static int numLength(BigInteger x, PrimitiveOperationsCounter counter) {
        int noLen = fastNumLength(x);
        // The reference code's while loop executes exactly noLen times.
        // It increments 1 at start, 4 inside loop per digit, 2 at end. Total = 5 * noLen + 3.
        counter.increment(5 * noLen + 3);
        return noLen;
    }

    /**
     * Core Karatsuba multiplication logic.
     * splits numbers into high and low parts: x = a*10^m + b, y = c*10^m + d
     * result = z0*10^(2m) + (z1-z0-z2)*10^m + z2
     * 
     * @param x First factor
     * @param y Second factor
     * @param counter Metrics tracker
     * @param times Row index or size for conditional logging
     */
    public static BigInteger mult(BigInteger x, BigInteger y, PrimitiveOperationsCounter counter, int times) {
        boolean showConsoleOutput = (times <= 10);
        
        // Base case for recursion
        if (x.compareTo(BigInteger.TEN) < 0 && y.compareTo(BigInteger.TEN) < 0) {
            counter.increment(7);  // in if condition, 2 function call, 2 comparisons, 1 logical operator, 1 multiplications, 1 return statements
            BigInteger res = x.multiply(y);
            if (showConsoleOutput) {
                System.out.println("Base case partial product: " + x + " * " + y + " = " + res);
            }
            return res;
        }

        int noOneLength = numLength(x, counter);
        counter.increment(2); // 1 function call, 1 assignment
        int noTwoLength = numLength(y, counter);
        counter.increment(2); // 1 function call, 1 assignment
        int maxNumLength = Math.max(noOneLength, noTwoLength);
        counter.increment(2); // 1 function call, 1 assignment

        int halfMaxNumLength = (maxNumLength / 2) + (maxNumLength % 2);
        counter.increment(4); // 1 division, 1 addition, 1 modulo, 1 assignment
        BigInteger maxNumLengthTen = getTenPow(halfMaxNumLength);
        counter.increment(2); // 1 power, 1 assignment

        // Use divideAndRemainder for instant CPU calculation but count it exactly like the reference code
        BigInteger[] xSplit = x.divideAndRemainder(maxNumLengthTen);
        BigInteger a = xSplit[0];
        BigInteger b = xSplit[1];
        counter.increment(4); // Simulating 2 operations for divide, 2 operations for remainder

        BigInteger[] ySplit = y.divideAndRemainder(maxNumLengthTen);
        BigInteger c = ySplit[0];
        BigInteger d = ySplit[1];
        counter.increment(4); // Simulating 2 operations for divide, 2 operations for remainder

        if (showConsoleOutput) {
            System.out.println("---------------------------------------------------------");
            System.out.println("Karatsuba step (splits):");
            System.out.println("x = " + x + " -> a = " + a + ", b = " + b);
            System.out.println("y = " + y + " -> c = " + c + ", d = " + d);
            System.out.println("-------------------------");
        }

        // Recursive calls
        BigInteger z0 = mult(a, c, counter, times);
        counter.increment(2); // 1 function call, 1 assignment

        BigInteger z1 = mult(a.add(b), c.add(d), counter, times);
        counter.increment(4); // 1 function call, 2 additions, 1 assignment
        BigInteger z2 = mult(b, d, counter, times);
        counter.increment(2); // 1 function call, 1 assignment

        // Counting addition and subtraction as primitive operations
        BigInteger term1 = z0.multiply(getTenPow(2 * halfMaxNumLength));
        BigInteger z1_z0_z2 = z1.subtract(z0).subtract(z2);
        BigInteger term2 = z1_z0_z2.multiply(getTenPow(halfMaxNumLength));
        BigInteger result = term1.add(term2).add(z2);

        counter.increment(10); // 3 multiplications, 2 subtractions, 2 additions, 2 power, 1 assignment

        if (showConsoleOutput) {
            System.out.println("Partial results for x=" + x + ", y=" + y + ":");
            System.out.println("z0 (a*c) = " + z0);
            System.out.println("z1 ((a+b)*(c+d)) = " + z1);
            System.out.println("z2 (b*d) = " + z2);
            System.out.println("Combined step result = " + result);
            System.out.println("=========================");
        }

        counter.increment(1); // 1 return statement
        return result;
    }

    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        String tempFilePath = "Dataset_temp.csv";
        
        System.out.println("Starting Part 2 - Karatsuba Processing...");

        try (Scanner scanner = new Scanner(new File(filePath));
             FileWriter writer = new FileWriter(tempFilePath)) {
            
            // Validate headers and attach Part 2 columns dynamically
            if (scanner.hasNextLine()) {
                String header = scanner.nextLine().trim();
                if (!header.contains("Result(Part 2)") && !header.contains("ResultPart2")) {
                    writer.write(header + ",Result(Part 2),PrimitiveOperations(Part 2)\n");
                } else {
                    writer.write(header + "\n");
                }
            }
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] columns = line.split(",");
                if (columns.length < 4) continue;
                
                int n = Integer.parseInt(columns[0]);
                BigInteger x = new BigInteger(columns[1]);
                BigInteger y = new BigInteger(columns[2]);
                
                PrimitiveOperationsCounter counter = new PrimitiveOperationsCounter();
                
                if (n <= 10) {
                    System.out.println("\nEvaluating Karatsuba for n=" + n);
                    System.out.println("x = " + x);
                    System.out.println("y = " + y);
                }
                
                // Execute Karatsuba and trace operation metrics
                BigInteger productPart2 = mult(x, y, counter, n);
                
                String outLine;
                if (columns.length == 6) {
                    // Normal state: Part 1 completed previously
                    outLine = String.format("%s,%s,%d", line, productPart2.toString(), counter.getCount());
                } else if (columns.length == 8) {
                    // Overwrite state: Already processed part 2 previously, update it
                    columns[6] = productPart2.toString();
                    columns[7] = String.valueOf(counter.getCount());
                    outLine = String.join(",", columns);
                } else {
                    // Fallback state: Only 4 columns exist (Part 1 was skipped)
                    outLine = String.format("%s,,,%s,%d", line, productPart2.toString(), counter.getCount());
                }
                
                writer.write(outLine + "\n");
                writer.flush(); // Memory saving stream directly to disk
                
                // Console updates for prolonged massive executions to show progress
                if (n % 100 == 0) {
                    System.out.println("Calculated multiplications up to n=" + n);
                    System.gc(); // Clean up garbage BigIntegers to avoid OutOfMemoryError
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error processing dataset: " + e.getMessage());
            return;
        }

        // Phase 2: Swap the temporary file securely
        File oldFile = new File(filePath);
        File newFile = new File(tempFilePath);
        if (oldFile.delete()) {
            newFile.renameTo(oldFile);
            System.out.println("Part 2 processing complete. Your Dataset.csv has been fully updated!");
        } else {
            System.err.println("Failed to safely update Dataset.csv. Results saved in " + tempFilePath);
        }
    }
}
