/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sorting;

import java.util.ArrayList;
import java.util.List;
import model.Task;

/**
 *
 * @author USER
 */

public class SortingAlgorithm {

    public static List<Task> sortByMotorBrand(List<Task> tasks) {

        List<Task> sorted = new ArrayList<>(tasks);

        int n = sorted.size();

        // Bubble Sort by Motor Brand (A-Z)
        for (int i = 0; i < n - 1; i++) {

            for (int j = 0; j < n - i - 1; j++) {

                String brand1 = sorted.get(j).getBrand();
                String brand2 = sorted.get(j + 1).getBrand();

                if (brand1.compareToIgnoreCase(brand2) > 0) {

                    // Swap records
                    Task temp = sorted.get(j);

                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }

        return sorted;
    }
}
