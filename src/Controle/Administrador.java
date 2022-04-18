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
 * @author Antony Araujo
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
            /** Procedimento de envio da solicitacao */
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "LISTAR_LIXEIRAS");                       
                       
            String dados = json.toString();
            nuvem_enviar.writeObject(dados); // Envio dos dados encapsulados
            nuvem_enviar.flush();  // Atualiza o envio/recebimento da conexao stream
            
            /** Procedimento de recebimento da resposta a solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();            
            JSONObject requisicao = new JSONObject(dados);            
            JSONArray lixeiras = requisicao.getJSONArray("Lixeira");            
                                    
            nuvem_receber.close(); // fecha o stream de recebimento de dados
            nuvem_enviar.close(); // fecha o stream de envio de dados
            nuvem.close(); // fecha a comunicacao via socket
            return lixeiras;
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        }        
        return null;
    }
    
    /**
     * Metodo que retorna as informacoes de um caminhao
     * recuperando-o do banco de dados atraves de de uma conexao via rede
     * @param codigo - inteiro que representa o codigo do caminhao a ser buscado no BD
     * @return caminhao - retorna um caminhao se o o codigo tiver sido localizado no BD
     * @return null - caso nenhum caminhao tenha sido encontrado no BD
     */
    public static JSONObject buscarCaminhao(int codigo){
        try(Socket nuvem = new Socket("localhost", 40000)){
            /** Procedimento de envio da solicitacao */
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "BUSCAR");                       
            json.put("codigo", codigo);                       
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();              
            
            /** Procedimento de recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();            
            JSONObject requisicao = new JSONObject(dados);                        
                                    
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
     * @return valor em % da ocupacao da lixeira aproximado em duas casas decimais
     */
    public static double getCapacidade(double atual, double maxima){        
        double porcentagem = (atual/maxima)*100;
        return round(porcentagem*100)/100.0;
    }
    
    /**
     * Exibe o codigo, estado e capacidade (%) das lixeiras existentes em um JSONArray de lixeiras
     * @param lixeiras - Array de lixeiras a serem exibidas
     */
    public static void exibirLixeiras(JSONArray lixeiras){
        System.out.println("====================\n"
                         + "  Lista de Lixeiras"
                + "       \n====================");
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
    
    /**
     * Realiza uma solicitacao ao servidor para alterar o estado de uma dada lixeira
     * @param codigo - codigo da lixeira a ter o seu estado alterado
     * @param estado - novo estado (true/false) da lixeira informada
     * @return mensagem com confirmacao ou nao da operacao
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static String alterarEstadoLixeira(int codigo, boolean estado) throws IOException, ClassNotFoundException{
        String mensagem_confirmacao = "";
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            /** Procedimento de envio da solicitacao */
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "ALTERAR_ESTADO");
            json.put("bloqueada", estado);
            json.put("codigo", codigo);            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();      
            
            /** Procedimento recebimento da resposta da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            JSONObject requisicao = new JSONObject(dados);            
                                                                       
            if(estado){ // Se a solicitacao for para bloquear a lixeira 
                if(requisicao.getBoolean("resposta")){  // e a lixeira tiver sido bloqueada via servidor
                    mensagem_confirmacao = "Lixeira bloqueada!";
                } else{ // Caso a lixeira ja esteja bloqueada
                    mensagem_confirmacao = "A lixeira já está bloqueada ou ocorreu algum problema na sua solicitação!";
                }
            } else{ // Se a solicitacao for para desbloquear a lixeira
                if(requisicao.getBoolean("resposta")){ // e a lixeira tiver sido bloqueada via servidor
                    mensagem_confirmacao = "Lixeira desbloqueada";
                } else{ // Caso a lixeira ja esteja desbloqueada
                    mensagem_confirmacao = "A lixeira já está desbloqueada ou ocorreu algum problema na sua solicitação!";
                }
            }
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();                         
        }
        return mensagem_confirmacao;
    }
           
    /**
     * Metodo responsavel por retornar a lista de lixeiras de um dado caminhao
     * @param codigo - inteiro que representa o codigo do caminhao a ser buscado
     * @return lixeiras - JSONArray que possui as lixeiras a serem recolhidas por um dado caminhao
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static JSONArray listarLixeirasCaminhao(int codigo) throws IOException, ClassNotFoundException{
        String mensagem_confirmacao = "";
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            /** Procedimento de envio da solicitacao */
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "LISTAR_LIXEIRAS");
            json.put("codigo", codigo);                        
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                  
            
            /** Procedimento de recebimento da resposta a solicitação */
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
    
    /**
     * Metodo responsavel por alterar a ordem de todas as lixeiras de um dado
     * caminhao de forma arbitraria conforme definido pelo administrador
     * @param ordem - Array que contem as lixeiras organizadas na ordem 
     * definidas pelo administrador e a ser alterada no Banco de Dados
     * @param codigoCaminhao - Codigo do caminhao que tera a lista de lixeiras
     * alterado
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static boolean alterarOrdem(JSONArray ordem, int codigoCaminhao, String forma) throws IOException, ClassNotFoundException{
        try(Socket nuvem = new Socket("localhost", 40000)){                                                
            /** Procedimento de envio da solicitacao ao servidor*/
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "ALTERAR_ORDEM");
            json.put("ordem", ordem);            
            json.put("codigo", codigoCaminhao);   
            json.put("forma", forma);
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                          
            
            /** Procedimento de recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();
            boolean resposta = false;
            if(dados.equals("true"))
                resposta = true;
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();  
            return resposta;
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
            System.out.println("\nEscolha uma operação: "
                    + "\n1. [Lixeira] Listar todas"                    
                    + "\n2. [Lixeira] Bloquear"
                    + "\n3. [Lixeira] Desbloquear"                    
                    + "\n4. [Caminhão] Ver informacoes"
                    + "\n5. [Caminhao] Alterar Ordem Coleta"                    
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
                    System.out.println("=====================\n"
                          + "       Caminhão"
                         + "\n====================="
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
                    System.out.println("Ordenação: \n1. Capacidade \n2. Arbitrariamente");
                    int ordenacaoOpcao = leitor.nextInt();
                    boolean resposta = false;
                    // Realiza a alteracao com base na capacidade
                    if(ordenacaoOpcao == 1){
                        resposta = alterarOrdem(null, codigoCaminhao, "capacidade");
                    }
                    // Realiza a alteracao da ordem de forma arbitraria
                    if(ordenacaoOpcao == 2){
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
                        resposta = alterarOrdem(lixeirasOrdenadas, codigoCaminhao, "arbitraria");                        
                    }
                    
                    if(ordenacaoOpcao > 2 || ordenacaoOpcao < 1)
                        System.out.println("Opção inválida, tente novamente!");                    
                    else{
                        if(resposta)
                            System.out.println("Ordem alterada com sucesso!");
                        else
                            System.out.println("A ordem não foi alterada, tente novamente!");
                    }
                    break;
            }
        }while(opcao != 0);
    }
}
