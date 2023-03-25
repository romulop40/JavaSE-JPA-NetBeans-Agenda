/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controle;

import DAO.TarefaJpaController;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Romulo
 */
@Entity
@Table(name = "tarefa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tarefa.findAll", query = "SELECT t FROM Tarefa t"),
    @NamedQuery(name = "Tarefa.findById", query = "SELECT t FROM Tarefa t WHERE t.id = :id"),
    @NamedQuery(name = "Tarefa.findByData", query = "SELECT t FROM Tarefa t WHERE t.data = :data"),
    @NamedQuery(name = "Tarefa.findByDescricao", query = "SELECT t FROM Tarefa t WHERE t.descricao = :descricao")})
public class Tarefa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @Column(name = "Data", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date data;
    @Basic(optional = false)
    @Column(name = "Descricao", nullable = false, length = 30)
    private String descricao;
    @JoinColumn(name = "id_Usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public Tarefa() {
    }

    public Tarefa(Long id) {
        this.id = id;
    }

    public Tarefa(Long id, Date data, String descricao) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tarefa)) {
            return false;
        }
        Tarefa other = (Tarefa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Controle.Tarefa[ id=" + id + " ]";
    }
    public boolean armazenado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            TarefaJpaController tarefaJpaController = new TarefaJpaController(emf);
            tarefaJpaController.create(this);
            JOptionPane.showMessageDialog(null, "Tarefa incluida");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean atualizado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            TarefaJpaController tarefaJpaController = new TarefaJpaController(emf);
            tarefaJpaController.edit(this);
            JOptionPane.showMessageDialog(null, "Tarefa atualizado");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean desarmazenado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            TarefaJpaController tarefaJpaController = new TarefaJpaController(emf);
            tarefaJpaController.destroy(this.getId());
            JOptionPane.showMessageDialog(null, "Tarefa excluida");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean encontradoId(Long id) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            TarefaJpaController tarefaJpaController = new TarefaJpaController(emf);
            Tarefa tarefaAux = tarefaJpaController.findTarefa(id);
            if (tarefaAux != null) {
                this.setId(tarefaAux.getId());
                this.setDescricao(tarefaAux.getDescricao());
                this.setData(tarefaAux.getData());
                this.setIdUsuario(tarefaAux.getIdUsuario());
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Tarefa não encontrada");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
    
}
