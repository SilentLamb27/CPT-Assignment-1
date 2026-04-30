import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the logic for Part 2: Karatsuba Algorithm.
 * Implements the Karatsuba multiplication technique utilizing java.math.BigInteger 
 * to recursively process extraordinarily large N-digit numbers efficiently.
 * Embeds operation trackers explicitly mapped for algorithmic complexity proofing.
 */
public class Karatsuba {

    /**
     * Inner utility class to bundle the mathematical product and the exact count 
     * of primitive operations required to reach that product.
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
     * Main execution process that consumes the dataset generated in Part 1.
     * Evaluates every row using the Karatsuba method and explicitly binds 
     * the results and operation metrics back to the CSV records.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        List<String> updatedLines = new ArrayList<>();
        
        System.out.println("Starting Part 2 - Karatsuba Processing...");

        // Phase 1: Read and process the generated Dataset
        try (Scanner scanner = new Scanner(new File(filePath))) {
            
            // Validate headers and attach Part 2 columns dynamically
            String header = scanner.nextLine();
            if (!header.contains("ResultPart2")) {
                updatedLines.add(header + ",ResultPart2,OperationsPart2");
            } else {
                updatedLines.add(header);
            }
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] columns = line.split(",");
                if (columns.length < 4) continue;
                
                int n = Integer.parseInt(columns[0]);
                String multiplicandStr = columns[1];
                String multiplierStr = columns[2];
                
                // Construct immutable BigIntegers for huge digit lengths handling
                BigInteger x = new BigInteger(multiplicandStr);
                BigInteger y = new BigInteger(multiplierStr);
                
                // Execute Karatsuba and trace operation metrics
                Metrics metrics = mult(x, y);
                
                // Format the updated rows correctly depending on current CSV state
                if (columns.length == 6) {
                    // Normal state: Part 1 completed previously
                    updatedLines.add(String.format("%s,%s,%d", line, metrics.finalResult.toString(), metrics.primitiveOps));
                } else if (columns.length == 8) {
                    // Overwrite state: Already processed part 2 previously, update it
                    columns[6] = metrics.finalResult.toString();
                    columns[7] = String.valueOf(metrics.primitiveOps);
                    updatedLines.add(String.join(",", columns));
                } else {
                    // Fallback state: Only 4 columns exist (Part 1 was skipped)
                    updatedLines.add(String.format("%s,,,%s,%d", line, metrics.finalResult.toString(), metrics.primitiveOps));
                }
                
                // Console updates for prolonged massive executions
                if (n % 100 == 0) {
                    System.out.println("Calculated multiplications up to n=" + n);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error reading dataset: " + e.getMessage());
            return;
        }

        // Phase 2: Overwrite the CSV dataset seamlessly retaining all column integrity
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String l : updatedLines) {
                writer.append(l).append("\n");
            }
            System.out.println("Part 2 processing complete. Your Dataset.csv has been fully updated!");
        } catch (Exception e) {
            System.err.println("Error writing back to dataset: " + e.getMessage());
        }
    }

    /**
     * Executes the recursive Karatsuba O(N^1.585) multiplication algorithm.
     * Continuously fractures the BigInteger values into halves mathematically 
     * resolving in a sub-quadratic manner.
     * 
     * @param x The first BigInteger operand
     * @param y The second BigInteger operand
     * @return Metrics object encompassing numeric resolution & primitive count tracker
     */
    public static Metrics mult(BigInteger x, BigInteger y) {
        long opsCounter = 0;
        
        opsCounter += 2; // conditions evaluation
        // Base Condition: Fall back to standard multiplication if numbers are single digit
        if (x.compareTo(BigInteger.TEN) < 0 && y.compareTo(BigInteger.TEN) < 0) {
            opsCounter += 1; // direct multiply operation
            return new Metrics(x.multiply(y), opsCounter);
        }

        opsCounter += 2; // getting string representations to find length natively
        int noOneLength = x.toString().length();
        int noTwoLength = y.toString().length();
        
        // Remove negative signs length contribution if they strictly exist 
        if (x.signum() == -1) noOneLength--;
        if (y.signum() == -1) noTwoLength--;

        opsCounter += 3; // finding max bounds and strictly calculating halves splits
        int maxNumLength = Math.max(noOneLength, noTwoLength);
        int halfMaxNumLength = (maxNumLength / 2) + (maxNumLength % 2);

        opsCounter += 1; // 10^(N/2) shift scaling multiplier calculation
        BigInteger maxNumLengthTen = BigInteger.TEN.pow(halfMaxNumLength);

        opsCounter += 4; // divide and remainder array bound assignments
        // Split X into upper half (a) and lower half (b)
        BigInteger[] xSplit = x.divideAndRemainder(maxNumLengthTen);
        BigInteger a = xSplit[0];
        BigInteger b = xSplit[1];

        // Split Y into upper half (c) and lower half (d)
        BigInteger[] ySplit = y.divideAndRemainder(maxNumLengthTen);
        BigInteger c = ySplit[0];
        BigInteger d = ySplit[1];

        opsCounter += 2; // prerequisite mathematical additions for computing z1 accurately
        BigInteger aPlusB = a.add(b);
        BigInteger cPlusD = c.add(d);

        opsCounter += 3; // Initializing three recursive function calls bounds (Sub-quadratic signature step)
        Metrics z0Metrics = mult(a, c);
        Metrics z1Metrics = mult(aPlusB, cPlusD);
        Metrics z2Metrics = mult(b, d);
        
        // Add up operations dynamically returning from inner recursive tree layers
        opsCounter += z0Metrics.primitiveOps + z1Metrics.primitiveOps + z2Metrics.primitiveOps;

        // Map computed products into local scopes
        BigInteger z0 = z0Metrics.finalResult;
        BigInteger z1 = z1Metrics.finalResult;
        BigInteger z2 = z2Metrics.finalResult;

        opsCounter += 8; // two subtractions, two multiplications (powers of 10), two additions 
        
        // Construct final mathematical formulation: z0 * 10^(2 * half) + (z1 - z0 - z2) * 10^(half) + z2
        BigInteger term1 = z0.multiply(BigInteger.TEN.pow(halfMaxNumLength * 2));
        BigInteger z1_z0_z2 = z1.subtract(z0).subtract(z2);
        BigInteger term2 = z1_z0_z2.multiply(BigInteger.TEN.pow(halfMaxNumLength));
        BigInteger ans = term1.add(term2).add(z2);

        return new Metrics(ans, opsCounter);
    }
}
