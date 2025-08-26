# FinTrack

[Acesse Aqui! - FinTrack](https://fintrack-c9cm.onrender.com)

Gerenciador colaborativo de **metas financeiras em grupo**, com geração de certificados personalizados ao atingir os objetivos.  
Construído com **Java + Spring** e uma stack moderna de frontend e infraestrutura.

---

## 📌 Sobre o Projeto

O **FinTrack** é um sistema para **criar e acompanhar metas financeiras coletivas**, permitindo que grupos organizem contribuições e acompanhem o progresso.  
Ao concluir uma meta, o sistema gera automaticamente um **certificado em JPG** usando `BufferedImage` e `Graphics2D`.  
A autenticação é feita via **Google OAuth2**, garantindo segurança e praticidade no login.

---

## ✨ Funcionalidades

- ✅ Cadastro e gerenciamento de **metas financeiras**
- ✅ Inclusão de **contribuintes** por meta
- ✅ Registro de **contribuições individuais**
- ✅ **Login com Google** (OAuth2)
- ✅ Geração automática de **certificado JPG** ao concluir a meta
- ✅ Interface web responsiva com **Thymeleaf + TailwindCSS + AlpineJS**
- ✅ Persistência com **PostgreSQL**
- ✅ Deploy em **Render** com **Docker**
- ✅ CI/CD com **Github Actions**

---

## 🛠 Tecnologias

### Backend
- Java 17+
- Spring Boot (Web, Security, Data JPA, OAuth2 Client)
- Jakarta Persistence (JPA)
- PostgreSQL
- BufferedImage & Graphics2D (certificados)
- Docker compose

### Frontend
- HTML + Thymeleaf
- TailwindCSS
- AlpineJS

### Infra
- Docker
- Render (Deploy)
- Github Actions

---

## ⚙️ Instalação

### Pré-requisitos
- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/) ou [Gradle](https://gradle.org/)
- [Docker](https://www.docker.com/)
- [PostgreSQL](https://www.postgresql.org/)

### Passos
```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/FinancesGroupJava.git
cd fintrack

# Construir o projeto
./mvnw clean package

# Rodar com Docker
docker-compose up --build
