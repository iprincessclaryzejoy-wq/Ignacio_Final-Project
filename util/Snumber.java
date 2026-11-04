/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.Random;

/**
 *
 * @author USER
 */
public class Snumber {
    public static String SnumberId(int year){
        Random random = new Random();
        int r = random.nextInt(1000000); //random number from 0 to 999999
        //convert the random number to a six digit string        
        return String.format("%d-%06d", year, r); //year=2024 r=231 -> 2024-000231
    }
}
