/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.Observable;

/**
 *
 * @author antony
 */
public class Lixeira extends Observable implements Runnable{    
    private int codigo;
    private int latitude;
    private int longitude;
    private double capacidadeAtual;
    private double capacidadeMaxima;
    private boolean bloqueada;
    private String host;
    private int porta;
    private boolean modo_recepcao; 
    Socket cliente;
    
    public Lixeira(int codigo, int latitude, int longitude, double capacidadeAtual, double capacidadeMaxima) throws IOException {
        this.codigo = codigo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidadeAtual = capacidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
        this.bloqueada = false;
        this.host = "localhost";
        this.porta = 12345;
        this.modo_recepcao = false;        
        this.cliente = new Socket(this.host,this.porta);
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getCapacidadeAtual() {
        return capacidadeAtual;
    }

    public void setCapacidadeAtual(double capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual;
        this.modo_recepcao = false;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
        modo_recepcao = false;
        setChanged();
        notifyObservers();
    }
         
    /**
     * Retorna a capacidade da lixeira em porcentagem
     * @return 
     */
    public int getCapacidade(){        
        double porcentagem = getCapacidadeAtual()/getCapacidadeMaxima();        
        int valor = (int) (porcentagem*100);
        return valor;
    }
    
    public String getDados(){                
        return String.valueOf(latitude) + ";" + String.valueOf(longitude) + ";" 
                + String.valueOf(getCapacidade()) + ";" + String.valueOf(isBloqueada());
    }
    
    public void adicionarLixo(int quantidade){
        setCapacidadeAtual(capacidadeAtual + quantidade);       
        try{                        
            OutputStream encapsulamento = cliente.getOutputStream();
            //encapsulamento.write(getDados().getBytes());
            PrintWriter writer = new PrintWriter(encapsulamento, true);
            //encapsulamento.flush();            
            boolean valor = false;
            
            do{                 
            writer.println(getDados())                 ;
            //ObjectInputStream comandoRecebido = new ObjectInputStream(cliente.getInputStream());
            InputStream input = cliente.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             if(reader.readLine().equals("true"))
                 valor = true;             
              System.out.println("valor: " + valor);
              this.setBloqueada(valor);
            } while(!valor);
            
            writer.println(getDados())                 ;
            cliente.close();
        }
        catch(Exception e){
        }
    }        

    @Override
    public void run() {                     
        try { 
                        
            System.out.println("Lixeira " + this.getLongitude() + this.getLatitude() + " em execução");
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
            boolean unblock = (boolean)entrada.readObject();
            if(unblock){
                  System.out.println("Lixeira desbloqueada");
                  setBloqueada(unblock);    
                  
             }                       
            /*if(entrada.readBoolean())
                System.out.println("exibiu o boolean: " + entrada.readBoolean());
            setBloqueada(entrada.readBoolean());*/
        }
            catch(Exception e) {
              System.out.println("Erro: " + e.getMessage());
            }   
    }       
       
}
