package io.github.flauberjp;

import static io.github.flauberjp.util.MyLogger.LOGGER;

import io.github.flauberjp.util.TemplateUtil;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

public class UserGithubProjectManipulator {

  public static void main(String[] args) throws IOException, URISyntaxException {
    LOGGER.info("Programa iniciado às: " + LocalDateTime.now());

    LOGGER.info("Projeto criado no Github com sucesso? " + criaProjetoInicialNoGithub(
        UserGithubInfo.get()));

    LOGGER.info("Programa finalizado às: " + LocalDateTime.now());
  }

  public static boolean criaProjetoInicialNoGithub(UserGithubInfo userGithubInfo) {
    LOGGER.debug("UserGithubProjectCreator.criaProjetoInicialNoGithub(userGithubInfo {})",
        userGithubInfo);
    boolean result = false;
    boolean repositorioExistente = false;
    try {
      repositorioExistente = UserGithubInfo.get().isRepoExistent();

      if (!repositorioExistente) {
        criaProjeto(userGithubInfo);
      }

      CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
          userGithubInfo.getUsername(), userGithubInfo.getPassword());

      Git git;

      String destinationDir = EvidenceGenerator
          .getDirOndeRepositorioRemotoSeraClonado(userGithubInfo.getRepoName());

      git = Git.cloneRepository().setDirectory(new File(destinationDir))
          .setCredentialsProvider(credentialsProvider).setURI(userGithubInfo.getRepoNameFullPath())
          .call();

      StoredConfig config = git.getRepository().getConfig();
      config.setString("user", null, "name", userGithubInfo.getGithubName());
      config.setString("user", null, "email", userGithubInfo.getGithubEmail()); //NOI18N
      config.save();

      if(seAlgumArquivoTiverSidoCriadoParaOProjeto(destinationDir)) {
        AddCommand addCommand = git.add().addFilepattern(".");
        addCommand.call();
        CommitCommand commitCommand = git.commit();
        commitCommand.setMessage("Initial setup").call();
        git.push().setCredentialsProvider(credentialsProvider).call();
      }

      result = true;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return result;
  }

  private static boolean seAlgumArquivoTiverSidoCriadoParaOProjeto(String destinationDir)
      throws IOException {
    return TemplateUtil.createIndexFileFromTemplateIfNotExist(destinationDir) |
        TemplateUtil.createReadmeFileFromTemplateIfNotExist(destinationDir);
  }

  private static void criaProjeto(UserGithubInfo userGithubInfo) throws IOException {
    LOGGER.debug("UserGithubProjectCreator.criaProjeto(userGithubInfo {})",
        userGithubInfo);
    GitHub github = userGithubInfo.get().getGitHub();
    GHCreateRepositoryBuilder repo = github.createRepository(userGithubInfo.getRepoName());
    repo.create();
  }
  
  private static void deletarProjeto(UserGithubInfo userGithubInfo) throws IOException {
    GHRepositorySearchBuilder search = userGithubInfo.getGitHub().searchRepositories();
    GHRepositorySearchBuilder s = search.q(userGithubInfo.getRepoName());

    PagedSearchIterable res = s.list();

    for (Object ghRepository : res) {
      if ((userGithubInfo.getUsername() + "/" + userGithubInfo.getRepoName())
          .equalsIgnoreCase(((GHRepository) ghRepository).getFullName())) {    	  
    	GHRepository repo  = (GHRepository) ghRepository;
    	repo.delete();
        break;
      }
    }
  }
  
  public static boolean deletarProjetoInicialNoGithub(UserGithubInfo userGithubInfo) {
    boolean result = false;
    boolean repositorioExistente = false;
    try {
      repositorioExistente = UserGithubInfo.get().isRepoExistent();

      if (repositorioExistente) {
    	  deletarProjeto(userGithubInfo);    	  
      }
      result = true;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return result;
  }
}
