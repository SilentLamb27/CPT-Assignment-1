import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * DatasetGenerator is responsible for generating the initial Dataset.csv file.
 * It creates pairs of random 'n' length BigIntegers and their theoretically expected products
 * to test the accuracy of our multiplication algorithms later.
 */
public class DatasetGenerator {

    /**
     * Main execution method that writes 1,000 randomized pairs of numbers to Dataset.csv.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        System.out.println("Generating Dataset...");
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write standard CSV header outlining the initial column structure
            writer.append("n,Multiplicand,Multiplier,ExpectedProduct\n");

            // Loop and generate test parameters for digit lengths from 1 to 1000
            for (int n = 1; n <= 1000; n++) {
                // Generate two distinct random numbers of strictly length 'n'
                BigInteger multiplicand = generateNLengthNumber(n);
                BigInteger multiplier = generateNLengthNumber(n);
                
                // Automatically calculate true baseline product to compare with our manual algorithms
                BigInteger expectedProduct = multiplicand.multiply(multiplier);

                // Write the gathered dataset row identically formatted
                writer.append(String.format("%d,%s,%s,%s\n", n, multiplicand.toString(), multiplier.toString(), expectedProduct.toString()));
                
                // Periodic console update to ensure progress isn't stalled visually
                if (n % 100 == 0) {
                    System.out.println("Generated n=" + n);
                }
            }
            System.out.println("Dataset successfully generated at: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing dataset: " + e.getMessage());
        }
    }

    /**
     * Helper method to generate a randomized BigInteger that strictly adheres 
     * to the desired length of digits 'n', avoiding leading zeroes.
     * @param length The exact amount of digits the generated number must possess.
     * @return BigInteger representing the newly generated random value.
     */
    private static BigInteger generateNLengthNumber(int length) {
        if (length <= 0) return BigInteger.ZERO;
        
        Random rng = new Random();
        StringBuilder numBuilder = new StringBuilder(length);
        
        // Ensure first digit isn't zero (1-9) to maintain precise overall length
        numBuilder.append(rng.nextInt(9) + 1);
        
        // Populate the remaining (length - 1) digits completely randomly (0-9)
        for (int i = 1; i < length; i++) {
            numBuilder.append(rng.nextInt(10));
        }
        
        return new BigInteger(numBuilder.toString());
    }
}
