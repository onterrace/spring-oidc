# Initialize your Office Add-in
이 문서의 원본은 [여기](https://docs.microsoft.com/en-us/office/dev/add-ins/develop/initialize-add-in)를 참고한다. 


그러나 Office 추가 기능은 라이브러리가 로드될 때까지 Office JavaScript API를 성공적으로 호출할 수 없습니다. 이 문서에서는 코드에서 라이브러리가 로드되었는지 확인할 수 있는 두 가지 방법을 설명합니다.

* Initialize with Office.onReady().
* Initialize with Office.initialize.



> **Office.initialize 대신에 Office.onReady()를 사용할 것을 추천한다.** 


## Initialize with Office.onReady()

Office.onReady()는 asynchronous method이고Office.js library가 로드되었는지 확인하는 동안 Promise object를 반환한다. 

라이브러리가 로드될 때, 그것은 Office.HostType enum value(Excel, Word, etc.)로  Office client application과 Office.PlatformType enum value (PC, Mac, OfficeOnline, etc.)로 platform을 설명하는 객체로써 Promise를 resolve한다. 

Office.onReady()가 호출되었을 때 라이브러리가 이미 로드되었다면 즉시 Promise는 Resolve한다. 


Office.onReady()를 호출하는 한가지 방법은 callback method를 그것에 전달하는 것이다. 

```jsx
Office.onReady(function(info) {
    if (info.host === Office.HostType.Excel) {
        // Do Excel-specific initialization (for example, make add-in task pane's
        // appearance compatible with Excel "green").
    }
    if (info.platform === Office.PlatformType.PC) {
        // Make minor layout changes in the task pane.
    }
    console.log(`Office.js is now ready in ${info.host} on ${info.platform}`);
});
```

callback function을 전달하는 대신에, then() 메서드를 Office.onReady() 콜에 묶을 수 있다. 

예를들어, 아래의 코드는 사용자의 Excel 버전이 API를 지원하는지 알아보기 위해 확인한다. 

```jsx
Office.onReady()
    .then(function() {
        if (!Office.context.requirements.isSetSupported('ExcelApi', '1.7')) {
            console.log("Sorry, this add-in only works with newer versions of Excel.");
        }
    });
```

TypeScript 에서 async와 await를 사용하는 같은 샘플은 아래와 같다. 
```jsx
(async () => {
    await Office.onReady();
    if (!Office.context.requirements.isSetSupported('ExcelApi', '1.7')) {
        console.log("Sorry, this add-in only works with newer versions of Excel.");
    }
})();
```

그러나 이 practice는 예외가 있다. 예를 들어 브라우저 도구를 사용하여 UI를 디버그하기 위해 추가 기능을 Office 응용 프로그램에서 테스트용으로 로드하는 대신 브라우저에서 열고 싶다고 가정하자. 이 시나리오에서 Office.js가 Office 호스트 응용 프로그램 외부에서 실행 중임을 확인하면 호스트와 플랫폼 모두에 null인 콜백을 호출하고 promise를 resolve한다. 

또 다른 예외는 추가 기능이 로드되는 동안 작업창에 진행률 표시기를 표시하려는 경우이다. (Another exception would be if you want a progress indicator to appear in the task pane while the add-in is loading.) 이 시나리오에서, 당신의 코드는 jQuery ready를 호출해야하고 progress indicator를 render하기 위해 그것의 콜백을 사용해야 한다. 그래서 Office.onReady 콜백은 최종 UI로 progress indicator를 대체할 수 있다. 
































