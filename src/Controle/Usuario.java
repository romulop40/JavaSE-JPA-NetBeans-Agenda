/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controle;

import DAO.UsuarioJpaController;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Romulo
 */
@Entity
@Table(name = "usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"Nome"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id = :id"),
    @NamedQuery(name = "Usuario.findByNome", query = "SELECT u FROM Usuario u WHERE u.nome = :nome"),
    @NamedQuery(name = "Usuario.findBySenha", query = "SELECT u FROM Usuario u WHERE u.senha = :senha")})
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @Column(name = "Nome", nullable = false, length = 10)
    private String nome;
    @Basic(optional = false)
    @Column(name = "Senha", nullable = false, length = 6)
    private String senha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private Collection<Tarefa> tarefaCollection;

    public Usuario() {
    }

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario(Long id, String nome, String senha) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @XmlTransient
    public Collection<Tarefa> getTarefaCollection() {
        return tarefaCollection;
    }

    public void setTarefaCollection(Collection<Tarefa> tarefaCollection) {
        this.tarefaCollection = tarefaCollection;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Controle.Usuario[ id=" + id + " ]";
    }
    public boolean armazenado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            UsuarioJpaController usuarioJpaController = new UsuarioJpaController(emf);
            usuarioJpaController.create(this);
            JOptionPane.showMessageDialog(null, "Usuário incluído");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean atualizado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            UsuarioJpaController usuarioJpaController = new UsuarioJpaController(emf);
            usuarioJpaController.edit(this);
            JOptionPane.showMessageDialog(null, "Usuário atualizado");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean desarmazenado() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            UsuarioJpaController usuarioJpaController = new UsuarioJpaController(emf);
            usuarioJpaController.destroy(this.getId());
            JOptionPane.showMessageDialog(null, "Usuário excluído");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }
 
    public boolean encontradoId(Long id) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            UsuarioJpaController usuarioJpaController = new UsuarioJpaController(emf);
            Usuario usuarioAux = usuarioJpaController.findUsuario(id);
            if (usuarioAux != null) {
                this.setId(usuarioAux.getId());
                this.setNome(usuarioAux.getNome());
                this.setSenha(usuarioAux.getSenha());
                this.setTarefaCollection(usuarioAux.getTarefaCollection());
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Usuário não encontrado");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause());
            return false;
        }
    }    
    
    public Usuario encontradoNome(String nome) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplicationAgendaJAVAPU");
            UsuarioJpaController usuarioJpaController = new UsuarioJpaController(emf);
            Usuario usuarioAux = (Usuario) usuarioJpaController.findNome(nome);
            if (usuarioAux != null) {
                this.setId(usuarioAux.getId());
                this.setNome(usuarioAux.getNome());
                this.setSenha(usuarioAux.getSenha());
                this.setTarefaCollection(usuarioAux.getTarefaCollection());
                return usuarioAux;
            } else {
                JOptionPane.showMessageDialog(null, "Usuário não encontrado");
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
