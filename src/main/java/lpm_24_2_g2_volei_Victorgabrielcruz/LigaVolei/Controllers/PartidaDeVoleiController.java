package lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.Equipe;
import lpm_24_2_g2_volei_Victorgabrielcruz.LigaVolei.Models.PartidaDeVolei;

/**
 * Controlador para gerenciar partidas de vôlei. Permite criar, registrar sets,
 * e buscar informações sobre partidas.
 */
@Controller
public class PartidaDeVoleiController {

    @PersistenceUnit
    private EntityManagerFactory factory;

    /**
     * Cria uma nova partida entre duas equipes.
     *
     * @param id1 ID da primeira equipe.
     * @param id2 ID da segunda equipe.
     * @return A partida criada.
     */
    @PostMapping("/partidas/{id1}/{id2}")
    public @ResponseBody
    PartidaDeVolei criarPartida(@PathVariable int id1, @PathVariable int id2) {
        EntityManager em = factory.createEntityManager();
        Equipe time1 = em.find(Equipe.class, id1);
        Equipe time2 = em.find(Equipe.class, id2);
        PartidaDeVolei partida = null;
        if (time1 != null && time2 != null) {
            partida = new PartidaDeVolei(time1, time2);
            time1.registrarPartida(partida);
            time2.registrarPartida(partida);
            em.getTransaction().begin();
            em.persist(partida);
            em.persist(time1);
            em.persist(time2);
            em.getTransaction().commit();
        }
        return partida;

    }

    /**
     * Cria várias partidas com base em um JSON.
     *
     * @param partidasJson JSON contendo as informações das partidas.
     * @return Lista das partidas criadas.
     */
    @PostMapping("/partidas")
    public @ResponseBody
    List<PartidaDeVolei> criarPartidas(@RequestBody Map<String, List<Map<String, Integer>>> partidasJson) {
        EntityManager em = factory.createEntityManager();
        List<PartidaDeVolei> partidasCriadas = new ArrayList<>();

        List<Map<String, Integer>> partidas = partidasJson.get("partidas");

        em.getTransaction().begin();
        for (Map<String, Integer> partidaInfo : partidas) {
            int id1 = partidaInfo.get("id1");
            int id2 = partidaInfo.get("id2");

            Equipe time1 = em.find(Equipe.class, id1);
            Equipe time2 = em.find(Equipe.class, id2);

            if (time1 != null && time2 != null) {
                PartidaDeVolei partida = new PartidaDeVolei(time1, time2);
                time1.registrarPartida(partida);
                time2.registrarPartida(partida);
                em.persist(partida);
                em.persist(time1);
                em.persist(time2);
                partidasCriadas.add(partida);
            }
        }
        em.getTransaction().commit();
        return partidasCriadas;
    }

    /**
     * Registra um set para uma partida específica.
     *
     * @param id ID da partida.
     * @param set Número do set.
     * @param placar1 Placar da primeira equipe.
     * @param placar2 Placar da segunda equipe.
     * @return A partida com o set registrado.
     */
    @PutMapping("/partidas/sets/{id}/{set}/{placar1}/{placar2}")
    public @ResponseBody
    PartidaDeVolei cadastrarSet(@PathVariable int id, @PathVariable int set, @PathVariable int placar1, @PathVariable int placar2) {
        EntityManager em = factory.createEntityManager();

        PartidaDeVolei partida = em.find(PartidaDeVolei.class, id);
        partida.registrarPlacarSet(set, placar1, placar2);
        em.getTransaction().begin();
        em.persist(partida);
        em.getTransaction().commit();
        return partida;
    }

    /**
     * Registra vários sets para partidas a partir de um JSON.
     *
     * @param setsJson JSON contendo os sets.
     * @return Lista das partidas atualizadas.
     */
    @PutMapping("/partidas/sets")
    public @ResponseBody
    List<PartidaDeVolei> cadastrarSets(@RequestBody Map<String, List<Map<String, Integer>>> setsJson) {
        EntityManager em = factory.createEntityManager();
        List<PartidaDeVolei> partidasAtualizadas = new ArrayList<>();

        List<Map<String, Integer>> sets = setsJson.get("sets");

        em.getTransaction().begin();
        for (Map<String, Integer> setInfo : sets) {
            int id = setInfo.get("id");
            int set = setInfo.get("set");
            int placar1 = setInfo.get("placar1");
            int placar2 = setInfo.get("placar2");

            PartidaDeVolei partida = em.find(PartidaDeVolei.class, id);
            if (partida != null) {
                partida.registrarPlacarSet(set, placar1, placar2);
                em.persist(partida);
                partidasAtualizadas.add(partida);
            }
        }
        em.getTransaction().commit();
        return partidasAtualizadas;
    }

    /**
     * Retorna o vencedor de uma partida.
     *
     * @param id O ID da partida.
     * @return Nome do vencedor.
     */
    @GetMapping("/partidas/vencedor/{id}")
    public @ResponseBody
    String vencedorPartida(@PathVariable int id) {
        EntityManager em = factory.createEntityManager();

        PartidaDeVolei partida = em.find(PartidaDeVolei.class, id);
        return partida.vencedorDoJogo();
    }

    /**
     * Exibe o placar de uma partida.
     *
     * @param id O ID da partida.
     * @return O placar da partida.
     */
    @GetMapping("/partidas/placar/{id}")
    public @ResponseBody
    String placarPartida(@PathVariable int id) {
        EntityManager em = factory.createEntityManager();

        PartidaDeVolei partida = em.find(PartidaDeVolei.class, id);
        return partida.exibirPlacar();
    }
}
