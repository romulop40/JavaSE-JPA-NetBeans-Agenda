/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DAO;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Controle.Tarefa;
import Controle.Usuario;
import DAO.exceptions.IllegalOrphanException;
import DAO.exceptions.NonexistentEntityException;
import DAO.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Romulo
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getTarefaCollection() == null) {
            usuario.setTarefaCollection(new ArrayList<Tarefa>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Tarefa> attachedTarefaCollection = new ArrayList<Tarefa>();
            for (Tarefa tarefaCollectionTarefaToAttach : usuario.getTarefaCollection()) {
                tarefaCollectionTarefaToAttach = em.getReference(tarefaCollectionTarefaToAttach.getClass(), tarefaCollectionTarefaToAttach.getId());
                attachedTarefaCollection.add(tarefaCollectionTarefaToAttach);
            }
            usuario.setTarefaCollection(attachedTarefaCollection);
            em.persist(usuario);
            for (Tarefa tarefaCollectionTarefa : usuario.getTarefaCollection()) {
                Usuario oldIdUsuarioOfTarefaCollectionTarefa = tarefaCollectionTarefa.getIdUsuario();
                tarefaCollectionTarefa.setIdUsuario(usuario);
                tarefaCollectionTarefa = em.merge(tarefaCollectionTarefa);
                if (oldIdUsuarioOfTarefaCollectionTarefa != null) {
                    oldIdUsuarioOfTarefaCollectionTarefa.getTarefaCollection().remove(tarefaCollectionTarefa);
                    oldIdUsuarioOfTarefaCollectionTarefa = em.merge(oldIdUsuarioOfTarefaCollectionTarefa);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getId()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getId());
            Collection<Tarefa> tarefaCollectionOld = persistentUsuario.getTarefaCollection();
            Collection<Tarefa> tarefaCollectionNew = usuario.getTarefaCollection();
            List<String> illegalOrphanMessages = null;
            for (Tarefa tarefaCollectionOldTarefa : tarefaCollectionOld) {
                if (!tarefaCollectionNew.contains(tarefaCollectionOldTarefa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tarefa " + tarefaCollectionOldTarefa + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Tarefa> attachedTarefaCollectionNew = new ArrayList<Tarefa>();
            for (Tarefa tarefaCollectionNewTarefaToAttach : tarefaCollectionNew) {
                tarefaCollectionNewTarefaToAttach = em.getReference(tarefaCollectionNewTarefaToAttach.getClass(), tarefaCollectionNewTarefaToAttach.getId());
                attachedTarefaCollectionNew.add(tarefaCollectionNewTarefaToAttach);
            }
            tarefaCollectionNew = attachedTarefaCollectionNew;
            usuario.setTarefaCollection(tarefaCollectionNew);
            usuario = em.merge(usuario);
            for (Tarefa tarefaCollectionNewTarefa : tarefaCollectionNew) {
                if (!tarefaCollectionOld.contains(tarefaCollectionNewTarefa)) {
                    Usuario oldIdUsuarioOfTarefaCollectionNewTarefa = tarefaCollectionNewTarefa.getIdUsuario();
                    tarefaCollectionNewTarefa.setIdUsuario(usuario);
                    tarefaCollectionNewTarefa = em.merge(tarefaCollectionNewTarefa);
                    if (oldIdUsuarioOfTarefaCollectionNewTarefa != null && !oldIdUsuarioOfTarefaCollectionNewTarefa.equals(usuario)) {
                        oldIdUsuarioOfTarefaCollectionNewTarefa.getTarefaCollection().remove(tarefaCollectionNewTarefa);
                        oldIdUsuarioOfTarefaCollectionNewTarefa = em.merge(oldIdUsuarioOfTarefaCollectionNewTarefa);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = usuario.getId();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Tarefa> tarefaCollectionOrphanCheck = usuario.getTarefaCollection();
            for (Tarefa tarefaCollectionOrphanCheckTarefa : tarefaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Tarefa " + tarefaCollectionOrphanCheckTarefa + " in its tarefaCollection field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public Usuario findNome(String nome) {
        EntityManager em = getEntityManager();
        try {
             return em.createNamedQuery("Usuario.findByNome",Usuario.class).setParameter("nome", nome).getSingleResult();
        }catch(Exception e){
            return null;
        } finally {
            em.close();
        }
    }
}
