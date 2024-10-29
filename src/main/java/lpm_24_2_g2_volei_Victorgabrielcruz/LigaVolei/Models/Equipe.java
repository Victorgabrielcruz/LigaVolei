package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.DTO.EquipeDTO;

/**
 * Classe "Equipe" para sistema de Liga LPM de Vôlei
 */
@Entity
@Table(name = "Equipes")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEquipe;
    private String nome;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<PartidaDeVolei> partidas;
    private int quantPartidas;

    /**
     * Cria uma equipe com nome recebido por parâmetro e 0 partidas disputadas.
     *
     * @param nome Nome da equipe. Caso seja vazio, será criada com nome "Sem
     * Nome"
     */
    public Equipe(String nome) {
        if (nome.length() == 0) {
            nome = "Sem Nome";
        }
        this.nome = nome;
        partidas = new LinkedList<>();
        quantPartidas = 0;
    }

    public Equipe() {

    }

    /**
     * Retorna o nome da equipe.
     *
     * @return String com o nome da equipe.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Registra uma partida para a equipe. Não há obrigação da partida estar
     * terminada.
     *
     * @param partida Partida a ser registrada.
     * @return Quantidade de partidas disputadas pela equipe.
     */
    public int registrarPartida(PartidaDeVolei partida) {
        //if(quantPartidas<MAX_PARTIDAS){
        partidas.add(partida);
        quantPartidas++;
        //}
        return quantPartidas;
    }

    /**
     * A partir das partidas registradas, verifica e retorna a quantidade de
     * vitórias da equipe
     *
     * @return Total de vitórias da equipe.
     */
    private int totalVitorias() {
        int total = 0;
        for (int i = 0; i < quantPartidas; i++) {
            String vencedor = partidas.get(i).vencedorDoJogo();
            if (vencedor.equals(nome)) {
                total++;
            }
        }
        return total;
    }

    /**
     * A partir das partidas registradas, verifica e retorna a quantidade de
     * derrotas da equipe
     *
     * @return Total de derrotas da equipe.
     */
    private int totalDerrotas() {
        return quantPartidas - totalVitorias();
    }

    /**
     * Calcula o aproveitamento total da equipe (vitórias/derrotas). Em caso de
     * 0 derrotas, o aproveitamento é retornado como 100,00.
     *
     * @return Aproveitamento de equipe (vitórias/derrotas), podendo ser 100 em
     * caso de 0 derrotas.
     */
    public double aproveitamentoTotal() {
        if (quantPartidas == 0) {
            return 0;
        }
        return (double) totalVitorias() / quantPartidas * 100;
    }

    /**
     * Calcula o aproveitamento em sets da equipe (vencidos/perdidos). Em caso
     * de 0 sets perdidos, o aproveitamento é retornado como 100,00.
     *
     * @return Aproveitamento em sets da equipe (vencidos/perdidos), podendo ser
     * 100 em caso de 0 sets perdidos.
     */
    public double aproveitamentoSets() {
        int setsVencidos = 0;
        int quantSets = 0;
        for (int i = 0; i < quantPartidas; i++) {
            setsVencidos += partidas.get(i).setsVencidosEquipe(nome);
            quantSets += partidas.get(i).setsDisputados();
        }
        if (quantPartidas == 0) {
            return 0;
        }
        return (double) setsVencidos / (quantPartidas * quantSets) * 100;
    }

    /**
     * Cria um relatório da equipe. Uma única linha contendo seu nome, total de
     * vitórias, total de derrotas, aproveitamento total e aproveitamento em
     * sets.
     *
     * @return String com o formato descrito acima.
     */
    public String relatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equipe: ").append(nome).append(" | ");
        sb.append("Quantidade de partidas: ").append(quantPartidas).append(" | ");
        sb.append("Vitórias: ").append(totalVitorias()).append(" | ");
        sb.append("Derrotas: ").append(totalDerrotas()).append(" | ");
        sb.append("Aproveitamento Total: ").append(String.format("%.2f", aproveitamentoTotal())).append("% | ");
        sb.append("Aproveitamento de Sets: ").append(String.format("%.2f", aproveitamentoSets())).append("%");
        return sb.toString();
    }

    /**
     * Gera um DTO da Equipe, com os dados relevantes para uma temporada: nome,
     * vitórias, derrotas e aproveitamentos.
     *
     * @return "Objeto" DTO com os dados de nome (str), vitórias (int), derrotas
     * (int), aproveitamento total (dbl), aproveitamento de sets (dbl)
     */
    public EquipeDTO getDTO() {
        return new EquipeDTO(nome, totalVitorias(), totalDerrotas(), aproveitamentoTotal(), aproveitamentoSets());
    }
}
