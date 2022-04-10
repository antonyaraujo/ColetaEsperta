/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author antony
 */
public class Administrador {
    private static String host;
    private static int porta;
    
    public Administrador(){
        this.host = "localhost";
        this.porta = 12345;
    }
    
    
    public static JSONArray getLixeiras(){
        try(Socket nuvem = new Socket("localhost", 12345)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "GET");                       
                       
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();            
            JSONObject requisicao = new JSONObject(dados);            
            JSONArray lixeiras = requisicao.getJSONArray("Lixeira");            
                                    
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();            
            return lixeiras;
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        }        
        return null;
    }
    
    /**
     * Retorna a capacidade da lixeira em porcentagem
     * @return 
     */
    public static int getCapacidade(double atual, double maxima){        
        double porcentagem = atual/maxima;        
        int valor = (int) (porcentagem*100);
        return valor;
    }
    
    public static void exibirLixeiras(JSONArray lixeiras){
        System.out.println("Lista de Lixeiras");
        for(int i = 0; i < lixeiras.length(); i++){
            JSONObject atual = lixeiras.getJSONObject(i);
            System.out.println("\nCódigo: " + atual.getInt("codigo"));
            if(atual.getBoolean("bloqueada"))
                System.out.println("Estado: bloqueada");
            else
                System.out.println("Estado: desbloqueada");
            System.out.println("Capacidade: " + getCapacidade(atual.getDouble("capacidadeAtual"), atual.getDouble("capacidadeMaxima")) + "%");            
            }
    }
    
    public static String alterarEstadoLixeira(int codigo, boolean estado) throws IOException, ClassNotFoundException{
        String mensagem_confirmacao = "";
        try(Socket nuvem = new Socket("localhost", 12345)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "POST");
            json.put("bloqueada", estado);
            json.put("codigo", codigo);
            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);            
                                                                       
            if(estado){
                if(requisicao.getBoolean("resposta")){
                    mensagem_confirmacao = "Lixeira bloqueada!";
                } else{
                    mensagem_confirmacao = "A lixeira já está bloqueada ou ocorreu algum problema na sua solicitação!";
                }
            } else{
                if(requisicao.getBoolean("resposta")){
                    mensagem_confirmacao = "Lixeira desbloqueada";
                } else{
                    mensagem_confirmacao = "A lixeira já está desbloqueada ou ocorreu algum problema na sua solicitação!";
                }
            }
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close(); 
                        
        }
        return mensagem_confirmacao;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int opcao = 1;
        Scanner leitor = new Scanner(System.in);
        do{
            System.out.println("Escolha uma operação: "
                    + "\n1. [Lixeira] Listar todas"                    
                    + "\n2. [Lixeira] Bloquear"
                    + "\n3. [Lixeira] Desbloquear"                    
                    + "\n4. [Caminhão] Ver informacoes"
                    + "\n5. [Caminhao] Alterar Ordem Coleta"
                    + "\n6. [Caminhao] Acompanhar em tempo real"
                    + "\n0. Sair");            
            opcao = leitor.nextInt();
            switch(opcao){
                case 1:
                    JSONArray lixeiras = getLixeiras();
                    exibirLixeiras(lixeiras);
                    break;
                case 2:
                    System.out.println("Informe o código da Lixeira a ser bloqueada: ");
                    int codigoLixeira = leitor.nextInt();
                    String confirmacao = alterarEstadoLixeira(codigoLixeira, true);
                    System.out.println(confirmacao);
                    break;
                case 3:
                    System.out.println("Informe o código da Lixeira a ser desbloqueada");
                    codigoLixeira = leitor.nextInt();
                    confirmacao = alterarEstadoLixeira(codigoLixeira, false);
                    System.out.println(confirmacao);
                    break;
            }
        }while(opcao != 0);
    }
}
