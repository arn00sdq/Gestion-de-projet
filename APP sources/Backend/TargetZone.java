/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backend;

import java.util.List;

/**
 *
 * @author MSI
 */
public class TargetZone {
    
    int upper_x;
    int upper_y;
    int lower_x;
    int lower_y;
    int w;
    int h;
    
    
    public int getUpper_x() {
        return upper_x;
    }

    public int getUpper_y() {
        return upper_y;
    }

    public int getLower_x() {
        return lower_x;
    }

    public int getLower_y() {
        return lower_y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
    
    public TargetZone(int upper_x,int upper_y,int lower_x,int lower_y,int w,int h){
        
        this.upper_x = upper_x;
        this.upper_y = upper_y;
        this.lower_x = lower_x;
        this.lower_y = lower_y;
        this.w = w;
        this.h = h;
   
    }
    
    public int getArea(){
        
        return this.w * this.h;
        
    }
    
    public boolean equals(TargetZone other){

        if(this.upper_x == other.upper_x){
            
            return true;
            
        }else{
        
            return false;
            
        }
    }
    
    public boolean existsInArray(List<TargetZone> array){
        
        boolean exists = false;

        for(int i = 0; i < array.size(); i++ ){
            
            if(this.equals(array.get(i))){
                
                exists = true;
            }
            
        }
        
        return exists;
        
    }

    @Override
    public String toString() {
        String res = new String();
        res += "ZoneCarotte \n\tp1 (" + upper_x + ", " + upper_y + ")";
        res += "\n\tp2 (" + lower_x + ", " + lower_y + ")";
        return res;
    }
    
    
}
