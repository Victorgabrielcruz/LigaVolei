package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Classe "PartidaDeVolei". Versão 0.3 para Sistema do LPM Vôlei
 */
@Entity
@Table(name = "Partidas")
public class PartidaDeVolei {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private static final int MAX_SETS = 5;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Equipe> equipes;
    @ElementCollection
    private List<List<Integer>> pontuacoes;

    public PartidaDeVolei() {

    }

    /**
     * Cria uma partida de vôlei com duas equipes e placares zerados. As equipes
     * não devem ser nulas (não está sendo verificado e causará erros.)
     *
     * @param equipe1 Equipe 1 (mandante)
     * @param equipe2 Equipe 2 (visitante)
     */
    public PartidaDeVolei(Equipe equipe1, Equipe equipe2) {
        equipes = new ArrayList<>(2);
        equipes.add(equipe1);
        equipes.add(equipe2);
        pontuacoes = new ArrayList<>(2);
        pontuacoes.add(new ArrayList<>(MAX_SETS));
        pontuacoes.add(new ArrayList<>(MAX_SETS));
    }

    /**
     * Retorna uma string com placar formatado, contendo cabeçalho e duas
     * linhas. Em cada linha o nome da equipe, placar de cada set e quantidade
     * de sets vendidos pela equipe.
     *
     * @return String multilinhas      <pre>
     *  Formato:
     *            1   2   3   4   5  FINAL
     *  Equipe 1  25  25  25  --  -- 3
     *  Equipe 2  20  20  20  --  -- 0
     * </pre>
     */
    public String exibirPlacar() {
        StringBuilder placar = new StringBuilder(String.format("%16s\t1\t2\t3\t4\t5\tFINAL\n", " "));
        String setZerado = "--";
        int setsDisputados = setsDisputados();
        for (int i = 0; i < 2; i++) {
            placar.append(String.format("%16s\t", equipes.get(i).getNome()));
            for (int j = 0; j < setsDisputados; j++) {
                placar.append(pontuacoes.get(i).get(j) + "\t");
            }
            for (int j = setsDisputados; j < 5; j++) {
                placar.append(setZerado + "\t");
            }
            placar.append(setsVencidos(i) + "\n");
        }
        return placar.toString();
    }

    public String getPlacar() {
        return exibirPlacar();
    }

    /**
     * Faz a validação de placares válidos para um set: sets entre 1 e 4, pelo
     * menos 25 pontos e 2 pontos de diferença. Caso ultrapasse 25 pontos,
     * exatamente 2 pontos de diferença. Para o set 5, mesma regra com 15
     * pontos.
     *
     * @param set Número do set (1-5)
     * @param pontos1 Pontuação da equipe 1
     * @param pontos2 Pontuação da equipe 2
     * @return TRUE/FALSE conforme o placar é válido ou não para aquele set.
     */
    private boolean validarPlacarSet(int set, int pontos1, int pontos2) {
        boolean resposta = false;
        int pontosMinimos = set == 5 ? 15 : 25;
        int diferenca = pontos1 - pontos2;
        int pontosVencedor = diferenca > 0 ? pontos1 : pontos2;
        resposta = (pontosVencedor == pontosMinimos && Math.abs(diferenca) >= 2) || (pontosVencedor > pontosMinimos && Math.abs(diferenca) == 2);
        return resposta;
    }

    /**
     * Registra um placar para um set, caso seja válido. Além da validação da
     * pontuação, não é permitido "pular sets" (por exemplo, só registra o set 4
     * se já estiverem registrados os sets de 1 a 3). Também não deixa registrar
     * sets além da vitória (por exemplo, 4 sets se a mesma equipe já venceu os
     * 3 primeiros)
     *
     * @param set Número do set (1-5)
     * @param pontosEquipe1 Pontuação da equipe 1
     * @param pontosEquipe2 Pontuação da equipe 2
     * @return TRUE/FALSE conforme foi possível registrar ou não o placar.
     */
    public boolean registrarPlacarSet(int set, int pontosEquipe1, int pontosEquipe2) {
        boolean valoresValidos = false;

        if (set >= 1 && set <= setsDisputados() + 1 && vencedorDoJogo().equals("Jogo em andamento")) {
            valoresValidos = validarPlacarSet(set, pontosEquipe1, pontosEquipe2);
            if (valoresValidos) {
                pontuacoes.get(0).add(set - 1, pontosEquipe1);
                pontuacoes.get(1).add(set - 1, pontosEquipe2);
            }
        }
        return valoresValidos;
    }

    /**
     * Retorna o nome da equipe vencedora do jogo, ou "Jogo em andamento" caso
     * ninguém tenha vencido 3 sets ainda.
     *
     * @return Nome da equipe vencedora ou "Jogo em andamento"
     */
    public String vencedorDoJogo() {
        String resultado = "Jogo em andamento"; // Modifiquei aqui para ser mais claro
        int[] setsVencidos = {setsVencidos(0), setsVencidos(1)};
        int vencedor = -1;

        for (int i = 0; i < setsVencidos.length; i++) {
            if (setsVencidos[i] >= 3) {
                vencedor = i;
                break;
            }
        }

        if (vencedor != -1) {
            resultado = equipes.get(vencedor).getNome();
        }

        return resultado;
    }

    /**
     * Retorna os pontos totais marcados por uma equipe até o momento. Em caso
     * da equipe procurada não exista, retornará 0 pontos.
     *
     * @param equipe Nome da equipe
     * @return Pontuação da equipe ou 0, se a equipe não existir.
     */
    public int pontosTotaisEquipe(String equipe) {
        int total = 0;
        int time = equipe.equals(equipes.get(0).getNome()) ? 0
                : equipe.equals(equipes.get(1).getNome()) ? 1 : -1;

        if (time != -1) {
            for (int i = 0; i < setsDisputados(); i++) {
                total += pontuacoes.get(time).get(i);
            }
        }

        return total;
    }

    /**
     * Retorna sets vencidos por uma equipe até o momento. Em caso da equipe
     * procurada não exista, retornará 0 sets.
     *
     * @param equipe Nome da equipe
     * @return Sets vencidos pela equipe ou 0, se a equipe não existir.
     */
    public int setsVencidosEquipe(String equipe) {
        int vencidos = 0;
        int time = equipe.equals(equipes.get(0).getNome()) ? 0
                : equipe.equals(equipes.get(1).getNome()) ? 1 : -1;

        if (time != -1) {
            vencidos = setsVencidos(time);
        }
        return vencidos;
    }

    /**
     * Retorna o total de sets disputados na partida até agora (entre 0 e 5)
     *
     * @return Inteiro com o total de sets disputados até agora (0-5)
     */
    public int setsDisputados() {
        int totalSetsDisputados = 0;

        // Verifica se a lista principal e as sublistas não estão vazias
        if (pontuacoes == null || pontuacoes.size() < 2 || pontuacoes.get(0).size() != pontuacoes.get(1).size()) {
            return totalSetsDisputados; // Retorna 0 se a lista estiver malformada
        }

        List<Integer> pontuacoesEquipe1 = pontuacoes.get(0);
        List<Integer> pontuacoesEquipe2 = pontuacoes.get(1);

        for (int i = 0; i < pontuacoesEquipe1.size(); i++) {
            if (pontuacoesEquipe1.get(i) > 0 || pontuacoesEquipe2.get(i) > 0) {
                totalSetsDisputados++;
            }
        }

        return totalSetsDisputados;
    }

    /**
     * Verifica/retorna a quantidade de sets vencidos por um time, para
     * verificação da condição de vitória no jogo
     *
     * @param time Posição do time (0-mandante, 1-visitante)
     * @return Quantidade de sets vencidos pelo time (0-3)
     */
    private int setsVencidos(int time) {
        int totalSetsVencidos = 0;
        int adversario = (time == 0) ? 1 : 0;

        // Verifica se a lista principal e as sublistas não estão vazias
        if (pontuacoes == null || pontuacoes.size() < 2 || pontuacoes.get(time).size() != pontuacoes.get(adversario).size()) {
            return totalSetsVencidos; // Retorna 0 se a lista estiver malformada
        }

        // Percorre os sets e calcula o número de sets vencidos
        for (int j = 0; j < pontuacoes.get(time).size(); j++) {
            int pontosTime = pontuacoes.get(time).get(j);
            int pontosAdversario = pontuacoes.get(adversario).get(j);

            // Verifica se o time venceu o set
            if (pontosTime > pontosAdversario) {
                // Condição para o último set (set 5) e outros sets
                if ((j == 4 && pontosTime >= 15) || (j < 4 && pontosTime >= 25 && Math.abs(pontosTime - pontosAdversario) >= 2)) {
                    totalSetsVencidos++;
                }
            }
        }

        return totalSetsVencidos;
    }

}
