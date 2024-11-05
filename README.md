# Backend
Talk-Spark 백엔드 레포지토리

<details>
  <summary> <b> 🏃 작업 진행 과정 </b> </summary>
  <img width="389" alt="1" src="https://github.com/user-attachments/assets/86c1bb8c-00fa-4989-9712-5e111842833b"> <br>
  Issues 탭으로 이동해, New 버튼을 누르고 '이슈 생성 기본 템플릿' 으로 이슈 작성을 시작합니다.
  
  <img width="959" alt="2" src="https://github.com/user-attachments/assets/f1ce009e-ffc7-4440-91af-b615118865c0"> <br>
  템플릿에 맞춰 Issue를 작성합니다. 이번에 작업할 내용을 간략하게 설명합니다.

  <img width="333" alt="3" src="https://github.com/user-attachments/assets/16740f02-9532-4200-b138-34a70631d28f"> <br>
  작성 시, 우측에 보이는 Assignee에는 본인을 할당하고, Labels에 적절한 Label들을 할당합니다.

  <img width="376" alt="4" src="https://github.com/user-attachments/assets/e47af4c5-bdee-40ea-ac95-de00478383d5"> <br>
  이슈 작성 후, 해당 이슈에 대한 작업을 진행할 브랜치를 생성해야 합니다. 우측에 보이는 Create a branch를 클릭합니다.

  <img width="476" alt="5" src="https://github.com/user-attachments/assets/a0252f03-3890-46a8-9144-9682f6096899"> <br>
  branch source가 develop인 것을 확인하고, 위 사진과 같은 형식의 branch name을 설정해 checkout locally 해줍니다.

  <img width="468" alt="6" src="https://github.com/user-attachments/assets/06dea34e-e13f-445d-83d1-23c494ddf3b1">
  <img width="819" alt="7" src="https://github.com/user-attachments/assets/f1bc2a8c-dd4c-4e14-b9bf-732958582f79">
  <br>
  본인 컴퓨터에 클론받은 talkspark 프로젝트에서 다음 명령어를 실행해 본인 브랜치로 이동합니다.

  <br>

  <img width="381" alt="8" src="https://github.com/user-attachments/assets/8f6c6da3-64e4-4e6c-9e5a-83cb32a96267">
  <img width="1387" alt="9" src="https://github.com/user-attachments/assets/b8bf087e-0838-48cf-ba46-140d0edf6bb8">
  <br>
  이동한 브랜치에서 작업합니다. 커밋 메시지 규칙을 지키며, 커밋을 쌓습니다. 커밋 메시지에는 이슈 번호도 표기합니다.

  <img width="920" alt="10" src="https://github.com/user-attachments/assets/fa95dba1-ec8d-4e50-b2c4-33d2209b84ad"> <br>
  해당 이슈를 위한 작업이 끝나면, 그 브랜치에서 develop으로 PR을 작성합니다. 템플릿을 활용해 작성합니다.

  <img width="344" alt="11" src="https://github.com/user-attachments/assets/eb810b37-371c-42c5-926c-624d453b3808"> <br>
  작성 시, 우측의 Reviewers에 백엔드 인원들을 할당하고, Assignees에 본인을 할당합니다. 이후 Labels에 적절한 Label들을 할당합니다.

  <img width="1404" alt="12" src="https://github.com/user-attachments/assets/72a564d5-9db9-4488-891d-4b95d6d64fca"> <br>
  작성된 PR은 한 명 이상의 Approve 리뷰가 있어야 병합될 수 있습니다. 리뷰하시는 분은 Files changed 탭에서 코드를 읽어보시고, 병합해도 될 것 같다면 Approve에 체크하시고 리뷰를 남겨주시면 됩니다.

  <br>

  본인의 브랜치가 develop에 병합되었다면, 본인 브랜치는 삭제해주시면 됩니다.
</details>

<details>
  <summary> <b> 📋 커밋 메시지, 이슈, PR에 입력할 작업 종류 리스트 </b> </summary>
  
  <img width="523" alt="스크린샷 2024-11-05 오전 11 29 26" src="https://github.com/user-attachments/assets/3e953fad-85eb-4791-96c2-9ebc73a2a7f0"> <br>

  커밋 메시지에는 이슈번호를 #과 함께 입력해줍니다.
</details>

## ❗️❗️❗️ application.yaml 관련 주의사항 ❗️❗️❗️
/src/main/resources에 application.yaml이 있습니다. <br>
<img width="376" alt="스크린샷 2024-11-05 오전 11 40 26" src="https://github.com/user-attachments/assets/19fc0c8f-4a08-4b36-9621-126418f2c20a"> <br>
이 파일에는 위 사진과 같은 내용만 있습니다. 각자 로컬에서 작업할 때 이 파일의 내용은 수정하지 않습니다. <br> <br>
<img width="205" alt="스크린샷 2024-11-05 오전 11 41 46" src="https://github.com/user-attachments/assets/edccd8ea-0136-4b2c-ae17-2209c5583623"> <br>
/src/main/resources/config 디렉토리를 생성하고, 그 디렉토리에서 secret.yaml 파일을 생성합니다. 이 파일을 각자 설정에 사용하도록 합니다. 이는 gitignore에 포함되어 github에 올라오지 않습니다. 예시를 위한 파일 내용은 다른 채널을 통해 공유하겠습니다.
