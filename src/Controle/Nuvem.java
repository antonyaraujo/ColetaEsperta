package Controle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author antony
 */
public class Nuvem {
    static File bd; // Arquivo que reresenta o banco de dados
    
    /**
     * Metodo responsavel por retornar todas as lixeiras armazenadas no arquivo banco de dados
     * @return lixeiras - Array com todos os objetos JSON que sao lixeiras
     * @throws IOException - erros de entrada/saida do sistema de arquivos e S.O.
     */
    public static JSONArray getLixeiras() throws IOException{
        bd = new File("./bancodedados.json");
        if(bd.exists() == false){            
            bd.createNewFile();
            FileWriter escritor = new FileWriter(bd);
            BufferedWriter bw = new BufferedWriter(escritor);
            bw.write("{'Lixeira':[],'Caminhao':[],'Estacao':[]}");
            bw.close();
            escritor.close();
        }
        FileReader leitor = new FileReader(bd);
        BufferedReader buffer = new BufferedReader(leitor);
        String arquivo = "";
        while(buffer.ready()){
            arquivo += buffer.readLine(); // Realiza a leitura do arquivo linha por linha
        }
        JSONObject json = new JSONObject(arquivo);                
        JSONArray lixeiras = json.getJSONArray("Lixeira");                            
        buffer.close();
        leitor.close();               
        return lixeiras;
    }
    
    /**
     * Metodo responsavel por retornar todos os caminhoes armazenados no arquivo banco de dados
     * @return caminhoes - Array com todos os objetos JSON que sao caminhoes
     * @throws IOException - erros de entrada/saida do sistema de arquivos e S.O.
     */
    public static JSONArray getCaminhoes() throws IOException{
        bd = new File("./bancodedados.json");
        if(bd.exists() == false){            
            bd.createNewFile();
            FileWriter escritor = new FileWriter(bd);
            BufferedWriter bw = new BufferedWriter(escritor);
            bw.write("{'Lixeira':[],'Caminhao':[],'Estacao':[]}");
            bw.close();
            escritor.close();            
        }
        FileReader leitor = new FileReader(bd);
        BufferedReader buffer = new BufferedReader(leitor);
        String arquivo = "";
        while(buffer.ready()){
            arquivo += buffer.readLine(); // Realiza a leitura do arquivo linha por linha
        }
        JSONObject json = new JSONObject(arquivo);                              
        JSONArray caminhoes = json.getJSONArray("Caminhao");                
        buffer.close();
        leitor.close();               
        return caminhoes;
    }
    
    /**
     * Metodo que escreve as alteracoes das lixeiras no arquivo de banco de dados
     * @param lista - novas lixeiras a serem inseridas na base de dados
     * @param lixeiras - lista de lixeiras recebida na leitura previa do arquivo
     * @throws IOException  - excecoes de entrada/saida do sistema de arquivos e SO
     */
    public static void escreverLixeiras(List lista, JSONArray lixeiras) throws IOException{
        JSONArray caminhoes = getCaminhoes();
        FileWriter escritor = new FileWriter(bd);
        BufferedWriter bw = new BufferedWriter(escritor);

        for(int i = 0; i < lista.size(); i++){
            lixeiras.put(lista.get(i));
        }
        JSONObject json = new JSONObject();
        json.put("Lixeira", lixeiras);
        json.put("Caminhao", caminhoes);
        bw.write(json.toString());
        bw.close(); 
        escritor.close();                    
    }
    
    /**
     * Metodo que escreve as alteracoes dos caminhoes no arquivo de banco de dados
     * @param lista - novos caminhoes a serem inseridos na base de dados
     * @param caminhoes - lista de caminhoes recebidos na leitura previa do arquivo
     * @throws IOException  - excecoes de entrada/saida do sistema de arquivos e SO
     */
    public static void escreverCaminhoes(List lista, JSONArray caminhoes) throws IOException{        
        JSONArray lixeiras = getLixeiras();        
        FileWriter escritor = new FileWriter(bd);
        BufferedWriter bw = new BufferedWriter(escritor);        
        for(int i = 0; i < lista.size(); i++){
            caminhoes.put(lista.get(i));            
        }
        JSONObject json = new JSONObject();
        json.put("Caminhao", caminhoes);        
        json.put("Lixeira", lixeiras);        
        bw.write(json.toString());
        bw.close(); 
        escritor.close();                    
    }
    
    public static void main(String[] args) throws IOException {
    try {
      // Instancia o ServerSocket ouvindo a porta 12345
      ServerSocket servidor = new ServerSocket(40000);
      System.out.println("Servidor ouvindo a porta 40000");
      while(true) {
        // o método accept() bloqueia a execução até que
        // o servidor receba um pedido de conexão
        Socket cliente = servidor.accept();        
        System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort());                
        
        // Recebe e processa requisicao de um cliente
        ObjectInputStream cliente_receber = new ObjectInputStream(cliente.getInputStream());        
        String dados = (String) cliente_receber.readObject();        
        JSONObject requisicao = new JSONObject(dados);
        
        // Inicializa objetos utilizados nas operacoes
        JSONArray lixeiras = getLixeiras();        
        List<Object> lista = new ArrayList();
        JSONObject resposta = new JSONObject();
        String dados_resposta = "";
        
        //Inicializa o Stream responsavel pela resposta ao cliente
        ObjectOutputStream cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());
        
        switch(requisicao.getString("cliente")){
            /** Operacao realizada por/com lixeiras*/
            case "Lixeira":                                
                /** Realiza a selecao das operacoes */
                switch(requisicao.getString("operacao")){
                    // Cria lixeiras no banco de dados
                    case "CRIAR":                        
                        resposta.put("codigo", lixeiras.length()+1);
                        resposta.put("latitude", requisicao.get("latitude"));
                        resposta.put("longitude", requisicao.get("longitude"));
                        resposta.put("capacidadeMaxima", requisicao.get("capacidadeMaxima"));
                        resposta.put("capacidadeAtual", 0);
                        resposta.put("bloqueada", false);
                        lista.add(resposta);
                        dados_resposta = resposta.toString();                        
                        break;
                    // Altera a capacidadeAtual de uma dada lixeira
                    case "ALTERAR_CAPACIDADE":
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                                if(requisicao.getString("alteracao").equals("adicionar")){
                                    double novaCapacidade = requisicao.getDouble("quantidade") + atual.getDouble("capacidadeAtual");
                                    if(novaCapacidade <= atual.getDouble("capacidadeMaxima")){
                                        atual.put("capacidadeAtual", novaCapacidade);
                                        resposta.put("confirmar_operacao", true);
                                    }else
                                        resposta.put("confirmar_operacao", false);
                                } else{
                                    double novaCapacidade = atual.getDouble("capacidadeAtual") - requisicao.getDouble("quantidade");
                                    if(novaCapacidade >= 0){
                                        atual.put("capacidadeAtual", novaCapacidade);
                                        resposta.put("confirmar_operacao", true);
                                    }else
                                        resposta.put("confirmar_operacao", false);
                                }
                                break;
                            }                             
                        }
                        dados_resposta = resposta.toString();                                               
                        break;    
                    // Retorna os dados de uma dada lixeira a partir de um codigo
                    case "BUSCAR":
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);                            
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){                                
                                resposta.put("codigo", atual.get("codigo"));
                                resposta.put("latitude", atual.get("latitude"));
                                resposta.put("longitude", atual.get("longitude"));
                                resposta.put("capacidadeMaxima", atual.get("capacidadeMaxima"));
                                resposta.put("capacidadeAtual", atual.get("capacidadeAtual"));
                                resposta.put("bloqueada", atual.get("bloqueada"));
                                dados_resposta = resposta.toString();                                                                
                            }
                        }
                        break;                                            
                }
                escreverLixeiras(lista, lixeiras);
                System.out.println(dados_resposta);
                escreverLixeiras(new ArrayList(), lixeiras);
                cliente_enviar.writeObject(dados_resposta);
                cliente_enviar.flush();           
                cliente_enviar.close();
                break;
            /** Operacoes realizadas pelos administradores */
            case "Administrador":   
                JSONArray caminhoes = getCaminhoes();                               
                switch(requisicao.getString("operacao")){
                    // Retorna lista com todas as lixeiras armazenadas no banco de dados
                    case "LISTAR_LIXEIRAS":
                        JSONObject dadosLixeiras = new JSONObject();                        
                        dadosLixeiras.put("Lixeira", lixeiras);                                                
                        dados_resposta = dadosLixeiras.toString();
                    break;
                    // Altera o estado de uma dada lixeira (altera a variavel boolean para true/false, represenando bloqueado/desbloqueado)
                    case "ALTERAR_ESTADO":                        
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                               if(atual.get("bloqueada") == requisicao.get("bloqueada"))
                                   resposta.put("resposta", false);
                               else{
                                    atual.put("bloqueada", requisicao.getBoolean("bloqueada"));                                                                                               
                                    resposta.put("resposta", true);
                                }
                            }
                        }
                        dados_resposta = resposta.toString();
                        escreverLixeiras(new ArrayList(), lixeiras);                                                
                        break;
                    case "ALTERAR_ORDEM":
                        for(int i = 0; i < caminhoes.length(); i++){
                            JSONObject atual = caminhoes.getJSONObject(i);                            
                            if(atual.getInt("codigo") == requisicao.getInt("caminhao")){
                                for(int j = 0; j < lixeiras.length();j++){
                                    JSONObject atualLixeira = lixeiras.getJSONObject(j);
                                    if(atualLixeira.getInt("codigo") == requisicao.getInt("Lixeira")){
                                        JSONArray lixeirasOrdem = new JSONArray();
                                        if(atual.getJSONArray("ordem") != null){
                                            lixeirasOrdem = atual.getJSONArray("ordem");                                            
                                        }
                                        lixeirasOrdem.put(requisicao.getInt("ordem"), requisicao.getInt("lixeira"));   
                                        resposta.put("resposta", true);
                                        resposta.put("mensagem", "Ordem alterada");
                                        break;
                                    }
                                    else{
                                        resposta.put("resposta", false);
                                        resposta.put("mensagem", "Lixeira não encontrada");
                                    }
                                }
                                break;
                            } else {
                                resposta.put("resposta", false);
                                resposta.put("mensagem", "Caminhao não encontrada");
                            }
                            JSONArray ordem = new JSONArray();                            
                            
                        }
                        break;
                    default:
                        dados_resposta = "Operação não encontrada";
                        break;
                }
                cliente_enviar.writeObject(dados_resposta);
                cliente_enviar.flush();           
                cliente_enviar.close();                
                break;
            /** Opercoes realizadas pelos caminhoes*/
            case "Caminhao":
                caminhoes = getCaminhoes();                               
                switch(requisicao.getString("operacao")){
                    // Cria um caminhao no banco de dados
                    case "CRIAR":
                        resposta.put("codigo", caminhoes.length()+1);
                        resposta.put("latitude", requisicao.get("latitude"));
                        resposta.put("longitude", requisicao.get("longitude"));
                        resposta.put("capacidadeMaxima", requisicao.get("capacidadeMaxima"));
                        resposta.put("capacidadeAtual", 0);                                                
                        JSONArray codigos = new JSONArray();
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = (JSONObject) lixeiras.get(i);
                            codigos.put(atual.getInt("codigo"));
                        }
                        resposta.put("lixeiras", codigos);
                        lista.add(resposta);
                        dados_resposta = resposta.toString();                        
                        System.out.println(dados_resposta);
                        break;     
                    // Realiza a busca de um caminhao no banco de dados e o retorna como objeto JSON
                    case "BUSCAR":                                                
                        for(int i = 0; i < caminhoes.length(); i++){
                            JSONObject atual = caminhoes.getJSONObject(i);                                                        
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){                
                                resposta = new JSONObject();
                                System.out.println(atual.getInt("codigo"));
                                resposta.put("codigo", atual.get("codigo"));
                                resposta.put("latitude", atual.get("latitude"));
                                resposta.put("longitude", atual.get("longitude"));
                                resposta.put("capacidadeMaxima", atual.get("capacidadeMaxima"));
                                resposta.put("capacidadeAtual", atual.get("capacidadeAtual"));                                
                                if(atual.has("lixeiras"))resposta.put("lixeiras", atual.get("lixeiras"));
                                resposta.put("resposta", true);
                                break;                                
                            } else {
                              resposta = new JSONObject();
                              resposta.put("codigo", -1);
                              resposta.put("resposta", false);
                            }                            
                        }
                        dados_resposta = resposta.toString();                                             
                        break;
                    // Realiza a coleta do lixo de uma lixeira por um caminhão
                    case "COLETAR":
                        JSONObject atualLixeira = new JSONObject();
                        JSONObject atualCaminhao = new JSONObject();                        
                        for(int i = 0; i < lixeiras.length(); i++){
                            atualLixeira = lixeiras.getJSONObject(i);
                            if(atualLixeira.getInt("codigo") == requisicao.getInt("lixeira")){
                                break; // Determina que a atualLixeira sera a lixeia encontrada atraves do codigo
                            }
                        }
                        for(int i = 0; i < caminhoes.length(); i++){
                            atualCaminhao = caminhoes.getJSONObject(i);
                            if(atualCaminhao.getInt("codigo") == requisicao.getInt("caminhao")){
                                break; // Determina que o atualCaminhao sera o caminhao encontrado atraves do codigo
                            }
                        }
                        if(atualLixeira.getInt("codigo") == requisicao.getInt("lixeira") && atualCaminhao.getInt("codigo") == requisicao.getInt("caminhao")){
                            double capacidadeAtualLixeira = atualLixeira.getDouble("capacidadeAtual");
                            double capacidadeMaximaCaminhao = atualCaminhao.getDouble("capacidadeMaxima");
                            double capacidadeAtualCaminhao = atualCaminhao.getDouble("capacidadeAtual");
                            double novaCapacidade = capacidadeAtualLixeira + capacidadeAtualCaminhao;
                            if(novaCapacidade > capacidadeMaximaCaminhao){
                                resposta.put("resposta", false);
                                resposta.put("mensagem", "Lixeira não coletada, descarregue o caminhão");
                            } else {                                
                                atualLixeira.put("capacidadeAtual", 0);
                                atualCaminhao.put("capacidadeAtual", novaCapacidade);
                                resposta.put("inserido", true);
                                resposta.put("mensagem", "Lixeira coletada");
                            }
                        }
                        dados_resposta = resposta.toString();                         
                        cliente_enviar.writeObject(dados_resposta);
                        cliente_enviar.flush();           
                        cliente_enviar.close();
                        escreverLixeiras(new ArrayList(), lixeiras);
                        escreverCaminhoes(lista, caminhoes);
                        break;                    
                    case "LISTAR_LIXEIRAS":
                        JSONObject caminhao = new JSONObject();
                        for(int i = 0; i < caminhoes.length(); i++){
                            caminhao = (JSONObject) caminhoes.get(i);
                            if(caminhao.getInt("codigo") == requisicao.getInt("codigo")){
                                break;
                            }
                        }
                        JSONArray lixeirasCaminhao = caminhao.getJSONArray("lixeiras");
                        resposta = new JSONObject();
                        resposta.put("lixeiras", lixeirasCaminhao);
                        dados_resposta = resposta.toString();                        
                        break;
                    case "ALTERAR_ORDEM":
                        for(int i = 0; i < caminhoes.length(); i++){
                            JSONObject atual = (JSONObject) caminhoes.get(i);
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                                atual.put("lixeiras", requisicao.getJSONArray("ordem"));
                                break;
                            }
                        }
                        dados_resposta = "confirmado";
                        break;
                    // Realiza listagem de lixeiras a serem coletadas pelo caminhao por capacidade
                    case "LISTAR_LIXEIRAS_CAPACIDADE":
                        List<Object> ordenadas = new ArrayList();
                        int maiorPosicao = 0;
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);
                            if(i == 0){
                                ordenadas.add(atual);                        
                            } else{
                                for(int j = 0; j < ordenadas.size(); j++){
                                    JSONObject ordenada = (JSONObject) ordenadas.get(j);                            
                                    if(atual.getDouble("capacidadeAtual") <= ordenada.getDouble("capacidadeAtual")){                                        
                                            ordenadas.add(j, atual);                                
                                            break;
                                    } else {
                                        if(j == ordenadas.size()-1){
                                            JSONObject maior = (JSONObject) ordenadas.remove(j);                                        
                                            ordenadas.add(j, atual);
                                            ordenadas.add(j, maior);
                                            maior = atual;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        
                       JSONArray convertida = new JSONArray();
                       for(int i = ordenadas.size(); i > 0; i--){
                           convertida.put(ordenadas.get(i-1));                           
                       }                      
                                                                                                                                       
                       JSONObject convertido = new JSONObject();
                       convertido.put("Lixeira", convertida);
                       dados_resposta = convertido.toString();                                               
                        break;
                    default:
                        dados_resposta = "[Caminhao] Operacao nao encontrada";
                        break;
                }
                    cliente_enviar.writeObject(dados_resposta);
                    cliente_enviar.flush();           
                    cliente_enviar.close();                    
                    escreverCaminhoes(lista, caminhoes);
                break;
            default:
                dados_resposta = "Cliente não encontrado/especificado!";
                cliente_enviar.writeObject(dados_resposta);
                cliente_enviar.flush();           
                cliente_enviar.close();
                break;
        }                                   
      }
    }
    catch(Exception e) {
       System.out.println("Erro: " + e.getMessage());
    }    
  }
}