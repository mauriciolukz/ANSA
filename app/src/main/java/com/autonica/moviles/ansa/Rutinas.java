package com.autonica.moviles.ansa;

public class Rutinas {

    public String ITEMNMBRFormat(String Codigo){
        String codigoFormato = "";
        switch (Codigo.length()) {
            case 10:
                codigoFormato = Codigo.substring(0, 5) + "-" + Codigo.substring(5, 10);
                break;
            case 12:
                codigoFormato = Codigo.substring(0, 5) + "-" + Codigo.substring(5, 10) + "-" + Codigo.substring(10, 12);
                break;
            case 13:
                if (Codigo.substring(10 ,12).toString().equals("  ")){ // 4853080293  W
                    codigoFormato = Codigo.substring(0, 5) + "-" + Codigo.substring(5, 10);
                }else{ //4853080293B1W
                    codigoFormato = Codigo.substring(0, 5) + "-" + Codigo.substring(5, 10) + "-" + Codigo.substring(10, 12);
                }
                break;
            case 15:
                if (Codigo.substring(12 ,15).toString().equals("H000")){
                    codigoFormato = Codigo;
                }else{
                    codigoFormato = Codigo; //REVISAR
                }
                break;
            case 16:
                if (Codigo.substring(12, 16).equals("0000")){  //33810-98J00-0000
                    codigoFormato = Codigo.substring(0, 15);
                }else if(Codigo.substring(5, 6).equals("-") && Codigo.substring(11, 12).equals("-")){  //33810-98J00-0ED0
                    codigoFormato = Codigo.substring(0, 15);
                }else{
                    codigoFormato = Codigo;
                }
                break;
            default:
                codigoFormato = Codigo;
        }
        return  codigoFormato.toString();
    }
}
