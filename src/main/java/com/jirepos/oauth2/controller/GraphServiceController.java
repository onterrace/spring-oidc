
package com.jirepos.oauth2.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.jirepos.core.util.ServletUtils;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;

import okhttp3.Request;

/**
 * Graph API 테스트용 컨트롤러이다.
 */
@Controller
@RequestMapping("/graph")
public class GraphServiceController {

  /**
   * 세션에 저장된 accessToken을 반환한다.
   */
  private String getAccessToken() {
    Optional<HttpServletRequest> opt = ServletUtils.getRequest();
    HttpServletRequest request = opt.get();
    String accessToken = (String) request.getSession().getAttribute("accessToken");
    return accessToken;
  }

  /**
   * 메일 목록을 조회한다.
   */
  @GetMapping("/maillist")
  public String getMailList(Model model) throws Exception {

    String clientId = ""; // application id라고도 부른다.
    String clientSecret = ""; // Azure portal에서 복사
    String tenantId = ""; // tenant id는 Azure portal에서 복사
    String authority = "https://login.microsoftonline.com/" + tenantId + "/";

    ConfidentialClientApplication application = ConfidentialClientApplication
        .builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret))
        .authority(authority)
        .build();

    String accessToken = getAccessToken(); // 사용자가 로그인을 했다고 가정
    IAuthenticationProvider authProvider = new IAuthenticationProvider() {
      @Override
      public CompletableFuture<String> getAuthorizationTokenAsync(URL requestUrl) {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete(accessToken);
        return future;
      }
    };

    GraphServiceClient<Request> graphClient = GraphServiceClient
        .builder()
        .authenticationProvider(authProvider)
        .buildClient();

    // 메일 리스트 가져오기
    MessageCollectionPage messages = graphClient.me().messages()
        .buildRequest()
        .select("sender,subject")
        .get();

    System.out.println("메일 리스트 가져오기");
    model.addAttribute("messages", messages);
    return "maillist";
  }

  /**
   * Google의 Service Account와 유사한 방식으로 사용자 로그인 없이 Graph API를 호출한다.
   */
  @GetMapping("/dirve-list")
  public String getDriveList(Model model) throws Exception {

    String clientId = ""; // application id라고도 부른다.
    String clientSecret = ""; // Azure portal에서 복사
    String tenantId = ""; // tenant id는 Azure portal에서 복사
    String authority = "https://login.microsoftonline.com/" + tenantId + "/";

    final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .tenantId(tenantId)
        .build();

    List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");

    final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes,
        clientSecretCredential);

    final GraphServiceClient<Request> graphClient = GraphServiceClient
        .builder()
        .authenticationProvider(tokenCredentialAuthProvider)
        .buildClient();

    DriveItemCollectionPage children = graphClient.drive().root().children()
        .buildRequest()
        .get();
    model.addAttribute("children", children);

    return "maillist";
  }

  /**
   * Teams의 Tab app에서 전달한 OAuth Token을 이용하여 사용자를 대신하여 그래프 API를 호출한다. 
   * @param authToken
   * @param model
   * @return
   * @throws Exception
   */
  @GetMapping("/teams-auth-token")
  public ResponseEntity<MessageCollectionPage> userTeamsAuthToken(@RequestParam("token") String authToken, Model model) throws Exception {

    String clientId = "c4a68db2-962d-4d44-ab37-c88a9ceaa7b5"; // application  id라고도 부른다.
    String clientSecret = "m1g8Q~yxOGA5s.DYEMDx59_pQKm2eqfRHD15QaYS"; // Azure  portal에서 복사
    String tenantId = "0f298de5-864d-4f37-9497-77ace3386e9e";  // // tenant id는 Azure portal에서 복사
    String authority = "https://login.microsoftonline.com/" + tenantId + "/";

    ConfidentialClientApplication application;
    application = ConfidentialClientApplication
        .builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret))
        .authority(authority)
        .build();

    String scope = "https://graph.microsoft.com/.default";
    OnBehalfOfParameters parameters = OnBehalfOfParameters.builder(
        Collections.singleton(scope),
        new UserAssertion(authToken))
        .build();

    IAuthenticationResult authResult = application.acquireToken(parameters).join();
    String accessToken = authResult.accessToken().toString();

    IAuthenticationProvider authProvider = new IAuthenticationProvider() {
      @Override
      public CompletableFuture<String> getAuthorizationTokenAsync(URL requestUrl) {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete(accessToken);
        return future;
      }
    };

    GraphServiceClient<Request> graphClient = GraphServiceClient
        .builder()
        .authenticationProvider(authProvider)
        .buildClient();

    // 메일 리스트 가져오기
    MessageCollectionPage messages = graphClient.me().messages()
        .buildRequest()
        .select("sender,subject")
        .get();

    System.out.println("메일 리스트 가져오기");
    // model.addAttribute("messages", messages);
    // return "maillist";
    return new ResponseEntity<>(messages, HttpStatus.OK);

  }//

}
