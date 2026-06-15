package com.example.bdroomcomcamera.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "racas",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("usuarioId")}
)
public class Racas {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int usuarioId;
    private String nomeRaca;
    private String descricao;
    private byte[] imagem;
    private String audioUri;
    private String audioNome;
    private String tipo;
    private String habitat;
    private String alinhamento;
    private int nivel;
    private int pontosVida;
    private int defesa;
    private int dificuldade;
    private String ataques;
    private String habilidades;
    private String fraquezas;
    private String resistencias;
    private String recompensas;
    private String anotacoesMestre;
    public Racas(String nomeRaca, String descricao, byte[] imagem){
        this.nomeRaca = nomeRaca;
        this.descricao = descricao;
        this.imagem = imagem;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    public String getNomeRaca() {
        return nomeRaca;
    }
    public void setNomeRaca(String nomeRaca) {
        this.nomeRaca = nomeRaca;
    }
    public String getDescricao() { return descricao;    }
    public void setDescricao(String descricao) { this.descricao = descricao;    }
    public byte[] getImagem() {
        return imagem;
    }
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
    public String getAudioUri() {
        return audioUri;
    }
    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }
    public String getAudioNome() {
        return audioNome;
    }
    public void setAudioNome(String audioNome) {
        this.audioNome = audioNome;
    }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getHabitat() { return habitat; }
    public void setHabitat(String habitat) { this.habitat = habitat; }
    public String getAlinhamento() { return alinhamento; }
    public void setAlinhamento(String alinhamento) { this.alinhamento = alinhamento; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public int getPontosVida() { return pontosVida; }
    public void setPontosVida(int pontosVida) { this.pontosVida = pontosVida; }
    public int getDefesa() { return defesa; }
    public void setDefesa(int defesa) { this.defesa = defesa; }
    public int getDificuldade() { return dificuldade; }
    public void setDificuldade(int dificuldade) { this.dificuldade = dificuldade; }
    public String getAtaques() { return ataques; }
    public void setAtaques(String ataques) { this.ataques = ataques; }
    public String getHabilidades() { return habilidades; }
    public void setHabilidades(String habilidades) { this.habilidades = habilidades; }
    public String getFraquezas() { return fraquezas; }
    public void setFraquezas(String fraquezas) { this.fraquezas = fraquezas; }
    public String getResistencias() { return resistencias; }
    public void setResistencias(String resistencias) { this.resistencias = resistencias; }
    public String getRecompensas() { return recompensas; }
    public void setRecompensas(String recompensas) { this.recompensas = recompensas; }
    public String getAnotacoesMestre() { return anotacoesMestre; }
    public void setAnotacoesMestre(String anotacoesMestre) { this.anotacoesMestre = anotacoesMestre; }
    @Override
    public String toString() {
        return nomeRaca;
    }
    }
