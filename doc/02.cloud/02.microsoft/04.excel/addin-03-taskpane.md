# Build an Excel task pane add-in 
이 문서의 원본은 [Build an Excel task pane add-in](https://docs.microsoft.com/en-us/office/dev/add-ins/quickstarts/excel-quickstart-jquery?tabs=yeomangenerator)와 [Tutorial: Create an Excel task pane add-in](https://docs.microsoft.com/en-us/office/dev/add-ins/tutorials/excel-tutorial)이다. 



## Prerequisites

* Node.js (the latest LTS version).
* The latest version of [Yeoman](https://github.com/yeoman/yo) and the [Yeoman generator for Office Add-ins](https://docs.microsoft.com/en-us/office/dev/add-ins/develop/yeoman-generator-overview). To install these tools globally, run the following command via the command prompt.


> LTS release status is "long-term support"

```
npm install -g yo generator-office
```


```
(base) PS C:\Users\latte> npm install -g yo generator-office
npm WARN deprecated har-validator@5.1.5: this library is no longer supported
npm WARN deprecated uuid@3.4.0: Please upgrade  to version 7 or higher.  Older versions may use Math.random() in certain circumstances, which is known to be problematic.  See https://v8.dev/blog/math-random for details.
npm WARN deprecated request@2.88.2: request has been deprecated, see https://github.com/request/request/issues/3142

added 953 packages, and audited 954 packages in 57s

65 packages are looking for funding
  run `npm fund` for details

7 vulnerabilities (5 moderate, 2 high)

To address issues that do not require attention, run:
  npm audit fix

To address all issues (including breaking changes), run:
  npm audit fix --force

Run `npm audit` for details.
npm notice
npm notice New minor version of npm available! 8.0.0 -> 8.9.0
npm notice Changelog: https://github.com/npm/cli/releases/tag/v8.9.0
npm notice Run npm install -g npm@8.9.0 to update!
npm notice
```

npm 버전이 낮아서 업데이트하라고 메지지가 나와서 업데이트했다. 


## Create the add-in project

Yeoman generator를 사용하여 add-in project를 생성한다. 
```
yo office 
```
![](../../.gitbook/assets/excel/yo-office-excel.png)


질문이 나온다. 다음과 같이 답한다. 

* Choose a project type: Office Add-in Task Pane project
* Choose a script type: Javascript
* What do you want to name your add-in? My Office Add-in
* Which Office client application would you like to support? Excel

"My Office Add-in" 폴더가 생성된다. 

1. 폴더로 들어가서 
2. npm start 
3. VS Code에서 프로젝트를 오픈한다.    
```
code .
```


npm start 하면 다음과 같이 오류가 난다. 
```
unable to App type: desktop
? Allow localhost loopback for Microsoft Edge WebView? Yes
Error: Unable to start debugging.
```

**Terminal을 관리자 모드로 오픈**한 다음에 npm start를 시작하면 정상적으로 필요한 패키지를 설치하고 정상적으로 실행된다. 



## Explore the project
```shell
📂project_root
  📂assets
  📂dist
  📂node_modules
  📂src
    📂commands
    📂taskpane
       📄taskpane.css
       📄taskpane.html
       📄taskpane.js
  📄manifest.xml
  📄package.json
```

* **manifest.xml**     
추가 기능의 설정과 기능을 정의
* **src/taskpane/taskpane.html**    
파일에는 작업 창에 대한 HTML 마크업이 포함되어 있다 .
* **src/taskpane/taskpane.css**    
파일에는 작업 창의 콘텐츠에 적용되는 CSS가 포함되어 있다 .
* **src/taskpane/taskpane.js**    
파일에는 작업 창과 Office 클라이언트 응용 프로그램 간의 상호 작용을 용이하게 하는 Office JavaScript API 코드가 포함되어 있다.



> 지금 Tutorial: Create an Excel task pane add-in 문서를 학습하고 있다. 


## Create a table
### taskpane.html 
taskpane.html을 연다. 
\<main\> 요소를 찾고 그 안의 모든 요소를 삭제한다. 
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Contoso Task Pane Add-in</title>

    <!-- Office JavaScript API -->
    <script type="text/javascript" src="https://appsforoffice.microsoft.com/lib/1.1/hosted/office.js"></script>

    <!-- For more information on Fluent UI, visit https://developer.microsoft.com/fluentui#/. -->
    <link rel="stylesheet" href="https://static2.sharepointonline.com/files/fabric/office-ui-fabric-core/9.6.1/css/fabric.min.css"/>

    <!-- Template styles -->
    <link href="taskpane.css" rel="stylesheet" type="text/css" />
</head>
<body class="ms-font-m ms-welcome ms-Fabric">
    <header class="ms-welcome__header ms-bgColor-neutralLighter">
        <img width="90" height="90" src="../../assets/logo-filled.png" alt="Contoso" title="Contoso" />
        <h1 class="ms-font-su">Welcome</h1>
    </header>
    <section id="sideload-msg" class="ms-welcome__main">
        <h2 class="ms-font-xl">Please sideload your add-in to see app body.</h2>
    </section>
    <main id="app-body" class="ms-welcome__main">
        <!-- 여기에 버튼을 추가할 것이다. -->
    </main>
</body>

</html>
```
main tag 안에 다음을 추가한다. 
```html
<button class="ms-Button" id="create-table">Create Table</button><br/><br/>
```

### taskpane.js 


taskpane.js 파일을 열고 필요없는 것은 삭제하고 다음과 같이 작성한다. 
```jsx
Office.onReady((info) => {
  // 여기에 코드를 추가할 것이다. 
});
```



info.host가 Excel인 경우에만 동작하도록 if 구문을 추가하고, ExcelApi를 지원하는지 확인한다. 

```jsx
Office.onReady((info) => {
    if (!Office.context.requirements.isSetSupported("ExcelApi", "1.7")) {
      console.log("Sorry. The tutorial add-in uses Excel.js APIs that are not available in your version of Office.");
    }
  }
});  
```
이제 main 태그에 추가한 button의 이벤트를 등록한다. createTable 함수는 나중에 만들 것이다. 
```jsx
Office.onReady((info) => {
    if (!Office.context.requirements.isSetSupported("ExcelApi", "1.7")) {
      console.log("Sorry. The tutorial add-in uses Excel.js APIs that are not available in your version of Office.");
    }
    // Assign event handlers and other initialization logic.
    document.getElementById("create-table").onclick = createTable;
  }
});  
```
createTable 함수를 다음과 같이 작성한다. 
```jsx
async function createTable() {
  await Excel.run(async (context) => {
    // 여기에 로직을 추가할 것이다. 
    // TODO 1 
  })
  .catch(function (error) {
      console.log("Error: " + error);
      if (error instanceof OfficeExtension.Error) {
          console.log("Debug info: " + JSON.stringify(error.debugInfo));
      }
  });
}
```


TODO 1에 다음을 추가한다. 
```jsx
// 활성화된 worksheet를 얻는다 
const currentWorksheet = context.workbook.worksheets.getActiveWorksheet();
// range를 구한다. 
let range = currentWorksheet.getRange("C3");
range.values = [[ 5 ]];
range.format.autofitColumns();

await context.sync();
```
[Set and get range values](https://docs.microsoft.com/en-us/office/dev/add-ins/excel/excel-add-ins-ranges-set-get-values)     




## build 
프로젝트 루트에서 다음을 실행한다. 

```shell
npm run build
```


### 디버깅하기 
다음을 실행한다. 
```shell
npm start 
```

그러면 Excel이 실행된다. 
![](../.gitbook/assets/excel/excel-01.png)

"Create Table" 버튼을 클릭하면 "C3" 셀에 '5'가 입력될 것이다. 

디버깅을 하려면 왼쪽 화살표를 클릭한다. 

"지원받기, 다시로드,디버거 첨부,보안정보" 컨텍스트 메뉴가 표시된다. 
"디버거 첨부"를 클릭한다. 

그러면 Edge 개발자 도구가 오픈된다. 

![](../.gitbook/assets/excel/excel-02.png)

아니면 Taskpand을 선택하고 우측 마우스 클릭한 다음에 "검사" 메뉴를 클릭해도 된다. 

autofitColumns() 줄 아래에 console.log()를 추가하자. 

```jsx
  range.format.autofitColumns();
  
  console.log("Hello World!!!!!!!");
```

그리고, "다시 로드" 메뉴를 클릭한다. 
이제 "Create Table" 버튼을 클릭하면 콘솔에 'Hello World'가 출력된다. 

## local web server 중지하기 
이미 실행되고 있는 loca web server가 있다면 다음을 입력하여 중지한다.
```shell
npm stop 
```
