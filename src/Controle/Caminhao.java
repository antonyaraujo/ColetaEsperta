/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Math.round;
import java.net.Socket;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author antony
 */
public class Caminhao {
    private int codigo;
    private double longitude;
    private double latitude;
    private double capacidadeMaxima;
    private double capacidadeAtual;        

    public Caminhao(int codigo, double latitude, double longitude, double capacidadeMaxima) {
        this.codigo = codigo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.capacidadeMaxima = capacidadeMaxima;
        this.capacidadeAtual = 0;        
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public double getCapacidadeAtual() {
        return capacidadeAtual;
    }

    public void setCapacidadeAtual(double capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual;
    }
            
    /**
     * Realiza a criacao de um caminhao no Banco de Dados com os valores correspondente
     * via Servidor
     * @param capacidadeMaxima - valor (dobule) em m³ de capacidade maxima do caminhao
     * @param latitude - latitude do caminhao
     * @param longitude - longitude do caminhao
     * @return Caminhao - objeto contendo os dados do caminhao criado, se for criado
     */
    public static Caminhao criarCaminhao(double capacidadeMaxima, double latitude, double longitude){
        try(Socket nuvem = new Socket("172.16.0.1", 40000)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                        
            //ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
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
            System.out.println(requisicao.toString());
            return new Caminhao(requisicao.getInt("codigo"), latitude, longitude, capacidadeMaxima);
        }catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Realiza a busca de um caminhao no BD a partir de um dado codigo
     * @param codigo - codigo pelo qual sera buscado o caminhao correspondente
     * @return Caminhao - retorna o caminhao com os dados correspondente caso
     * exista um caminhao com o codigo informado
     * @return null - caso nao exista nenhum caminhao com o codigo informado
     */
    public static Caminhao buscarCaminhao(int codigo){
         try(Socket nuvem = new Socket("172.16.0.1", 40000)){
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
            
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();            
            Caminhao caminhao;
            if(requisicao.getInt("codigo") != -1){
            caminhao =  new Caminhao(requisicao.getInt("codigo"), requisicao.getDouble("latitude"), requisicao.getDouble("longitude"), requisicao.getDouble("capacidadeMaxima"));            
            caminhao.setCapacidadeAtual(requisicao.getDouble("capacidadeAtual"));
            } else{
                caminhao = new Caminhao(requisicao.getInt("codigo"), 0, 0, 0);
            }
            return caminhao;
        }catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retorna todas as lixeiras de um dado caminhao
     * @param codigoCaminhao - codigo do caminhao de qual sera buscado as lixeiras
     * @return lixeiras - JSONArray que contem JSONObjects das lixeiras 
     * a serem recolhidas pelo caminhao
     */
    public static JSONArray getLixeiras(int codigoCaminhao){
        try(Socket nuvem = new Socket("172.16.0.1", 40000)){
            /** Realiza a solicitacao de busca das lixeiras do caminhao do codigo informado */
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Administrador");
            json.put("operacao", "LISTAR_LIXEIRAS");                       
            json.put("caminhao", codigoCaminhao);                       
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
     * @return valor em % da ocupacao da lixeira aproximado em duas casas decimais
     */
    public static double getCapacidade(double atual, double maxima){        
        double porcentagem = (atual/maxima)*100;                
        return round(porcentagem*100)/100.0;
    }
    
    /**
     * Realiza a exibicao das lixeiras
     * @param lixeiras - lista de lixeiras
     */
    public static void exibirLixeiras(JSONArray lixeiras){
        System.out.println("Lista de Lixeiras");
        for(int i = 0; i < lixeiras.length(); i++){
            JSONObject atual = (JSONObject)lixeiras.get(i);
            System.out.println("\nCódigo: " + atual.getInt("codigo"));
            if(atual.getBoolean("bloqueada"))
                System.out.println("Estado: bloqueada");
            else
                System.out.println("Estado: desbloqueada");
            System.out.println("Capacidade: " + getCapacidade(atual.getDouble("capacidadeAtual"), atual.getDouble("capacidadeMaxima")) + "%");
            }
    }
    
    /**
     * Metodo responsavel por fazer as solicitacoes de coleta de Lixeira no servidor
     * @param lixeira - Lixeira a ser coletada
     * @param caminhao - Caminhao que coletara a lixeira
     * @return true - Se a coleta da lixeira tiver sido realizada com sucesso
     * @return false - Se a lixeira nao tiver sido coletada
     */
    public static boolean coletarLixeira(JSONObject lixeira, Caminhao caminhao){
        try(Socket nuvem = new Socket("172.16.0.1", 40000)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "COLETAR");
            json.put("lixeira", lixeira.getInt("codigo"));
            json.put("caminhao", caminhao.getCodigo());            
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
            return requisicao.getBoolean("resposta");
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        }        
        return false;
    }
    
    /**
     * Metodo responsavel por fazer a requisicao de esvaziamento (zera a 
     * capacidadeAtual) do Caminhao
     * @param caminhao - Caminhao a ser esvaziado
     * @return true - Se o caminhao tiver sido esvaziado
     * @return false - Se o caminhao nao tiver sido esvaziado
     */
    public static boolean esvaziarCaminhao(Caminhao caminhao){
        try(Socket nuvem = new Socket("172.16.0.1", 40000)){
            ObjectOutputStream nuvem_enviar = new ObjectOutputStream(nuvem.getOutputStream());                                    
            JSONObject json = new JSONObject();
            json.put("cliente", "Caminhao");
            json.put("operacao", "ESVAZIAR");            
            json.put("caminhao", caminhao.getCodigo());            
            String dados = json.toString();
            nuvem_enviar.writeObject(dados);
            nuvem_enviar.flush();                
            
            /** Recebimento dos dados da solicitação */
            ObjectInputStream nuvem_receber = new ObjectInputStream(nuvem.getInputStream());
            dados = (String) nuvem_receber.readObject();                        
            JSONObject requisicao = new JSONObject(dados);                                    
            
            boolean resposta = false;
            if(dados.equals("true"))
                resposta = true;
            nuvem_receber.close();
            nuvem_enviar.close();
            nuvem.close();                 
            
            return requisicao.getBoolean("resposta");
        }catch(Exception e) {            
           System.out.println("Erro: " + e.getMessage());
        }        
        return false;
    }
    
    public static void main(String[] args)  {
        System.out.println("   ____      _      _        _____                     _        \n" +
        "  / ___|___ | | ___| |_ __ _| ____|___ _ __   ___ _ __| |_ __ _ \n" +
        " | |   / _ \\| |/ _ \\ __/ _` |  _| / __| '_ \\ / _ \\ '__| __/ _` |\n" +
        " | |__| (_) | |  __/ || (_| | |___\\__ \\ |_) |  __/ |  | || (_| |\n" +
        "  \\____\\___/|_|\\___|\\__\\__,_|_____|___/ .__/ \\___|_|   \\__\\__,_|\n" +
        "                                      |_|                       ");
        int opcao = 1;
        Scanner leitor = new Scanner(System.in);
        System.out.println("Escolha uma opção: "
                + "\n1. Criar caminhão;"
                + "\n2. Selecionar caminhão");                
        opcao = leitor.nextInt();
        Caminhao caminhao;
        if(opcao == 1){
            System.out.println("Informe a capacidade total da Lixeira (m³): ");
            double capacidade = leitor.nextDouble();
            System.out.println("Informe a latitude: ");
            double lat = leitor.nextDouble();
            System.out.println("Informe a longitude: ");
            double lon = leitor.nextDouble();
            caminhao = criarCaminhao(capacidade, lat, lon);
        } else {
            int codigo = -1;
            do{
                System.out.println("Informe o codigo de um caminhao exitente: ");
                codigo = leitor.nextInt();
                caminhao = buscarCaminhao(codigo);                
                if(codigo != caminhao.getCodigo()){
                    System.out.println("Lixeira não encontrada, informe outro código");                
                    codigo = caminhao.getCodigo();
                }                
            }while(codigo == -1);            
        }
        
        do{
            System.out.println("Escolha uma opção: "
                    + "\n1. Ver lista de lixeiras;"
                    + "\n2. Recolher Lixeira"
                    + "\n3. Esvaziar caminhão"
                    + "\n0. Sair");
            opcao = leitor.nextInt();
            JSONArray lixeiras = getLixeiras(caminhao.getCodigo());            
            switch(opcao){
                case 1:                                                            
                    exibirLixeiras(getLixeiras(caminhao.getCodigo()));                    
                    break;
                case 2:                                        
                    boolean confirmacao = false;
                    JSONObject lixeira = new JSONObject();
                    
                    do{
                        System.out.println("Informe o código da lixeira a ser coleta");
                        int codigo = leitor.nextInt();
                        for(int i = 0; i < lixeiras.length(); i++){
                            JSONObject iteracao = (JSONObject) lixeiras.getJSONObject(i);                            
                            if(iteracao.getInt("codigo") == codigo){                               
                                confirmacao = true;
                                lixeira = iteracao;                                
                                break;             
                            }
                        }                        
                        if(!confirmacao)
                                System.out.println("Lixeira não encontrada, informe um novo codigo");
                    }while(!confirmacao);
                    
                    boolean saida = coletarLixeira(lixeira, caminhao);                    
                    if(saida){
                        System.out.println("Lixeira coletada!");
                    } else {
                        System.out.println("Lixeira não coletada, descarregue o caminhão");
                    }
                    
                    break;
                case 3:
                    boolean resposta = esvaziarCaminhao(caminhao);
                    if(resposta)
                        System.out.println("Caminhão esvaziado!");
                    else
                        System.out.println("Caminhão não esvaziado!");
                    break;
            }
        }while(opcao != 0);
    }
}
