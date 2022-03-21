/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        //ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
        //saida.flush();
        //saida.writeObject(new Date());
        
        
        // Recebimento de dados da lixeira
        Scanner entrada = new Scanner(cliente.getInputStream());
        while (entrada.hasNextLine()) {
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
        System.out.println("Deseja desbloquear? [S/N]");
        Scanner block = new Scanner(System.in);
        String b = block.nextLine();
        if(b == "S"){
            
        }
        
        //saida.close();
        entrada.close();
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
