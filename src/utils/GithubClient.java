package utils;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import consts.GithubConfig;

import javax.validation.constraints.NotNull;

import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.net.HttpURLConnection;
import java.io.FileOutputStream;

public class GithubClient {

	/**
	 * @param githubRemoteUrl
	 *            Remote git http url which ends with .git.
	 * @param accessToken
	 *            Personal access token.
	 * @param branchName
	 *            Name of the branch which should be downloaded
	 * @param destinationDir
	 *            Destination directory where the downloaded files should be
	 *            present.
	 * @return
	 * @throws Exception
	 */
	public boolean downloadRepoContent(@NotNull String githubRemoteUrl, @NotNull String accessToken,
			@NotNull String branchName, @NotNull String destinationDir) throws Exception {
		// String githubSourceUrl, String accessToken
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(GithubConfig.username,
				GithubConfig.password);
		// URL fileUrl = new URL("file://" + destinationDir);
		File destinationFile = new File(destinationDir);
		// delete any existing file
		FileUtils.deleteDirectory(destinationFile);
		Git.cloneRepository().setURI(githubRemoteUrl).setBranch(branchName).setDirectory(destinationFile)
				.setCredentialsProvider(credentialsProvider).call();
		if (destinationFile.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean downloadRepoContentByAuthentication(@NotNull String githubRemoteUrl, @NotNull String accessToken,
			@NotNull String branchName, @NotNull String destinationDir) throws Exception {
		// String githubSourceUrl, String accessToken
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(accessToken, "");
		// URL fileUrl = new URL("file://" + destinationDir);
		File destinationFile = new File(destinationDir);
		// delete any existing file
		FileUtils.deleteDirectory(destinationFile);
		Git.cloneRepository().setURI(githubRemoteUrl).setBranch(branchName).setDirectory(destinationFile)
				.setCredentialsProvider(credentialsProvider).call();
		if (destinationFile.length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public String downloadRepoContentCheck(@NotNull String githubRemoteUrl, @NotNull String accessToken,
			@NotNull String branchName, @NotNull String destinationDir) throws Exception {
		// String githubSourceUrl, String accessToken
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(accessToken, "");
		// URL fileUrl = new URL("file://" + destinationDir);
		File destinationFile = new File(destinationDir);
		// delete any existing file
		FileUtils.deleteDirectory(destinationFile);
//		System.out.println(githubRemoteUrl+" aaa "+Git.cloneRepository()
//				.setCredentialsProvider(credentialsProvider).setURI(githubRemoteUrl)
//				.setDirectory(destinationFile).setBare(true).call().getRepository().getBranch());
		String branchDefault=Git.cloneRepository()
				.setCredentialsProvider(credentialsProvider).setURI(githubRemoteUrl)
				.setDirectory(destinationFile).setBare(true).call().getRepository().getBranch();
		return branchDefault;
	}

	public boolean downloadRepoContentByArchiveDownload(@NotNull String githubRemoteUrl, @NotNull String accessToken,
			 @NotNull String destinationDir) throws Exception {
		File destinationFile = new File(destinationDir);
		boolean result = false;
		try {
			
			URL url = new URL(githubRemoteUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String userCredentials = GithubConfig.username + ":" + GithubConfig.password;
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// connection.setRequestProperty("Content-Length", "" +
			// postData.getBytes().length);
			// connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			InputStream in = connection.getInputStream();
			FileOutputStream out = new FileOutputStream(destinationDir);
			result = copy(in, out, 1024);
			out.close();
			if (destinationFile.length() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;

	}

	public static boolean copy(InputStream input, OutputStream output, int bufferSize) {
		byte[] buf = new byte[bufferSize];
		boolean result = false;
		try {
			int n = input.read(buf);
			while (n >= 0) {
				output.write(buf, 0, n);
				n = input.read(buf);
			}
			output.flush();
			result = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
