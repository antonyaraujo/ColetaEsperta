```
   ____      _      _        _____                     _        
  / ___|___ | | ___| |_ __ _| ____|___ _ __   ___ _ __| |_ __ _ 
 | |   / _ \| |/ _ \ __/ _` |  _| / __| '_ \ / _ \ '__| __/ _` |
 | |__| (_) | |  __/ || (_| | |___\__ \ |_) |  __/ |  | || (_| |
  \____\___/|_|\___|\__\__,_|_____|___/ .__/ \___|_|   \__\__,_|
                                      |_|                       
```

# ColetaEsperta
Repositório criado para o programa que simula o sistema de coleta inteligente de lixo em uma cidade inteligente (smartcity)  utilizando comunicação via Rede em uma arquitetura IoT centralizada.

#Sumário
[Pré-requisitos](#pre-requisitos)
Deploys
	[Servidor (Nuvem)](#deploy-do-servidor)
	[Lixeira](#deploy-da-lixeira)
	[Administrador](#deploy-do-administrador)
	[Caminhão](#deploy-do-caminhao)

## Pré-requisitos
Para fazer o deploy da aplicação é necessário que você possua previamente o Docker instalado, as instruções para a instalação do docker estão em: .

## Deploy do Servidor
Para realizar o deploy do servidor em um container é necessário estar no diretório raiz do projeto, tendo issos sendo feito, deve-se: 

> docker build -f Dockerfile_Nuvem -t coleta_esperta/nuvem .

Esse comando criará o contâiner liberando as portas necessárias para o uso do servidor. Tendo isso feito deve-se criar a rede na qual o serviço irá ser utilizado

> docker network create --driver=bridge  --subnet=172.16.0.0/25 --ip-range=172.16.0.0/25 --gateway=172.16.0.126

A rede criada suporta um total de 126 conexões, incluindo nelas a do próprio servidor. Para iniciar e adicionar o servidor a subrede criada, no endereço utilizado pela aplicação, deve-se utilizar o comando:

> docker run --net=rede_coletaesperta --ip=172.16.0.1 -it --rm --name nuvem coleta_esperta/nuvem

O servidor também pode ser executado em segundo plano, caso desejado: 

> docker run -d --net=rede_coletaesperta --ip=172.16.0.1 -it --rm --name nuvem coleta_esperta/nuvem

## Deploy da Lixeira

Para realizar o deploy da lixeira em um contâiner é necessário, inicialmente, criar o contâiner:
 
> docker build -f Dockerfile_Lixeira -t coleta_esperta/lixeira .

Criado o contâiner, deve-se executá-lo, adicionando-o a rede e, nesse caso, não há necessidade de especificar o ip, portanto dá-se o seguinte comando:

> docker run --net=rede_coletaesperta -it --rm --name lixeira coleta_esperta/lixeira

Como a proposta do sistema é que este possua várias lixeiras, é interessante criar vários contâineres como exibido acima alterando o nome dos contâineres para evitar conflitos de execução.

## Deploy do Administrador

Para realizar o deploy do Administrador em um contâiner é necessário, inicialmente, criar o contâiner:
 
> docker build -f Dockerfile_Administrador -t coleta_esperta/administrador .

Criado o contâiner, deve-se executá-lo, adicionando-o a rede e, nesse caso, não há necessidade de especificar o ip, portanto dá-se o seguinte comando:

> docker run --net=rede_coletaesperta -it --rm --name administrador coleta_esperta/administrador

## Deploy do Caminhão

Para realizar o deploy do Caminhão em um contâiner é necessário, inicialmente, criar o contâiner:
 
> docker build -f Dockerfile_Caminhao -t coleta_esperta/caminhao .

Criado o contâiner, deve-se executá-lo, adicionando-o a rede e, nesse caso, não há necessidade de especificar o ip, portanto dá-se o seguinte comando:

> docker run --net=rede_coletaesperta -it --rm --name caminhao coleta_esperta/caminhao
