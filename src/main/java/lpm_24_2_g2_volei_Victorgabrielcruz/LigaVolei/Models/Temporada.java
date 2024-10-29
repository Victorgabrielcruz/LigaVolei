package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * A classe {@code Temporada} representa uma temporada de voleibol, contendo
 * informações sobre o ano e as equipes participantes.
 *
 * <p>
 * Oferece funcionalidades para cadastrar equipes, buscar uma equipe específica,
 * determinar a equipe líder e exibir uma tabela com o aproveitamento das
 * equipes.
 *
 * @author
 * @version 1.0
 */
@Entity(name = "Temporadas")
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name = "ano")
    private int ano;

    @OneToMany
    private List<Equipe> equipes;

    /**
     * Construtor que inicializa uma nova temporada com o ano informado.
     *
     * @param ano o ano da temporada.
     */
    public Temporada(int ano) {
        this.ano = ano;
    }

    /**
     * Construtor padrão sem parâmetros.
     */
    public Temporada() {
    }

    /**
     * Cadastra uma nova equipe na temporada.
     *
     * @param equipe a equipe a ser adicionada.
     * @return o número total de equipes após a adição, ou 0 se a equipe for
     * nula.
     */
    public int cadastrarEquipe(Equipe equipe) {
        if (equipe != null) {
            equipes.add(equipe);
            return equipes.size();
        }
        return 0;
    }

    /**
     * Busca uma equipe pelo nome na lista de equipes da temporada.
     *
     * @param nomeEquipe o nome da equipe a ser buscada.
     * @return a equipe correspondente ao nome informado, ou {@code null} se não
     * encontrada.
     */
    public Equipe buscarEquipe(String nomeEquipe) {
        for (Equipe equipe : equipes) {
            if (equipe.getNome().equalsIgnoreCase(nomeEquipe)) {
                return equipe;
            }
        }
        return null;
    }

    /**
     * Determina a equipe líder da temporada com base no aproveitamento total e
     * de sets.
     *
     * @return a equipe com o melhor desempenho na temporada.
     */
    public Equipe liderTemporada() {
        Equipe lider = equipes.get(0);

        for (Equipe equipe : equipes) {
            if (equipe.aproveitamentoTotal() > lider.aproveitamentoTotal()) {
                lider = equipe;
            } else if (equipe.aproveitamentoTotal() == lider.aproveitamentoTotal()) {
                if (equipe.aproveitamentoSets() > lider.aproveitamentoSets()) {
                    lider = equipe;
                }
            }
        }

        return lider;
    }

    /**
     * Gera uma string representando a tabela da temporada, contendo o nome das
     * equipes e seus aproveitamentos.
     *
     * @return uma string formatada com a tabela da temporada.
     */
    public String tabela() {
        StringBuilder tabela = new StringBuilder();
        tabela.append("------------------- Tabela da Temporada de " + ano + " -------------------\n");
        tabela.append(String.format("%-20s %-20s %-20s%n", "Nome da Equipe", "Aproveitamento Total", "Aproveitamento Sets"));
        tabela.append("----------------------------------------------------------------------\n");

        for (Equipe equipe : equipes) {
            tabela.append(String.format("%-20s %-20.2f %-20.2f%n",
                    equipe.getNome(),
                    equipe.aproveitamentoTotal(),
                    equipe.aproveitamentoSets()
            ));
        }

        tabela.append("----------------------------------------------------------------------\n");
        return tabela.toString();
    }

}
