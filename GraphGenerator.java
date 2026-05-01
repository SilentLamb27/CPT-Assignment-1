import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class GraphGenerator {
    public static void main(String[] args) {
        List<Integer> xData = new ArrayList<>();
        List<Long> yData = new ArrayList<>();

        System.out.println("Reading Dataset.csv...");
        // Read data
        try (Scanner scanner = new Scanner(new File("Dataset.csv"))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length >= 6) {
                    try {
                        int n = Integer.parseInt(cols[0]);
                        // OperationsPart1 is column 5 (index 5)
                        long ops = Long.parseLong(cols[5]);
                        xData.add(n);
                        yData.add(ops);
                    } catch (Exception e) {
                        // Skip unparseable lines
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Could not read Dataset.csv: " + e.getMessage());
            return;
        }

        if (xData.isEmpty()) {
            System.out.println("No data found for Simple Multiplication in Dataset.csv.");
            return;
        }

        System.out.println("Generating graph...");
        // Image dimensions
        int width = 1200;
        int height = 800;
        int padding = 120;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        
        // Enable anti-aliasing for smooth lines
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        
        // Find min/max boundaries
        long minX = xData.get(0), maxX = xData.get(0);
        long minY = yData.get(0), maxY = yData.get(0);
        for (int i = 0; i < xData.size(); i++) {
            minX = Math.min(minX, xData.get(i));
            maxX = Math.max(maxX, xData.get(i));
            minY = Math.min(minY, yData.get(i));
            maxY = Math.max(maxY, yData.get(i));
        }
        
        if (maxY == minY) maxY = minY + 1;
        if (maxX == minX) maxX = minX + 1;

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2.drawLine(padding, padding, padding, height - padding); // Y-axis
        
        // Draw Titles
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Simple Multiplication Primitive Operations vs Digits (n)", width / 2 - 300, padding / 2);
        
        // Draw X-axis label
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Number of Digits (n)", width / 2 - 80, height - padding / 3);
        
        // Draw Y-axis label (vertical text)
        g2.translate(padding / 3, height / 2 + 100);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Primitive Operations Count", 0, 0);
        g2.rotate(Math.PI / 2);
        g2.translate(-padding / 3, -(height / 2 + 100));

        // Draw Y-axis markers (Scale)
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        int numYMarkers = 10;
        for (int i = 0; i <= numYMarkers; i++) {
            int yPos = height - padding - (i * (height - 2 * padding) / numYMarkers);
            long yVal = minY + (i * (maxY - minY) / numYMarkers);
            g2.drawLine(padding - 5, yPos, padding, yPos);
            String label = String.format("%,d", yVal);
            g2.drawString(label, padding - 10 - g2.getFontMetrics().stringWidth(label), yPos + 5);
        }

        // Draw X-axis markers (Scale)
        int numXMarkers = 10;
        for (int i = 0; i <= numXMarkers; i++) {
            int xPos = padding + (i * (width - 2 * padding) / numXMarkers);
            long xVal = minX + (i * (maxX - minX) / numXMarkers);
            g2.drawLine(xPos, height - padding, xPos, height - padding + 5);
            g2.drawString(String.valueOf(xVal), xPos - 15, height - padding + 20);
        }

        // Draw data points and connecting line
        g2.setColor(new Color(41, 128, 185)); // Nice blue color
        g2.setStroke(new BasicStroke(3));
        
        for (int i = 0; i < xData.size() - 1; i++) {
            int x1 = padding + (int) ((xData.get(i) - minX) * (width - 2 * padding) / (maxX - minX));
            int y1 = height - padding - (int) ((yData.get(i) - minY) * (height - 2 * padding) / (maxY - minY));
            
            int x2 = padding + (int) ((xData.get(i+1) - minX) * (width - 2 * padding) / (maxX - minX));
            int y2 = height - padding - (int) ((yData.get(i+1) - minY) * (height - 2 * padding) / (maxY - minY));
            
            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x1 - 4, y1 - 4, 8, 8); // Data point circle
        }
        
        // Draw the very last point
        int lastX = padding + (int) ((xData.get(xData.size()-1) - minX) * (width - 2 * padding) / (maxX - minX));
        int lastY = height - padding - (int) ((yData.get(yData.size()-1) - minY) * (height - 2 * padding) / (maxY - minY));
        g2.fillOval(lastX - 4, lastY - 4, 8, 8);

        g2.dispose();
        
        // Save the image
        try {
            File outputFile = new File("SimpleMultiplicationGraph.png");
            ImageIO.write(img, "png", outputFile);
            System.out.println("Successfully generated: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
