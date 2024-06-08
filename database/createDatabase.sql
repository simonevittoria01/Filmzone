DROP DATABASE IF EXISTS progettoTSW;
CREATE DATABASE progettoTSW;
use progettoTSW;


CREATE TABLE utente(
	id int auto_increment PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL
    );
    
CREATE TABLE ordine(
	id int auto_increment primary key,
	data_acquisto date default null,
    utente int not null,
    foreign key(utente) references utente(id)
    );

CREATE TABLE regista(
	id int auto_increment PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL
    );
    
CREATE TABLE genere(
	nome varchar(30) primary key);
    
CREATE TABLE film(
	id int auto_increment primary key,
    nome varchar(255) not null, 
    durata int not null,
    data_uscita date not null,
    genere varchar(30) not null,
    descrizione varchar(255) not null,
    prezzo float not null,
    copertina longblob,
    regista int not null,
    foreign key(genere) references genere(nome),
    foreign key(regista) references regista(id)
    );
    
CREATE TABLE attore(
	 id int auto_increment PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL
    );
    
CREATE TABLE recita(
	film int not null,
    attore int not null,
    primary key(film, attore),
    foreign key(film) references film(id) ON DELETE CASCADE,
    foreign key(attore) references attore(id)
    );
    
CREATE TABLE sala(
	num_sala int primary key,
    num_posti int not null
    );
    
CREATE TABLE proiezione(
	data_ora datetime not null,
    posti_disponibili int not null,
    film int not null,
    num_sala int not null,
    primary key(film, data_ora, num_sala),
    foreign key (film) references film(id) ON DELETE CASCADE,
    foreign key(num_sala) references sala(num_sala)
    );
    
CREATE TABLE biglietto(
	id int auto_increment primary key,
    fila varchar(1) not null,
    posto int not null,
    prezzo float not null,
    num_sala int not null,
    data_ora datetime not null, 
    film int not null,
    foreign key(film, data_ora, num_sala) references proiezione(film, data_ora, num_sala) ON DELETE CASCADE
    );

CREATE TABLE recensione(
	utente int not null,
    film int not null,
    voto float not null,
    commento varchar(255) default null,
    primary key(utente, film),
    foreign key (utente) references utente(id),
    foreign key(film) references film(id) ON DELETE CASCADE
    );
    
CREATE TABLE aggiungere(
	ordine int not null,
    biglietto int not null,
    primary key(ordine, biglietto),
    foreign key (ordine) references ordine(id) ON DELETE CASCADE,
    foreign key(biglietto) references biglietto(id) ON DELETE CASCADE
    );
    
INSERT INTO utente(nome, cognome, email, password) VALUES 
("admin", "admin", "filmzone@gmail.com", "7c4a8d09ca3762af61e59520943dc26494f8941b");



INSERT INTO genere(nome) VALUES 
("Azione"),
("Avventura"),
("Commedia"),
("Drammatico"),
("Fantasy"),
("Horror"),
("Thriller");

INSERT INTO sala(num_sala, num_posti) VALUES 
(1, 100),
(2, 100),
(3, 100),
(4, 100),
(5, 100),
(6, 100);

