/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DAO;

import Controle.Tarefa;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Controle.Usuario;
import DAO.exceptions.NonexistentEntityException;
import DAO.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Romulo
 */
public class TarefaJpaController implements Serializable {

    public TarefaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tarefa tarefa) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = tarefa.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                tarefa.setIdUsuario(idUsuario);
            }
            em.persist(tarefa);
            if (idUsuario != null) {
                idUsuario.getTarefaCollection().add(tarefa);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTarefa(tarefa.getId()) != null) {
                throw new PreexistingEntityException("Tarefa " + tarefa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tarefa tarefa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tarefa persistentTarefa = em.find(Tarefa.class, tarefa.getId());
            Usuario idUsuarioOld = persistentTarefa.getIdUsuario();
            Usuario idUsuarioNew = tarefa.getIdUsuario();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                tarefa.setIdUsuario(idUsuarioNew);
            }
            tarefa = em.merge(tarefa);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getTarefaCollection().remove(tarefa);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getTarefaCollection().add(tarefa);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = tarefa.getId();
                if (findTarefa(id) == null) {
                    throw new NonexistentEntityException("The tarefa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tarefa tarefa;
            try {
                tarefa = em.getReference(Tarefa.class, id);
                tarefa.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tarefa with id " + id + " no longer exists.", enfe);
            }
            Usuario idUsuario = tarefa.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getTarefaCollection().remove(tarefa);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(tarefa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tarefa> findTarefaEntities() {
        return findTarefaEntities(true, -1, -1);
    }

    public List<Tarefa> findTarefaEntities(int maxResults, int firstResult) {
        return findTarefaEntities(false, maxResults, firstResult);
    }

    private List<Tarefa> findTarefaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tarefa.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Tarefa findTarefa(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tarefa.class, id);
        } finally {
            em.close();
        }
    }

    public int getTarefaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tarefa> rt = cq.from(Tarefa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
