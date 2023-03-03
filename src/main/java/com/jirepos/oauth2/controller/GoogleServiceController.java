package com.jirepos.oauth2.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.jirepos.core.util.ClassPathFileUtil;
import com.jirepos.core.util.ServletUtils;

import lombok.RequiredArgsConstructor;

/** 
 * OpenID conenct Login 이후에 Google API를 이용하여 구글 문서 정보를 조회하기 위한 컨트롤러이다. 
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/google")
public class GoogleServiceController {

    // import com.google.api.client.json.gson.GsonFactory;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    /** 구글 문서를 조회하여 퍼미션 목록을 출력하는 테스트 */
    @RequestMapping("/permission-list")
    public String getPermissionsOfGoogleDoc(Model model) throws Exception {

        // 세션에서 accessToken을 가져옴
        Optional<HttpServletRequest> opt = ServletUtils.getRequest();
        HttpServletRequest request = opt.get();
        String accessToken = (String) request.getSession().getAttribute("accessToken");

        // https://developers.google.com/api-client-library/java/google-api-java-client/oauth2?hl=ko
        // GoogleCredential은 액세스 토큰을 사용하여 보호된 리소스에 액세스하기 위한 OAuth 2.0의 스레드 안전 도우미
        // 클래스입니다.
        // 예를 들어 이미 액세스 토큰이 있는 경우 다음과 같은 방법으로 요청할 수 있습니다.

        // GoogleCredential이 deprecatd 되어서 GoogleCredentials를 쓰라고 했다는데
        // 관련된 자료를 찾을 수 없음.
        GoogleCredential credential = new GoogleCredential();
        // 세션에서 꺼낸 accessToken을 설정한다.
        credential.setAccessToken(accessToken);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();
        // 구글 드라이브에서 문서를 생성하고 링크를 복사한 다음에 문서 아이디만 추출하면 된다.
        String fileId = "1rLaJbOeIk_L9Vy-I5V6KuEU_imqyN-5MobH03XGEhYE";
        // setFields("*")을 해야만 email 주소를 반환한다.
        PermissionList permissions = service.permissions().list(fileId).setFields("*").execute();
        permissions.getPermissions().forEach(permission -> {
            System.out.println(permission.getEmailAddress());
        });
        //
        model.addAttribute("permission", permissions.getPermissions());
        return "permissionList";
    }

    /** 서비스 어카운트를 사용하여 사용자의 이메일 목록을 조회한다. */
    @GetMapping("/gmail-list")
    public String getEmailsUsinServiceAccount(Model model) throws IOException, GeneralSecurityException  {

        String GSUITE_SERVICE_ACCOUNT_EMAIL = "sanghyeon-svc@sanghyeon-test.iam.gserviceaccount.com";
        String GSUITE_SERVICE_ACCOUNT_KEY_FILE_PATH = "google/sanghyeon-test-0867b06eddeb.p12";
        String APPLICATION_NAME = "Naon Groupware";
        List<String> GMAIL_SCOPES = Arrays.asList(
                "https://www.googleapis.com/auth/gmail.compose",
                "https://www.googleapis.com/auth/gmail.labels",
                "https://www.googleapis.com/auth/gmail.readonly");

        List<String> LABEL_IDS = Arrays.asList("INBOX");

        String userEmail = "admin@gsuite.naonsoft.kr";

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(GSUITE_SERVICE_ACCOUNT_EMAIL) // Service Account Email
                .setServiceAccountUser(userEmail) // 사용자 이메일
                .setServiceAccountScopes(GMAIL_SCOPES)
                .setServiceAccountPrivateKeyFromP12File(ClassPathFileUtil.getFileObject(
                        GSUITE_SERVICE_ACCOUNT_KEY_FILE_PATH))
                .build();

        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();

        ListMessagesResponse response; 
        List<Message> messages = new ArrayList<Message>();
        response = service.users().messages()
                .list(userEmail)
                .setMaxResults(5L)
                .execute();// 5개

        if (response != null && response.getMessages() != null) {
            for (Message message : response.getMessages()) {
                System.out.println(message.getId());

            }
            model.addAttribute("mails", response.getMessages());
        }
        return "gmail-messages";
    }//:

}/// ~
