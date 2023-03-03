# Tutorial: Create an Excel task pane add-in

이 문서의 원본은 [여기](https://docs.microsoft.com/en-us/office/dev/add-ins/tutorials/excel-tutorial)를 참조한다. 



## Create a table 

\<main\> tag 아래 라인들을 다 지운다. 
```html
    <main id="app-body" class="ms-welcome__main" style="display: none;">

    </main>
```

main tag 아래에 다음을 추가한다. 
```html
<button class="ms-Button" id="create-table">Create Table</button><br/><br/>
```

아래 지우고 

```jsx
document.getElementById("run").onclick = run;
```

run function 모두 지운다. 

