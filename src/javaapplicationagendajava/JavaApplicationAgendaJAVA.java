/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplicationagendajava;

import Controle.Tarefa;
import Controle.Usuario;
import java.sql.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Romulo
 */
public class JavaApplicationAgendaJAVA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Usuario usuario = new Usuario(); // Cria o usuário
        usuario.setNome("Admin"); // Configura o nome (não pode ser duplicado)
        usuario.setSenha("abcde"); // Configura uma senha
        if (usuario.armazenado()) { // Se armazenado o usuário no BD
 
            Tarefa tarefa1 = new Tarefa(); // Criar uma tarefa1
            tarefa1.setData(Date.valueOf("2014-01-01")); // Configura Data
            tarefa1.setDescricao("Ligar para o trabalho"); // Configura descrição da tarefa1
            tarefa1.setIdUsuario(usuario); // Configura o usuário para o usuário PauloVL
 
            Tarefa tarefa2 = new Tarefa();
            tarefa2.setData(Date.valueOf("2014-01-01"));
            tarefa2.setDescricao("Pagar Conta de luz");
            tarefa2.setIdUsuario(usuario);
 
 
            if (!tarefa1.armazenado() || !tarefa2.armazenado()) { // Se não gravada tarefa1 ou tarefa2
                JOptionPane.showMessageDialog(null, "Falha em gravação de tarefa");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Falha em gravação de usuário");
        }
    }
    
}
