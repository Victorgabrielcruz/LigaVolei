package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.DTO.EquipeDTO;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.Equipe;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.PartidaDeVolei;

/**
 * Controlador para gerenciar operações relacionadas às equipes de vôlei.
 * Permite criar, buscar, e listar partidas e relatórios de equipes.
 */
@Controller
public class EquipeController {

    @PersistenceUnit
    private EntityManagerFactory factory;

    /**
     * Cria uma nova equipe e a persiste no banco de dados.
     *
     * @param equipe A equipe a ser criada.
     * @return A equipe criada.
     */
    @PostMapping("/equipes")
    public @ResponseBody
    Equipe criarEquipe(@RequestBody Equipe equipe) {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(equipe);
        manager.getTransaction().commit();
        return equipe;
    }

    /**
     * Cria várias equipes e as persiste no banco de dados.
     *
     * @param equipes Lista de equipes a serem criadas.
     * @return A lista de equipes criadas.
     */
    @PostMapping("/VariasEquipes")
    public @ResponseBody
    List<Equipe> criarEquipes(@RequestBody List<Equipe> equipes) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            for (Equipe equipe : equipes) {
                manager.persist(equipe);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar equipes: " + e.getMessage(), e);
        } finally {
            manager.close();
        }
        return equipes;
    }

    /**
     * Gera um relatório sobre uma equipe específica.
     *
     * @param id O ID da equipe.
     * @return O relatório da equipe ou "Não encontrada" se não existir.
     */
    @GetMapping("/equipes/relatorio/{id}")
    public @ResponseBody
    String relatorioEquipe(@PathVariable int id) {
        String retorno = "Não encontrada";
        EntityManager manager = factory.createEntityManager();
        Equipe equipe = manager.find(Equipe.class, id);
        if (equipe != null) {
            retorno = equipe.relatorio();
        }
        return retorno;
    }

    /**
     * Retorna todas as partidas de uma equipe.
     *
     * @param id O ID da equipe.
     * @return Lista das partidas da equipe.
     */
    @GetMapping("/equipes/partidas/{id}")
    public @ResponseBody
    List<PartidaDeVolei> partidasDaEquipe(@PathVariable int id) {
        EntityManager manager = factory.createEntityManager();
        TypedQuery<PartidaDeVolei> seletor = manager.createQuery("SELECT partidas FROM Equipe e WHERE e.idEquipe = :idEquipe",
                PartidaDeVolei.class);
        seletor.setParameter("idEquipe", id);
        return seletor.getResultList();
    }

    /**
     * Busca uma equipe e retorna seus dados como DTO.
     *
     * @param id O ID da equipe.
     * @return DTO contendo os dados da equipe ou null se não encontrada.
     */
    @GetMapping("/equipes/{id}")
    public @ResponseBody
    EquipeDTO buscarEquipe(@PathVariable int id) {
        EquipeDTO retorno = null;
        EntityManager manager = factory.createEntityManager();
        Equipe equipe = manager.find(Equipe.class, id);
        if (equipe != null) {
            retorno = equipe.getDTO();
        }
        return retorno;
    }

}
