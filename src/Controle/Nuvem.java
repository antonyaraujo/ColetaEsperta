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
    static File bd;
    
    public static JSONArray getLixeiras() throws IOException{
        bd = new File("./bancodedados.json");
        if(bd.exists() == false){            
            bd.createNewFile();
            FileWriter escritor = new FileWriter(bd);
            BufferedWriter bw = new BufferedWriter(escritor);
            bw.write("{'Lixeira':[]}");
            bw.close();
            escritor.close();            
        }
        FileReader leitor = new FileReader(bd);
        BufferedReader buffer = new BufferedReader(leitor);
        JSONObject json = new JSONObject(buffer.readLine());                
        JSONArray lixeiras = json.getJSONArray("Lixeira");                            
        buffer.close();
        leitor.close();               
        return lixeiras;
    }
    
    public static void escreverLixeiras(List lista, JSONArray lixeiras) throws IOException{
        FileWriter escritor = new FileWriter(bd);
        BufferedWriter bw = new BufferedWriter(escritor);

        for(int i = 0; i < lista.size(); i++){
            lixeiras.put(lista.get(i));
        }
        JSONObject json = new JSONObject();
        json.put("Lixeira", lixeiras);
        bw.write(json.toString());
        bw.close(); 
        escritor.close();                    
    }
    
    public static void main(String[] args) throws IOException {
    try {
      // Instancia o ServerSocket ouvindo a porta 12345
      ServerSocket servidor = new ServerSocket(12345);
      System.out.println("Servidor ouvindo a porta 12345");
      while(true) {
        // o método accept() bloqueia a execução até que
        // o servidor receba um pedido de conexão
        Socket cliente = servidor.accept();        
        System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort());                
        
        // Recebe e processa requisicao        
        ObjectInputStream cliente_receber = new ObjectInputStream(cliente.getInputStream());        
        String dados = (String) cliente_receber.readObject();        
        JSONObject requisicao = new JSONObject(dados);
                        
        switch(requisicao.getString("cliente")){
            /** Operacao realizada com lixeiras*/
            case "Lixeira":                
                JSONArray lixeiras = getLixeiras();
                List<Object> lista = new ArrayList();
                /** Realiza a selecao das operacoes */
                switch(requisicao.getString("operacao")){
                    case "CRIAR":
                        JSONObject objeto = new JSONObject();
                        objeto.put("codigo", lixeiras.length()+1);
                        objeto.put("latitude", requisicao.get("latitude"));
                        objeto.put("longitude", requisicao.get("longitude"));
                        objeto.put("capacidadeMaxima", requisicao.get("capacidadeMaxima"));
                        objeto.put("capacidadeAtual", 0);
                        objeto.put("bloqueada", false);
                        lista.add(objeto);
                        ObjectOutputStream cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());
                        dados = objeto.toString();
                        cliente_enviar.writeObject(dados);
                        cliente_enviar.flush();
                        cliente_enviar.close();
                        break;
                    case "ALTERAR_CAPACIDADE":
                        cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());
                        objeto = new JSONObject();
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                                if(requisicao.getString("operacao").equals("adicionar")){
                                    double novaCapacidade = requisicao.getDouble("quantidade") + atual.getDouble("capacidadeAtual");
                                    if(novaCapacidade <= atual.getDouble("capacidadeMaxima")){
                                        atual.put("capacidadeAtual", novaCapacidade);
                                        objeto.put("confirmar_operacao", true);
                                    }else
                                        objeto.put("confirmar_operacao", false);
                                } else{
                                    double novaCapacidade = atual.getDouble("capacidadeAtual") - requisicao.getDouble("quantidade");
                                    if(novaCapacidade >= 0){
                                        atual.put("capacidadeAtual", novaCapacidade);
                                        objeto.put("confirmar_operacao", true);
                                    }else
                                        objeto.put("confirmar_operacao", false);
                                }
                                break;
                            }                             
                        }
                        dados = objeto.toString();
                        cliente_enviar.writeObject(dados);
                        cliente_enviar.flush();
                        cliente_enviar.close();                        
                        break;
                    case "BLOQUEAR":
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                               atual.put("bloqueada", requisicao.get("bloqueada"));                                                                                               
                            }                             
                    }
                        break;                        
                    case "GET":                        
                        cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());                        
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject atual = lixeiras.getJSONObject(i);                            
                            if(atual.getInt("codigo") == requisicao.getInt("codigo")){
                                objeto = new JSONObject();
                                objeto.put("codigo", atual.get("codigo"));
                                objeto.put("latitude", atual.get("latitude"));
                                objeto.put("longitude", atual.get("longitude"));
                                objeto.put("capacidadeMaxima", atual.get("capacidadeMaxima"));
                                objeto.put("capacidadeAtual", atual.get("capacidadeAtual"));
                                objeto.put("bloqueada", atual.get("bloqueada"));
                                dados = objeto.toString();
                                cliente_enviar.writeObject(dados);
                                cliente_enviar.flush();           
                                cliente_enviar.close();
                            }
                        }
                        break;
                        
                }
                escreverLixeiras(lista, lixeiras);
                break;
                
            case "Administrador":
                lixeiras = getLixeiras();
                switch(requisicao.getString("operacao")){
                    case "GET":                                    
                        ObjectOutputStream cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());                                                
                        JSONObject dadosLixeiras = new JSONObject();                        
                        dadosLixeiras.put("Lixeira", lixeiras);                        
                        cliente_enviar.writeObject(dadosLixeiras.toString());
                        cliente_enviar.flush();           
                        cliente_enviar.close();
                    break;
                    case "POST":
                        JSONObject resposta = new JSONObject();
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
                        escreverLixeiras(new ArrayList(), lixeiras);
                        cliente_enviar = new ObjectOutputStream(cliente.getOutputStream());                        
                        cliente_enviar.writeObject(resposta.toString());
                        cliente_enviar.flush();           
                        cliente_enviar.close();
                        break;
                }
                break;
            default:
                break;
        }
        
      }
    }
    catch(Exception e) {
       System.out.println("Erro: " + e.getMessage());
    }
    finally {
    }
  }
}
