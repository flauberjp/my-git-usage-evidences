package io.github.flauberjp.forms;

import io.github.flauberjp.GenerateHook;
import io.github.flauberjp.UserGithubInfo;
import io.github.flauberjp.UserGithubProjectCreator;
import io.github.flauberjp.util.Util;
import io.github.flauberjp.Version;
import io.github.flauberjp.forms.model.GitDir;
import io.github.flauberjp.forms.model.GitDirListRenderer;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.Map;
import static io.github.flauberjp.util.MyLogger.logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.SneakyThrows;
import static io.github.flauberjp.util.MyLogger.logger;

public class FormMain extends JFrame {

  private JPanel contentPane;
  private JTextField txtUsername;
  private JPasswordField passwordField;
  private JTextField txtReponame;

  /**
   * Create the frame.
   */
  public FormMain() {
    logger.debug("FormMain.FormMain()");
    geraPainelPrincipal();

    botaoConfigurar();

    adicionarLblProgramName();

    selecionadorDeProjetosGit();
  }

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    logger.info("FormMain.main(args = {})", args);
    showFormMain();
  }

  public static void showFormMain() {
    logger.debug("FormMain.showFormMain()");
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          FormMain frame = new FormMain();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }


  private void geraPainelPrincipal() {
    logger.debug("FormMain.geraPainelPrincipal()");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 525, 540);
    contentPane = new JPanel();
    contentPane.setToolTipText("");
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    JLabel lblTitle = new JLabel("Credenciais do Github");
    setLabelUnderline(lblTitle);
    lblTitle.setBounds(35, 70, 203, 14);
    contentPane.add(lblTitle);

    JLabel lblUsername = new JLabel("Nome de Usu\u00E1rio:");
    lblUsername.setBounds(35, 99, 111, 14);
    contentPane.add(lblUsername);

    txtUsername = new JTextField();
    txtUsername.setText("mygitusageevicencesapp");
    txtUsername.setColumns(10);
    txtUsername.setBounds(160, 97, 325, 20);
    contentPane.add(txtUsername);

    JLabel lblPassword = new JLabel("Password:");
    lblPassword.setBounds(35, 124, 111, 14);
    contentPane.add(lblPassword);

    passwordField = new JPasswordField();
    passwordField.setToolTipText("e.g. passw0rd");
    passwordField.setText("44dbb46ec17d03c3545a4301370565c45e870ce3");
    passwordField.setBounds(160, 122, 325, 20);
    contentPane.add(passwordField);

    JLabel lblRepoNameArea = new JLabel(
        "Repositório no seu Github que irá registrar o seu uso local do git");
    setLabelUnderline(lblRepoNameArea);
    lblRepoNameArea.setBounds(35, 167, 450, 14);
    contentPane.add(lblRepoNameArea);

    JLabel lblRepoName = new JLabel("Nome do Repositório:");
    lblRepoName.setBounds(35, 194, 123, 14);
    contentPane.add(lblRepoName);

    txtReponame = new JTextField();
    txtReponame.setText("my-git-usage-evidences");
    txtReponame.setColumns(10);
    txtReponame.setBounds(160, 192, 325, 20);
    contentPane.add(txtReponame);

    JLabel lblProjects = new JLabel(
        "Projetos que não são do Github que terão o uso local do git registrado no Github");
    setLabelUnderline(lblProjects);
    lblProjects.setBounds(35, 225, 450, 14);
    contentPane.add(lblProjects);
  }

  private void setLabelUnderline(JLabel label) {
    logger.debug("FormMain.setLabelUnderline(label = {})", label);
    Font font = label.getFont();
    Map attributes = font.getAttributes();
    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    label.setFont(font.deriveFont(attributes));
  }

  private void botaoConfigurar() {
    logger.debug("FormMain.botaoConfigurar()");
    JButton btn = new JButton("Aplicar configurações");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          logger.info("Botão \"Aplicar configurações\" pressionando");
          logger.debug("Lista de projetos git selecionados: " + Util.getSelectedGitDirStringList().toString());
          UserGithubInfo userGithubInfo = UserGithubInfo.get(txtUsername.getText(),
              String.valueOf(passwordField.getPassword()));
          userGithubInfo.setRepoName(txtReponame.getText());
          Util.savePropertiesToFile(userGithubInfo.toProperties(), UserGithubInfo.PROPERTIES_FILE);
          UserGithubProjectCreator.criaProjetoInicialNoGithub(userGithubInfo);
          GenerateHook.generateHook(Util.getSelectedGitDirStringList());
          JOptionPane.showMessageDialog(contentPane, "Configurações aplicadas!");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(contentPane,
              "Problemas ao aplicar configurações. Exception: " + ex.getMessage());
        }
      }
    });
    btn.setBounds(188, 440, 170, 23);
    contentPane.add(btn);

  }

  private void adicionarLblProgramName() {
    logger.debug("FormMain.adicionarLblProgramName()");
    JLabel lblProgramName = new JLabel("my-git-usage-evidences Program");
    lblProgramName.setToolTipText(Version.getVersionFromPom());
    lblProgramName.setFont(new Font("Tahoma", Font.BOLD, 16));
    lblProgramName.setHorizontalAlignment(SwingConstants.CENTER);
    lblProgramName.setBounds(35, 11, 450, 23);
    contentPane.add(lblProgramName);
  }

  private void selecionadorDeProjetosGit() {
    logger.debug("FormMain.selecionadorDeProjetosGit()");
    String label = "Selecione a Pasta Pai dos Projetos Github ";
    JLabel lblPastaPai = new JLabel(label);
    lblPastaPai.setBounds(35, 250, 323, 14);
    contentPane.add(lblPastaPai);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(35, 278, 450, 151);
    contentPane.add(scrollPane);

    JList<GitDir> list = new JList<GitDir>();
    scrollPane.setViewportView(list);

    JButton btnSelect = new JButton("Selecionar");
    btnSelect.setToolTipText(label);
    btnSelect.addActionListener(new ActionListener() {
      @SneakyThrows
      public void actionPerformed(ActionEvent e) {
        logger.info("Botão \"Selecionar\" pressionado");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();

          Util.addGitFiles(file);
          // Build the model from previous Git Path using GitDir Class
          Util.buildDefaultListModel();

          // Set a JList containing GitDir's
          list.setModel(Util.getListModel());

          logger.info(Util.getListModel().toString()); //

          // Use a GitDirListRenderer to renderer list cells
          list.setCellRenderer(new GitDirListRenderer());
          list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          lblPastaPai.setToolTipText("Selecionado: " + file.getCanonicalPath());
          lblPastaPai.setText("Selecionado: " + file.getCanonicalPath());
        } else {
          lblPastaPai.setText(label);
        }

      }
    });
    btnSelect.setBounds(362, 244, 123, 23);
    contentPane.add(btnSelect);

    JSeparator separator = new JSeparator();
    separator.setBounds(35, 82, 396, 2);
    contentPane.add(separator);

    JSeparator separator_1 = new JSeparator();
    separator_1.setBounds(35, 179, 396, 2);
    contentPane.add(separator_1);

    JSeparator separator_2 = new JSeparator();
    separator_2.setBounds(35, 237, 396, 2);
    contentPane.add(separator_2);

    // Add a mouse listener to handle changing selection
    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent event) {
        JList<GitDir> list = (JList<GitDir>) event.getSource();

        // Get index of item clicked
        int index = list.locationToIndex(event.getPoint());
        GitDir item = (GitDir) list.getModel().getElementAt(index);

        // Toggle selected state
        item.setSelected(!item.isSelected());

        // Repaint cell
        list.repaint(list.getCellBounds(index, index));

        logger.info(item + " " + item.isSelected());
      }
    });
  }
}
