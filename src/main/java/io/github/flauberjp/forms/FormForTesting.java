package io.github.flauberjp.forms;

import io.github.flauberjp.EvidenceGenerator;
import io.github.flauberjp.UserGithubInfo;
import io.github.flauberjp.UserGithubProjectCreator;
import io.github.flauberjp.Util;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import lombok.SneakyThrows;
import org.kohsuke.github.GHCompare.User;

public class FormForTesting extends JFrame {

  private JPanel contentPane;
  private JTextField txtUsername;
  private JPasswordField passwordField;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          FormForTesting frame = new FormForTesting();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public FormForTesting() {
    geraPainelPrincipal();

    botaoValidacao();

    botaoSalvarDados();

    botaoLerDados();

    botaoCriarProjetoRemoto();

    botaoGerarEvidencia();

    botaoSemFuncaoAinda();

  }

  private void geraPainelPrincipal() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 525, 397);
    contentPane = new JPanel();
    contentPane.setToolTipText("");
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    JLabel lblUsername = new JLabel("Nome de Usu\u00E1rio");
    lblUsername.setBounds(35, 111, 111, 14);
    contentPane.add(lblUsername);

    txtUsername = new JTextField();
    txtUsername.setText("mygitusageevicencesapp");
    txtUsername.setColumns(10);
    txtUsername.setBounds(156, 108, 293, 20);
    contentPane.add(txtUsername);

    passwordField = new JPasswordField();
    passwordField.setToolTipText("e.g. passw0rd");
    passwordField.setText("44dbb46ec17d03c3545a4301370565c45e870ce3");
    passwordField.setBounds(156, 153, 293, 20);
    contentPane.add(passwordField);

    JLabel lblPassword = new JLabel("Password");
    lblPassword.setBounds(35, 156, 111, 14);
    contentPane.add(lblPassword);

    JLabel lblTitle = new JLabel("Dados do seu Github");
    lblTitle.setBounds(192, 23, 203, 14);
    contentPane.add(lblTitle);

    JLabel lblTeste = new JLabel("Área de testes");
    lblTeste.setBounds(220, 5, 203, 14);
    lblTeste.setForeground(Color.red);
    contentPane.add(lblTeste);
  }

  private void botaoGerarEvidencia() {
    JButton btnGenerateEvidence = new JButton("Gerar evidência");
    btnGenerateEvidence.addActionListener(new ActionListener() {
      @SneakyThrows
      public void actionPerformed(ActionEvent e) {
        try {
          if(EvidenceGenerator.geraEvidenciaDeUsoDoGit(UserGithubInfo.get())) {
            JOptionPane.showMessageDialog(contentPane, "Evidência gerada.");
          } else {
            JOptionPane.showMessageDialog(contentPane, "Problemas na geração de evidências.");
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(contentPane, "Problemas na geração de evidências. Exception: " + ex.getMessage());
        }
      }
    });
    btnGenerateEvidence.setBounds(156, 189, 300, 23);
    contentPane.add(btnGenerateEvidence);
  }

  private void botaoCriarProjetoRemoto() {
    JButton btnCreateProject = new JButton("Criar projeto no repo remoto");
    btnCreateProject.addActionListener(new ActionListener() {
      @SneakyThrows
      public void actionPerformed(ActionEvent e) {
        try {
          UserGithubProjectCreator.criaProjetoInicialNoGithub(UserGithubInfo.get());
          JOptionPane.showMessageDialog(contentPane, "Projeto criado.");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(contentPane, "Problemas na criação do projeto. Exception: " + ex.getMessage());
        }

      }
    });
    btnCreateProject.setBounds(156, 216, 300, 23);
    contentPane.add(btnCreateProject);
  }

  private void botaoLerDados() {
    JButton btnLerArq = new JButton("Ler conteúdo do arquivo " + UserGithubInfo.PROPERTIES_FILE);
    btnLerArq.addActionListener(new ActionListener() {
      @SneakyThrows
      public void actionPerformed(ActionEvent e) {
        if(!new File(UserGithubInfo.PROPERTIES_FILE).exists()) {
          JOptionPane.showMessageDialog(contentPane, "Arquivo inexistente, valide as suas credenciais primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
          return;
        }
        UserGithubInfo userGithubInfo = UserGithubInfo.get(Util.ReadPropertiesFromFile(UserGithubInfo.PROPERTIES_FILE));
        String output =
            "\tlogin=" + userGithubInfo.getUsername() + "\n" +
            "\tpassword=" + userGithubInfo.getPassword() + "\n" +
            "\tgithubName=" + userGithubInfo.getGithubName()+ "\n" +
            "\tgithubRepoNameFullPath=" + userGithubInfo.getRepoNameFullPath() + "\n" +
            "\tgithubEmail=" + userGithubInfo.getGithubEmail() + "\n" +
            "\trepoName=" + userGithubInfo.getRepoName();
        JOptionPane.showMessageDialog(contentPane, "Credenciais válidas!\n\n" + output);
      }
    });
    btnLerArq.setBounds(156, 243, 300, 23);
    contentPane.add(btnLerArq);
  }

  private void botaoSalvarDados() {
    JButton btnConfirm = new JButton("Salvar Dados em " + UserGithubInfo.PROPERTIES_FILE);
    btnConfirm.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          UserGithubInfo.reset();
          Util.SavePropertiesToFile(UserGithubInfo.get(txtUsername.getText(), String.valueOf(passwordField.getPassword())).toProperties(), UserGithubInfo.PROPERTIES_FILE);
          JOptionPane.showMessageDialog(contentPane, "Dados salvos!");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(contentPane, "Problemas ao tentar salvar dados. Exception: " + ex.getMessage());
        }
      }
    });
    btnConfirm.setBounds(156, 270, 300, 23);
    contentPane.add(btnConfirm);
  }

  private void botaoValidacao() {
    JButton btnValidacao = new JButton("Validar Credenciais");
    btnValidacao.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UserGithubInfo.reset();
        if(UserGithubInfo.validarCredenciais(txtUsername.getText(), String.valueOf(passwordField.getPassword()))) {
          try {
            UserGithubInfo userGithubInfo = UserGithubInfo.get();
            String output =
                "\tlogin=" + userGithubInfo.getUsername() + "\n" +
                    "\tpassword=" + userGithubInfo.getPassword() + "\n" +
                    "\tgithubName=" + userGithubInfo.getGithubName()+ "\n" +
                    "\tgithubRepoNameFullPath=" + userGithubInfo.getRepoNameFullPath() + "\n" +
                    "\tgithubEmail=" + userGithubInfo.getGithubEmail() + "\n" +
                    "\trepoName=" + userGithubInfo.getRepoName();
            JOptionPane.showMessageDialog(contentPane, "Credenciais válidas!\n\n" + output);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(contentPane, "Credenciais válidas, mas houve problemas ao ler propriedades. Exception: " + ex.getMessage());
          }
        } else {
          JOptionPane.showMessageDialog(contentPane, "Credenciais inválidas");
        }
      }
    });
    btnValidacao.setBounds(156, 297, 300, 23);
    contentPane.add(btnValidacao);
  }

  private void botaoSemFuncaoAinda() {
    JButton btn = new JButton("Sem função atribuída ainda");
    btn.addActionListener(new ActionListener() {
      @SneakyThrows
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(contentPane, "Função não implementada ainda");
      }
    });
    btn.setBounds(156, 324, 300, 23);
    contentPane.add(btn);
  }
}