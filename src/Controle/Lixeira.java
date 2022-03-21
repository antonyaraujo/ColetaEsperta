/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.Date;

/**
 *
 * @author antony
 */
public class Lixeira {    
    private int latitude;
    private int longitude;
    private double capacidadeAtual;
    private double capacidadeMaxima;
    private boolean bloqueada;
    private String host;
    private int porta;

    public Lixeira(int latitude, int longitude, double capacidadeAtual, double capacidadeMaxima) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidadeAtual = capacidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
        this.bloqueada = false;
        this.host = "localhost";
        this.porta = 12345;
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
            Socket cliente = new Socket(host,porta);
            OutputStream encapsulamento = cliente.getOutputStream();
            encapsulamento.write(getDados().getBytes());
            encapsulamento.flush();
            encapsulamento.close();
            cliente.close();
        }
        catch(Exception e){
        }
    }
    
    public static void main(String[] args) {
    try {
      Socket cliente = new Socket("localhost",12345);
      ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
      Date data_atual = (Date)entrada.readObject();
      
      // Enviando dados para a nuvem      
      Lixeira l = new Lixeira(2, 2, 10.0, 20.0);            
      OutputStream encapsulamento = cliente.getOutputStream();
      encapsulamento.write(l.getDados().getBytes());
      encapsulamento.flush();
            
      entrada.close();
      System.out.println("Conexão encerrada");
    }
    catch(Exception e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }
}
