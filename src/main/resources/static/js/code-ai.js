const questionList = [
  {
    key: "language",
    question: "네, 프로젝트 코드 생성을 도와드리겠습니다.\n어떤 언어로 작성해드릴까요?",
    type: "single",
    options: ["C", "C++", "Java", "Python"]
  },
  {
    key: "algorithms",
    question: "어떤 알고리즘이 필요하신가요?\n필요하다고 생각되는 알고리즘들을 모두 선택해주세요!",
    type: "multi",
    options: ["정렬", "탐색", "최소신장트리", "동적계획법", "그리디", "큐", "스택", "트리"]
  },
  {
    key: "topic",
    question: "선택하신 언어와 알고리즘을 바탕으로, 어떤 프로젝트 생성을 도와드릴까요?\n원하시는 주제를 입력해주세요!",
    type: "text"
  },
  {
    key: "input_format",
    question: "좋은 프로젝트네요!\n이제 프로젝트의 완성도를 높여보겠습니다.\n해당 프로그램의 입력 형식을 선택해주세요!",
    type: "multi",
    options: ["정수", "문자", "1차원 배열", "2차원 배열", "텍스트파일"]
  },
  {
    key: "input_example",
    question: "입력 예시나 제한 조건이 있다면 입력해주세요!\n만약 입력 예시에 개행이 필요하다면, '/'로 개행을 표시해주세요.",
    type: "text"
  },
  {
    key: "output_format",
    question: "입력 조건 확인이 완료되었습니다!\n이번에는 프로그램에 맞는 출력 형식을 선택해주세요!",
    type: "multi",
    options: ["정수", "문자", "1차원 배열", "2차원 배열", "텍스트파일"]
  },
  {
    key: "output_example",
    question: "출력 예시가 있다면 입력해주세요!\n만약 출력 예시에 개행이 필요하다면 '/'로 개행을 표시해주세요.",
    type: "text"
  },
  {
    key: "error_handling",
    question: "마지막으로 완성도를 높혀보겠습니다!\n입력이 잘못되었을 때, 에러 메시지 출력 후\n어떻게 처리하기를 원하시나요?",
    type: "single",
    options: ["프로그램 종료", "재입력 요청"]
  },
  {
    key: "warnings",
    question: "주의해야할 점들이 있다면 입력해주세요!",
    type: "text"
  },
  {
    key: "style",
    question: "알겠습니다! 주의해서 도와드릴게요\n추가로, 원하는 코드 스타일이 있으신가요?",
    type: "multi",
    options: ["함수 단위로 작성", "main에 전부 작성", "주석 포함", "주석 제외", "클래스 사용", "간단 스크립트 형식"]
  }
];

let currentStep = 0;
let answers = {};

function typeText(element, text, speed = 50, callback) {
  element.innerText = "";
  let index = 0;
  const timer = setInterval(() => {
    element.innerText += text.charAt(index);
    index++;
    if (index >= text.length) {
      clearInterval(timer);
      if (callback) callback();
    }
  }, speed);
}

function renderQuestion() {
  const q = questionList[currentStep];
  const balloonBox = document.getElementById("balloon-box");
  typeText(balloonBox, q.question, 40);

  const box = document.getElementById("options-box");
  box.innerHTML = "";
  box.classList.remove("grid-2", "vertical");

  if (q.type === "single" || q.type === "multi") {
      if (q.options.length <= 4) {
        box.classList.add("vertical");
      } else {
        box.classList.add("grid-2");
      }
    q.options.forEach(option => {
      const btn = document.createElement("button");
      btn.className = "option-button";
      btn.innerText = option;
      btn.onclick = () => handleOptionClick(q, btn, option);
      box.appendChild(btn);
    });
  } else if (q.type === "text") {
    const input = document.createElement("textarea");
    input.rows = 4;
    input.oninput = () => {
      answers[q.key] = input.value;
      document.getElementById("next-btn").style.display = "inline";
    };
    box.appendChild(input);
  }

  document.getElementById("next-btn").style.display = q.type === "text" ? "none" : "inline";
  document.getElementById("submit-btn").style.display = "none";
}

function handleOptionClick(q, btn, option) {
  if (q.type === "single") {
    answers[q.key] = option;
    document.querySelectorAll(".option-button").forEach(el => el.classList.remove("selected"));
    btn.classList.add("selected");
    document.getElementById("next-btn").style.display = "inline";
  } else {
    btn.classList.toggle("selected");
    answers[q.key] = answers[q.key] || [];
    if (btn.classList.contains("selected")) {
      answers[q.key].push(option);
    } else {
      answers[q.key] = answers[q.key].filter(o => o !== option);
    }
    document.getElementById("next-btn").style.display = answers[q.key].length > 0 ? "inline" : "none";
  }
}

document.getElementById("next-btn").onclick = () => {
  currentStep++;
  if (currentStep < questionList.length) {
    renderQuestion();
  } else {
    document.getElementById("next-btn").style.display = "none";
    document.getElementById("submit-btn").style.display = "inline";
  }
};

document.getElementById("submit-btn").onclick = () => {
  document.getElementById("submit-btn").style.display = "none";
  document.getElementById("options-box").style.display = "none";
  const loadingContainer = document.getElementById("loading-container");
  loadingContainer.style.display = "block";

  loadingContainer.innerHTML = `
    <h2 style="text-align: center; color: white;">프로젝트 생성 중...</h2>
    <div id="loading-bar" style="
      position: relative;
      width: 100%;
      height: 29px;
      background: #b2f5ea;
      border-radius: 10px;
      overflow: hidden;
      margin-top: 20px;
    ">
      <div id="loading-progress" style="
        height: 100%;
        width: 0%;
        background: white;
        border-radius: 10px;
        transition: width 0.3s;
      "></div>

      <span id="loading-percent" style="
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: black;
        font-weight: bold;
        font-size: 14px;
      ">0%</span>
    </div>
  `;
  typeText(document.getElementById("balloon-box"), "모두 확인했습니다!\n알려주신 정보들을 바탕으로 프로젝트 생성을 도와드리겠습니다.\n잠시만 기다려주세요!", 40);

  const progressBar = document.getElementById("loading-progress");
  const percentText = document.getElementById("loading-percent");

  let progress = 0;
  const duration = 6000;
  const interval = 50;
  const increment = 100 / (duration / interval);

  const timer = setInterval(() => {
    progress += increment;
    if (progress >= 100) {
      progress = 100;
      clearInterval(timer);
    }
    progressBar.style.width = `${progress}%`;
    percentText.textContent = `${Math.round(progress)}%`;
  }, interval);

  setTimeout(() => {
    fetch("/api/generate-project", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(answers)
    })
    .then(res => res.json())
    .then(data => {
      loadingContainer.style.display = "none";
      document.body.style.background = "black url('../images/background.jpg') no-repeat center center";
      document.body.style.backgroundSize = "cover";
      const app = document.getElementById("app");
      app.innerHTML = `
        <div class="result-container">
          <h2>프로젝트 생성</h2>
          <textarea style="width:100%;height:400px; margin-top: 5px;">${data.code}</textarea>
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
            <div style="color: #777; font-style: italic;">
              ! TIP: 직접 수정하거나, 자신의 생각을 덧붙여 제시글을 작성하세요.
            </div>
            <div>
              <button onclick="submitGeneratedProject()" class="custom-button button-complete">작성 완료</button>
              <button onclick="location.href='index.html'" class="custom-button button-home">처음으로</button>
            </div>
          </div>
        </div>
      `;
    });
  }, duration);
};

window.onload = renderQuestion;

function submitGeneratedProject() {
  const username = localStorage.getItem("username");
  if (!username) {
    alert("로그인이 필요합니다.");
    window.location.href = "/login.html";
    return;
  }

  const title = "AI 생성 프로젝트";
  const content = document.querySelector("textarea").value;
  const tag = "AI";

  fetch("/api/board", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Username": username
    },
    body: JSON.stringify({
      title,
      userName: username,
      content,
      tag
    })
  })
    .then(res => {
      if (res.ok) {
        window.location.href = "/index.html";
      } else {
        return res.json().then(data => {
          alert(data.message || "게시글 작성에 실패했습니다.");
        });
      }
    })
    .catch(err => {
      console.error("Error writing project:", err);
      alert("오류가 발생했습니다.");
    });
}