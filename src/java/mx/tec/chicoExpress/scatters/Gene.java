/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tec.chicoExpress.scatters;

/**
 *
 * @author mike-
 */
public class Gene {
    
    private double x;
    private double y;
    private double logrankx;
    private double logranky;
    private double rankx;
    private double ranky;
    private int idx;
    private String ensembl;
    private String symbol;
    
    public Gene() {
        
        
        
    }
    
    public void setX(double x) {
        
        this.x = x;
        
    }
    
    public void setY(double y) {
        
        this.y = y;
        
    }
    
    public void setIdx(int idx) {
        
        this.idx = idx;
        
    }
    
    public void setMetadata(String meta, String what) {
        
        switch (what) {
            
            case "ensembl":
                this.ensembl = meta;
                break;
            
            case "symbol":
                this.symbol = meta;
                break;
            
        }
        
    }
    
    public void setRank(double rank, String what) {
        
        if (what.equals("x")) {
            
            this.rankx = rank;
        
        } else {
            
            this.ranky = rank;
            
        }
        
    }
    
    public void setLogrank(double logrank, String what) {
        
        if (what.equals("x")) {
            
            this.logrankx = logrank;
        
        } else {
            
            this.logranky = logrank;
            
        }
        
    }
    
    public double getRaw(String what) {
        
        if (what.equals("x")) return Math.abs(this.x);
        return Math.abs(this.y);
        
    }
    
    public double getRawSigned(String what) {
        
        if (what.equals("x")) return this.x;
        return this.y;
        
    }
    
    public double getRank(String what) {
        
        if (what.equals("x")) return this.rankx;
        return this.ranky;
        
    }
    
    public double getLogrank(String what) {
        
        if (what.equals("x")) return this.logrankx;
        return this.logranky;
        
    }
    
    public String getMetadata(String what) {
        
        String meta = "";
        switch (what) {
            
            case "ensembl":
                meta = this.ensembl;
                break;
            
            case "symbol":
                meta = this.symbol;
                break;
            
        }
        
        return meta;
        
    }
    
}
