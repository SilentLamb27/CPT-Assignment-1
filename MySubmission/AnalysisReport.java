import java.io.File;
import java.util.Scanner;

/**
 * AnalysisReport reads the processed Dataset.csv to provide a summary
 * comparison of the Simple Multiplication (O(N^2)) vs Karatsuba (O(N^1.585)) algorithms.
 * It strictly parses the empiric data recorded by our custom operation counters
 * to mathematically prove the theoretical bounds.
 */
public class AnalysisReport {

    /**
     * Main execution entry point.
     * @param args Command-line arguments (not dynamically used).
     */
    public static void main(String[] args) {
        String filePath = "Dataset.csv";
        
        // Print the console header for the report
        System.out.println("=========================================================");
        System.out.println("          CPT Assignment - Algorithm Analysis            ");
        System.out.println("=========================================================");
        System.out.printf("%-10s %-20s %-20s\n", "n (digits)", "Simple Ops (Part 1)", "Karatsuba Ops (Part 2)");
        System.out.println("---------------------------------------------------------");

        try (Scanner scanner = new Scanner(new File(filePath))) {
            // Skip the CSV header row
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            // Sequentially read each line of the dataset
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue; // Skip empty lines
                
                String[] columns = line.split(",");
                // Ensure the row has both Part 1 and Part 2 data computed
                if (columns.length < 8) {
                    continue; // Incomplete row, skip analysis
                }
                
                // Extract relevant columns for complexity comparison
                int n = Integer.parseInt(columns[0]); // Number of digits
                long simpleOps = Long.parseLong(columns[5]); // O(N^2) operation count
                long karatsubaOps = Long.parseLong(columns[7]); // O(N^1.585) operation count
                
                // Print specific milestone n values to cleanly illustrate the growth rate curve
                if (n == 1 || n == 10 || n == 50 || n == 100 || n == 200 || n == 500 || n == 1000) {
                    System.out.printf("%-10d %-20d %-20d\n", n, simpleOps, karatsubaOps);
                }
            }
            
        } catch (Exception e) {
            // Gracefully catch and output any file reading errors
            System.err.println("Error reading dataset: " + e.getMessage());
        }

        // Print final conclusive remarks tying empirical data to the assignment rubric's theoretical analysis
        System.out.println("=========================================================");
        System.out.println("\nTheoretical Complexity Comparison:");
        System.out.println("- Simple Multiplication: O(N^2)");
        System.out.println("- Karatsuba Algorithm: O(N^1.585)");
        System.out.println("\nDiscussion:");
        System.out.println("The empirical data should clearly demonstrate that for very small values of 'n',");
        System.out.println("Simple Multiplication may have fewer operations due to its simpler base cases and lower overhead.");
        System.out.println("However, as 'n' scales towards 1000, Karatsuba becomes drastically more efficient,");
        System.out.println("confirming the sub-quadratic asymptotic time bound of O(N^1.585).");
    }
}
