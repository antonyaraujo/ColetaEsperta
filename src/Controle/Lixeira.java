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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author antony
 */
public class Lixeira extends Thread{    
    private int codigo;
    private double latitude;
    private double longitude;
    private double capacidadeAtual;
    private double capacidadeMaxima;
    private boolean bloqueada;
    private String host;
    private int porta;        
    
    public Lixeira(int codigo, double latitude, double longitude, double capacidadeMaxima) throws IOException {
        this.codigo = codigo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidadeAtual = 0;
        this.capacidadeMaxima = capacidadeMaxima;
        this.bloqueada = false;
        this.host = "localhost";
        this.porta = 12345;                
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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
    
    public static Lixeira criarLixeira(double capacidadeMaxima, double latitude, double longitude){
        try(Socket nuvem = new Socket("localhost", 12345)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                        
            //ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            JSONObject json = new JSONObject();
            json.put("cliente", "Lixeira");
            json.put("operacao", "CRIAR");                       
            json.put("capacidadeMaxima", capacidadeMaxima);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);                        
            
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();
            
            return new Lixeira(requisicao.getInt("codigo"), latitude, longitude, capacidadeMaxima);
        }catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }
    
    public static Lixeira buscarLixeira(int codigo){
         try(Socket nuvem = new Socket("localhost", 12345)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Lixeira");
            json.put("operacao", "GET");           
            json.put("codigo", codigo);           
                       
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();
            System.out.println(requisicao.toString());
            Lixeira l =  new Lixeira(requisicao.getInt("codigo"), requisicao.getDouble("latitude"), requisicao.getDouble("longitude"), requisicao.getDouble("capacidadeMaxima"));
            l.setBloqueada(requisicao.getBoolean("bloqueada"));
            l.setCapacidadeAtual(requisicao.getDouble("capacidadeAtual"));
            return l;
        }catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }
    
    public boolean alterarCapacidade(double quantidade, String alteracao){                
        try(Socket nuvem = new Socket(this.host, this.porta)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                        
            //ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            JSONObject json = new JSONObject();
            json.put("cliente", "Lixeira");
            json.put("operacao", "ALTERAR_CAPACIDADE");           
            json.put("codigo", this.getCodigo());           
            json.put("quantidade", quantidade);
            json.put("alteracao", alteracao);
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);                        
            nuvem_enviar.close();            
            
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);
            return requisicao.getBoolean("confirmar_operacao");
            
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        } 
        return false;
    }        

    public void sincronizar() throws IOException, ClassNotFoundException{
        try(Socket nuvem = new Socket(this.host, this.porta)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Lixeira");
            json.put("operacao", "GET");           
            json.put("codigo", this.getCodigo());           
           
            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);
            
            this.setLatitude(requisicao.getDouble("latitude"));
            this.setLongitude(requisicao.getDouble("longitude"));
            this.setBloqueada(requisicao.getBoolean("bloqueada"));
            this.setCapacidadeMaxima(requisicao.getDouble("capacidadeMaxima"));
            this.setCapacidadeAtual(requisicao.getDouble("capacidadeAtual"));
                                    
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();
            System.out.println("Sincronizado!");
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        } 
    }
                   
    public static void main(String[] args) throws IOException, InterruptedException {        
        Lixeira lixo;
        System.out.println("#### INICIALIZAÇÃO DA LIXEIRA ####"
                + "\n1. Criar nova lixeira;"
                + "\n2. Utilizar Lixeira existente");        
        Scanner leitor = new Scanner(System.in);
        int opcao = leitor.nextInt();
        if(opcao == 1){
            System.out.println("Informe a capacidade total da Lixeira (m³): ");
            double capacidade = leitor.nextDouble();
            System.out.println("Informe a latitude: ");
            double lat = leitor.nextDouble();
            System.out.println("Informe a longitude: ");
            double lon = leitor.nextDouble();
            lixo = criarLixeira(capacidade, lat, lon);
        } else {
            System.out.println("Informe o código da Lixeira: ");
            int codigo = leitor.nextInt();
            lixo = buscarLixeira(codigo);
        }
                        
        Runnable r = new Runnable() {
        public void run() {
                try {
                    while(true){
                        if(lixo != null)
                            lixo.sincronizar();                         
                        sleep(10000);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
                    }        
        }};
        
       Thread t = new Thread(r);
       t.start();
        
        try {
            opcao = 1;
            do{                                            
                System.out.println("Selecione a opção que deseja realizar \n1. Ver Info \n2. Adicionar Lixo;"
                        + "\n3. Retirar Lixo; \n0. Sair;");
                opcao = leitor.nextInt();
                switch(opcao){
                    case 1:
                        System.out.println("CÓDIGO: " + lixo.getCodigo());
                        if(lixo.isBloqueada())
                            System.out.println("STATUS: Bloqueada");
                        else
                            System.out.println("STATUS: Desbloqueada");
                        System.out.println("CAPACIDADE ATUAL: " + lixo.getCapacidade() + "%");
                        break;
                    case 2:
                        if(lixo.isBloqueada() == false){
                            System.out.println("Capacidade Atual: " + lixo.getCapacidade());
                            System.out.println("Informe a quantidade de lixo (m³): ");
                            double quantidade = leitor.nextDouble();
                            boolean resposta = lixo.alterarCapacidade(quantidade, "adicionar");
                            if(resposta)
                                System.out.println("Lixo adicionado!");
                            else
                                System.out.println("Quantidade ultrapassa a capacidade maxima da lixeira!");
                        } else {
                            System.out.println("A lixeira está bloqueada!");
                        }
                    break;
                    case 3:
                        if(lixo.isBloqueada() == false){
                            System.out.println("Capacidade Atual: " + lixo.getCapacidade());
                            System.out.println("Informe a quantidade de lixo (m³): ");
                            double quantidade_remover = leitor.nextDouble();
                            boolean resposta = lixo.alterarCapacidade(quantidade_remover, "remover");
                            if(resposta)
                                System.out.println("Lixo removido!");
                            else
                                System.out.println("Quantidade ultrapassa a capacidade atual da lixeira!");
                        } else {
                            System.out.println("A lixeira está bloqueada!");
                        }
                    break;
                }
                                                                               
            } while(opcao != 0);
            
            t.stop();
        }catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
    }
        finally {
        }
    }       
       
}
