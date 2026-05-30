# Trabalho_Final_PDM

# Mystic Creatures

## Integrantes do Grupo

* João Pedro de Jesus Perin

Professor(a): Ana Karina Dourado Salina de Oliveira

---

# Visão Geral do Software

O projeto consiste em um aplicativo Android desenvolvido em Java utilizando o banco de dados Room.

O aplicativo possui sistema de cadastro e login de usuários, recuperação de senha, armazenamento de foto do usuário utilizando câmera/galeria e gerenciamento de criaturas cadastradas no sistema.

O objetivo do software é permitir que usuários realizem o gerenciamento de criaturas fictícias de maneira simples, armazenando informações como nome, descrição e imagem.

---

# Usuários do Sistema

## Usuário comum

O usuário pode:

* Realizar cadastro no aplicativo
* Fazer login
* Recuperar senha
* Visualizar sua foto e nome após login
* Cadastrar criaturas
* Alterar criaturas cadastradas
* Excluir criaturas
* Buscar criaturas pelo nome
* Visualizar lista de criaturas cadastradas

---

# Requisitos Funcionais

## RF01 – Cadastro de Usuário

O sistema deve permitir cadastrar usuários contendo:

* nome
* email
* senha
* foto

## RF02 – Login

O sistema deve permitir autenticação utilizando email e senha.

## RF03 – Recuperação de Senha

O sistema deve permitir alterar a senha utilizando o email cadastrado.

## RF04 – Exibição de Usuário

O sistema deve mostrar o nome e foto do usuário após login.

## RF05 – Cadastro de Criaturas

O sistema deve permitir cadastrar criaturas contendo:

* nome
* descrição
* imagem

## RF06 – Alteração de Criaturas

O sistema deve permitir alterar dados das criaturas cadastradas.

## RF07 – Exclusão de Criaturas

O sistema deve permitir excluir criaturas cadastradas.

## RF08 – Busca de Criaturas

O sistema deve permitir buscar criaturas pelo nome.

## RF09 – Listagem

O sistema deve listar todas as criaturas cadastradas.

---

# Entradas do Sistema

O aplicativo recebe as seguintes entradas:

## Usuário

* Nome
* Email
* Senha
* Foto

## Criaturas

* Nome da criatura
* Descrição
* Imagem

---

# Processamento do Sistema

O aplicativo realiza:

* Validação de login
* Armazenamento de dados utilizando Room Database
* Conversão de imagens para byte array
* Busca de criaturas por nome
* Atualização de dados cadastrados
* Exclusão de registros
* Recuperação de senha

---

# Saídas do Sistema

O sistema apresenta:

* Mensagens de sucesso e erro
* Lista de criaturas cadastradas
* Resultado de buscas
* Nome e foto do usuário logado
* Confirmações de cadastro, alteração e exclusão

---

# Tecnologias Utilizadas

* Java
* Android Studio
* Room Database
* SQLite
* ViewBinding
* Camera/Galeria
* MediaPlayer

---

# Estrutura do Projeto

## Activities

* ActivityCadastroUsuario
* ActivityLogin
* ActivityRecuperarSenha
* ActivityRacas
* MainActivity

## Banco de Dados

* AppDatabase

## DAOs

* UsuarioDao
* RacasDao

## Entidades

* Usuario
* Racas

---

# Funcionalidades Extras

* Sons ao clicar nos botões
* Busca dinâmica
* Imagens nas criaturas
* Confirmação antes de excluir
* Interface personalizada

---
