/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Math.round;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author antony
 */
public class Administrador {    
    
    public Administrador(){        
    }
    
    /**
     * Metodo que retorna a lista de lixeiras armazenadas no banco de dados 
     * recuperando-as atraves de de uma conexao via rede
     * @return lixeiras - Array que possui todas as lixeiras do banco de dados
     */
    public static JSONArray getLixeiras(){
        try(Socket nuvem = new Socket("localhost", 40000)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "LISTAR_LIXEIRAS");                       
                       
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
     * Metodo que retorna as informacoes de um caminhao
     * recuperando-o do banco de dados atraves de de uma conexao via rede
     * @return lixeiras - Array que possui todas as lixeiras do banco de dados
     */
    public static JSONObject buscarCaminhao(int codigo){
        try(Socket nuvem = new Socket("localhost", 40000)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "BUSCAR");                       
            json.put("codigo", codigo);
                       
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();            
            JSONObject requisicao = new JSONObject(dados);            
            //JSONArray lixeiras = requisicao.getJSONArray("Lixeira");            
                                    
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();            
            return requisicao;
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        }        
        return null;
    }
    
    /**
     * Retorna a capacidade da lixeira em porcentagem
     * @return 
     */
    public static double getCapacidade(double atual, double maxima){        
        double porcentagem = atual/maxima;                
        return round(porcentagem*100);
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
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "ALTERAR_ESTADO");
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
    
    public void alterarOrdem(int codigoCaminhao, int codigoLixeira, int ordem) throws IOException, ClassNotFoundException{
    String mensagem_confirmacao = "";
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "ALTERAR_ORDEM");
            json.put("caminhao", codigoCaminhao);
            json.put("lixeira", codigoLixeira);
            json.put("ordem", ordem);
            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);            
                                                                       
            if(requisicao.getBoolean("resposta")){
                
            }
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close(); 
                        
        }        
    }
    
    public static JSONArray listarLixeirasCaminhao(int codigo) throws IOException, ClassNotFoundException{
        String mensagem_confirmacao = "";
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "LISTAR_LIXEIRAS");
            json.put("codigo", codigo);            
            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);            
                                                                                   
            JSONArray lixeiras = requisicao.getJSONArray("lixeiras");
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close(); 
            return lixeiras;
        }
    }
    
    public static void alterarOrdem(JSONArray ordem, int codigoCaminhao) throws IOException, ClassNotFoundException{
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "ALTERAR_ORDEM");
            json.put("ordem", ordem);            
            json.put("codigo", codigoCaminhao);            
            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                                    
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            //JSONObject requisicao = new JSONObject(dados);            
                                                                                   
            //JSONArray lixeiras = requisicao.getJSONArray("lixeiras");
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();             
        }
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("   ____      _      _        _____                     _        \n" +
        "  / ___|___ | | ___| |_ __ _| ____|___ _ __   ___ _ __| |_ __ _ \n" +
        " | |   / _ \\| |/ _ \\ __/ _` |  _| / __| '_ \\ / _ \\ '__| __/ _` |\n" +
        " | |__| (_) | |  __/ || (_| | |___\\__ \\ |_) |  __/ |  | || (_| |\n" +
        "  \\____\\___/|_|\\___|\\__\\__,_|_____|___/ .__/ \\___|_|   \\__\\__,_|\n" +
        "                                      |_|                       ");
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
                case 4:
                    System.out.println("Informe o código do caminhão");
                    int codigoCaminhao = leitor.nextInt();
                    JSONObject caminhao = buscarCaminhao(codigoCaminhao);
                    System.out.println(caminhao.toString());
                    System.out.println("Caminhão: "
                            + "\nCódigo: " + caminhao.getInt("codigo")
                            + "\nCapacidade: " + getCapacidade(caminhao.getDouble("capacidadeAtual"), caminhao.getDouble("capacidadeMaxima")) +"%"
                            + "\nLista de Lixeiras:");                    
                    if(caminhao.has("lixeiras")){
                        JSONArray lixeirasCaminhao = caminhao.getJSONArray("lixeiras");                    
                        for(int i = 0; i < lixeirasCaminhao.length(); i++){
                            int atual = (Integer) lixeirasCaminhao.get(i);
                            System.out.println(i+1 + ". Lixeira código: " + atual);
                        }
                    } else{
                        System.out.println("O caminhão não possui lixeiras adicionadas");
                    }
                    break;
                case 5:
                    lixeiras = getLixeiras();
                    System.out.println("Informe o código do caminhão");
                    codigoCaminhao = leitor.nextInt();
                    JSONArray lixeirasCaminhao = listarLixeirasCaminhao(codigoCaminhao);
                    JSONArray lixeirasOrdenadas = new JSONArray();
                    List<Object> codigos = new ArrayList();
                    for(int i = 0; i < lixeiras.length(); i++){
                        JSONObject atualLixeira =  (JSONObject) lixeiras.get(i);
                        for(int j = 0; j < lixeirasCaminhao.length(); j++){
                         int atualLixeiraCaminhao = (Integer) lixeirasCaminhao.get(j);
                         if(atualLixeira.getInt("codigo") == atualLixeiraCaminhao){
                             // Determina a ordem das lixeiras uma por uma                             
                             boolean repetir = false;
                             do{                                
                             System.out.println("==== Lixeira ====");
                             System.out.println("Código: " + atualLixeira.getInt("codigo"));
                             double capacidade = getCapacidade(atualLixeira.getDouble("capacidadeAtual"), atualLixeira.getDouble("capacidadeMaxima"));
                             System.out.println("Capacidade: " + capacidade + "%");
                             System.out.println("Qual a ordem da lixeira?");                                                                                                                    
                             int ordem = leitor.nextInt();                                
                                if(codigos.contains(ordem)){
                                    repetir = true;
                                    System.out.println("Essa ordem já foi selecionada, tente novamente");
                                } else {
                                    codigos.add(ordem);
                                    lixeirasOrdenadas.put(ordem, atualLixeira.getInt("codigo"));
                                }
                             }while(repetir);
                             break;
                         }
                        }                        
                    }                    
                    lixeirasOrdenadas.remove(0);
                    System.out.println(lixeirasOrdenadas.toString());
                    alterarOrdem(lixeirasOrdenadas, codigoCaminhao);
                    break;
            }
        }while(opcao != 0);
    }
}
