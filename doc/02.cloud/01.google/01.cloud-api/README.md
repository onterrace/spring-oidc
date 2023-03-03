# Google Cloud API

Google API는 인증 및 권한 부여에 OAuth 2.0 프로토콜을 사용합니다. 사용자는 구글에 로그인하고 서비스를 제공하는 웹앱은 구글의 인증정보를 사용하여 사용자를 로그인을 시킬 수 있습니다.  로그인한 사용자를 대신하여 Gmail이나 Google Drive API와 같은 API을 호출할 수 있습니다. 서비스 어카운트라는 특별한 계정을 이용하여 사용자의 리소스에 접근할 수 있습니다. 구글 번역, 지도 등 특정 API 또는 서비스에 대한 요청을 하는 경우에 API-KEY를 사용합니다.  이러한 시나리오는 다음과 같이 세가지 키워드로 분리할 수 있습니다. 


구글 클라우드 API를 사용하기 위해서는 구글 클라우드 콘솔에서 OAuth 동의화면 생성, OAuth Client 생성, 그리고 Service Account 생성을 해야하고, 구글 워크스페이스 어드민 콘솔에서는 서비스 어카운트에 대한 도메인 전체 위임을 해야 합니다. 

* **OAuth2**
    * 구글이 사용자 인증을 처리합니다. 
    * 사용자를 대신하여 구글 API를 사용합니다. 
        * Gmail, Google Drive 등 Workspace API가 해당됩니다. 
* **Service Account** 
    * 사용자를 대신하지 않고 어플리케이션이 구글 API를 사용합니다. 
        * Gmail, Google Drive 등 Workspace API가 해당됩니다. 
    * 서비스 계정은 계정에 고유한 이메일 주소로 식별됩니다.
    * 구글 워크스페이스(유료)를 사용해야 가능합니다. 
* **API Key** 
    * 지도, 번역 등의 사용량에 따라 과금 되는 API를 사용합니다. 


구글 클라우드 콘솔과 구글 워크스페이스 관리 콘솔은 아래 링크를 이용하면 됩니다. 
* [구글 클라우드 콘솔 → https://console.cloud.google.com/](https://console.cloud.google.com/)
    * OAuth2 클아이언트 등록
    * Service Account 등록
    * API-Key 등록
* [구글 워크스페이스 관리 콘솔 → https://accounts.google.com/](https://accounts.google.com/)
    * Service Account 권한 허용 


아래의 순서대로 생성합니다. 
* 프로젝트 생성
* API 사용 설정 
* OAuth2 동의화면 생성 
* OAuth2 클라이언트 생성

서비스 어카운트는 다음의 순서대로 생성합니다. 
* Service Account 생성(옵션)
* 도메인 전체 위임 

API-KEY는 별도의 순서가 없습니다. 
* API-KEY 생성 



