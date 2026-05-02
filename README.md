# CPT212 - Design and Analysis of Algorithms Assignment

This project implements and compares two fundamental multiplication algorithms: **Simple Multiplication** and the **Karatsuba Algorithm**. The goal is to analyze their performance characteristics and verify their theoretical time complexities through empirical data.

## Project Structure

- **`DatasetGenerator.java`**: Generates a `Dataset.csv` containing pairs of random numbers of lengths ranging from $n=1$ to $n=10,000$.
- **`SimpleMultiplication.java`**: Implements the digit-by-digit $O(n^2)$ multiplication algorithm.
- **`Karatsuba.java`**: Implements the divide-and-conquer Karatsuba algorithm with $O(n^{1.585})$ complexity.
- **`Dataset.csv`**: The central data file storing test cases, expected results, and the operation counts for both algorithms.

## Features

- **Accurate Operation Counting**: Both algorithms systematically track "primitive operations" (assignments, basic arithmetic, and logic) to provide a fair comparison of algorithmic work.
- **Step-by-Step Logging**: For small inputs ($n \le 10$), both algorithms print their internal steps (partial products, carries, and recursive splits) to the terminal for easy verification.
- **BigInteger Support**: Capable of handling massive numbers up to 10,000 digits.
- **Performance Analysis**: Includes data on the "Zigzag Phenomenon" caused by recursive splitting and identifies the crossover point where Karatsuba becomes more efficient than Simple Multiplication.

## How to Run

1.  **Compile all files**:
    ```bash
    javac *.java
    ```

2.  **Generate the dataset**:
    ```bash
    java DatasetGenerator
    ```

3.  **Run Simple Multiplication (Part 1)**:
    ```bash
    java SimpleMultiplication
    ```

4.  **Run Karatsuba Multiplication (Part 2)**:
    ```bash
    java Karatsuba
    ```

## Results
The performance results and operation counts are saved back into `Dataset.csv`, which can be used to generate graphs and perform further analysis in the assignment report.
