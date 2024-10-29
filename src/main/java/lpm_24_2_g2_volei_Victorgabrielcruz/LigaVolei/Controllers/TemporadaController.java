package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.DTO.TemporadaDTO;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.Equipe;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.Temporada;

/**
 * Controlador para gerenciar temporadas de vôlei. Permite criar temporadas,
 * buscar equipes e cadastrar novas equipes.
 */
@Controller
@RequestMapping("/temporada")
public class TemporadaController {

    @PersistenceUnit
    private EntityManagerFactory factory;

    /**
     * Cria uma nova temporada com base em um DTO.
     *
     * @param temporadaDTO DTO contendo as informações da temporada.
     * @return A temporada criada.
     */
    @PostMapping
    public @ResponseBody
    Temporada criarTemporada(@RequestBody TemporadaDTO temporadaDTO) {
        EntityManager manager = factory.createEntityManager();
        Temporada novaTemporada = new Temporada(temporadaDTO.getAno());

        manager.getTransaction().begin();
        manager.persist(novaTemporada);
        manager.getTransaction().commit();

        return novaTemporada;
    }

    /**
     * Busca uma equipe em uma temporada específica.
     *
     * @param id O ID da temporada.
     * @param nomeEquipe Nome da equipe a ser buscada.
     * @return A equipe encontrada ou null.
     */
    @GetMapping("/buscarEquipe/{id}")
    public @ResponseBody
    Equipe MostrEquipe(@PathVariable int id, @RequestBody String nomeEquipe) {
        EntityManager em = factory.createEntityManager();

        Temporada temporada = em.find(Temporada.class, id);
        Equipe equipe = temporada.buscarEquipe(nomeEquipe);
        return equipe;
    }

    /**
     * Retorna a equipe líder da temporada.
     *
     * @param id O ID da temporada.
     * @return A equipe líder.
     */
    @GetMapping("/LiderTemporada/{id}")
    public @ResponseBody
    Equipe LiderTemporada(@PathVariable int id) {
        EntityManager em = factory.createEntityManager();

        Temporada temporada = em.find(Temporada.class, id);
        Equipe equipe = temporada.liderTemporada();
        return equipe;
    }

    /**
     * Exibe a tabela de classificação da temporada.
     *
     * @param id O ID da temporada.
     * @return A tabela da temporada.
     */
    @GetMapping("/{id}")
    public @ResponseBody
    String Tabela(@PathVariable int id) {
        EntityManager em = factory.createEntityManager();
        Temporada temporada = em.find(Temporada.class, id);
        return temporada.tabela();
    }

    /**
     * Cadastra uma equipe em uma temporada.
     *
     * @param temporadaId ID da temporada.
     * @param equipeId ID da equipe.
     * @return Mensagem de sucesso ou erro.
     */
    @PutMapping("/{temporadaId}/equipes/{equipeId}")
    public @ResponseBody
    String cadastrarEquipe(@PathVariable int temporadaId, @PathVariable int equipeId) {
        EntityManager em = factory.createEntityManager();

        Temporada temporada = em.find(Temporada.class, temporadaId);
        if (temporada == null) {
            return "Temporada não encontrada!";
        }

        Equipe equipe = em.find(Equipe.class, equipeId);
        if (equipe == null) {
            return "Equipe não encontrada!";
        }

        em.getTransaction().begin();
        int numeroEquipes = temporada.cadastrarEquipe(equipe);
        em.persist(temporada);
        em.getTransaction().commit();

        return "Equipe cadastrada com sucesso! Total de equipes na temporada: " + numeroEquipes;
    }

    /**
     * Cadastra várias equipes em uma temporada.
     *
     * @param temporadaId ID da temporada.
     * @param equipeIds IDs das equipes.
     * @return Mensagem de sucesso ou erro.
     */
    @PutMapping("/{temporadaId}/equipes")
    public @ResponseBody
    String cadastrarEquipes(@PathVariable int temporadaId, @RequestBody List<Integer> equipeIds) {
        EntityManager em = factory.createEntityManager();

        Temporada temporada = em.find(Temporada.class, temporadaId);
        if (temporada == null) {
            return "Temporada não encontrada!";
        }

        int equipesCadastradas = 0;
        em.getTransaction().begin();

        for (Integer equipeId : equipeIds) {
            Equipe equipe = em.find(Equipe.class, equipeId);
            if (equipe != null) {
                temporada.cadastrarEquipe(equipe);
                equipesCadastradas++;
            } else {
                em.getTransaction().rollback();
                return "Erro: Equipe com ID " + equipeId + " não encontrada.";
            }
        }

        em.persist(temporada);
        em.getTransaction().commit();

        return equipesCadastradas + " equipes cadastradas com sucesso na temporada.";
    }
}
