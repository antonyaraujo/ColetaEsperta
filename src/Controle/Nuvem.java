/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author antony
 */
public class Nuvem {
    ArrayList lixeiras;
    
    
    public static void main(String[] args) {
    try {
      // Instancia o ServerSocket ouvindo a porta 12345
      ServerSocket servidor = new ServerSocket(12345);
      System.out.println("Servidor ouvindo a porta 12345");
      while(true) {
        // o método accept() bloqueia a execução até que
        // o servidor receba um pedido de conexão
        Socket cliente = servidor.accept();        
        System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort());                
        
        // Recebimento de dados da lixeira
        //Scanner entrada = new Scanner(cliente.getInputStream());
        InputStream input = cliente.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                                    
        OutputStream output = cliente.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
                
        String[] lixeira = reader.readLine().split(";");        
        System.out.println("Lixeira " + lixeira[0] + lixeira[1]);
        System.out.println("latitude: " + lixeira[0]);
        System.out.println("longitude: " + lixeira[1]);
        System.out.println("capacidade: " + lixeira[2]);
        System.out.print("status: ");
        if(lixeira[3] == "true") 
            System.out.println("bloqueada");
        else 
            System.out.println("desbloqueada");
             
        writer.println("true");        
        sleep(1000);
        lixeira = reader.readLine().split(";");
        System.out.println("Lixeira " + lixeira[0] + lixeira[1]);
        System.out.println("latitude: " + lixeira[0]);
        System.out.println("longitude: " + lixeira[1]);
        System.out.println("capacidade: " + lixeira[2]);
        System.out.print("status: ");
        if(lixeira[3] == "true") 
            System.out.println("bloqueada");
        else 
            System.out.println("desbloqueada");
        
        /*while (entrada.hasNextLine()) {
             String texto = entrada.nextLine();
             String[] lixeira = texto.split(";");
             System.out.println("Lixeira " + lixeira[0] + lixeira[1]);
             System.out.println("latitude: " + lixeira[0]);
             System.out.println("longitude: " + lixeira[1]);
             System.out.println("capacidade: " + lixeira[2]);
             System.out.print("status: ");
             if(lixeira[3] == "true") 
                 System.out.print("desbloqueada");
             else 
                 System.out.print("bloqueada");               
        }       
        
        ObjectOutputStream bloqueio = new ObjectOutputStream(cliente.getOutputStream());        
        bloqueio.flush();
        bloqueio.writeObject("true".getBytes());        
        bloqueio.close();
        
        entrada.close();*/
        cliente.close();
      }
    }
    catch(Exception e) {
       System.out.println("Erro: " + e.getMessage());
    }
    finally {
    }
  }
}
