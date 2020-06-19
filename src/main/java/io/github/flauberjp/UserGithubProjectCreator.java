package io.github.flauberjp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class UserGithubProjectCreator {

  public static void main(String[] args) throws IOException, URISyntaxException {
    System.out.println("Programa iniciado às: " + LocalDateTime.now());

    System.out.println(criaProjetoInicialNoGithub(UserGithubInfo.get()));

    System.out.println("Programa finalizado às: " + LocalDateTime.now());
  }

  public static boolean criaProjetoInicialNoGithub(UserGithubInfo userGithubInfo) {
    String dataEHoraExecucao = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    boolean result;
    try {
      GitHub github = GitHub.connectUsingPassword(userGithubInfo.getUsername(), userGithubInfo.getPassword());

      GHCreateRepositoryBuilder repo = github.createRepository(userGithubInfo.getRepoName());
      repo.private_(true);
      repo.create();

      CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
          userGithubInfo.getUsername(), userGithubInfo.getPassword());

      Git git;

      String dir = EvidenceGenerator
          .getDirOndeRepositorioRemotoSeraClonado(userGithubInfo.getRepoName());

      git = Git.cloneRepository().setDirectory(new File(dir))
          .setCredentialsProvider(credentialsProvider).setURI(userGithubInfo.getRepoNameFullPath())
          .call();

      StoredConfig config = git.getRepository().getConfig();
      config.setString("user", null, "name", userGithubInfo.getGithubName());
      config.setString("user", null, "email", userGithubInfo.getGithubEmail()); //NOI18N
      config.save();

      // Copia arquivos iniciais usando templates
      Util.convertResourceToFile("initialProjectTemplate/template_index.html", dir + "/index.html");
      Util.convertResourceToFile("initialProjectTemplate/template_README.md", dir + "/README.md");

      git.add().addFilepattern(".").call();
      git.commit().setMessage("Initial setup").call();
      git.push().setCredentialsProvider(credentialsProvider).call();

      result = true;
    } catch (Exception e) {
      e.printStackTrace();
      result = false;
    }
    return result;
  }
}
