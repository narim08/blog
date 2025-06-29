const questionList = [
  {
    key: "language",
    question: "어떤 언어로 작성해드릴까요?",
    type: "single",
    options: ["c", "c++", "java", "python"]
  },
  {
    key: "algorithms",
    question: "어떤 알고리즘이 필요하신가요?",
    type: "multi",
    options: ["정렬", "탐색", "최소신장트리", "동적계획법", "그리디", "큐", "스택", "트리"]
  },
  {
    key: "topic",
    question: "프로젝트 주제를 입력해주세요!",
    type: "text"
  },
  {
    key: "input_format",
    question: "프로그램의 입력 형식을 선택해주세요!",
    type: "multi",
    options: ["정수", "문자", "1차원 배열", "2차원 배열", "텍스트파일"]
  },
  {
    key: "input_example",
    question: "입력 예시나 제한 조건이 있다면 입력해주세요! '/'로 개행을 표현하세요.",
    type: "text"
  },
  {
    key: "output_format",
    question: "출력 형식을 선택해주세요!",
    type: "multi",
    options: ["정수", "문자", "1차원 배열", "2차원 배열", "텍스트파일"]
  },
  {
    key: "output_example",
    question: "출력 예시가 있다면 입력해주세요! '/'로 개행을 표현하세요.",
    type: "text"
  },
  {
    key: "error_handling",
    question: "입력이 잘못되었다면 어떻게 처리할까요?",
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
    question: "원하는 코드 스타일이 있으신가요?",
    type: "multi",
    options: ["함수 단위로 작성", "main에 전부 작성", "주석 포함", "주석 제외", "클래스 사용", "간단 스크립트 형식"]
  }
];

let currentStep = 0;
let answers = {};

function renderQuestion() {
  const q = questionList[currentStep];
  document.getElementById("balloon-box").innerText = q.question;

  const box = document.getElementById("options-box");
  box.innerHTML = "";

  if (q.type === "single" || q.type === "multi") {
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
  fetch("/api/generate-project", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(answers)
  })
    .then(res => res.json())
    .then(data => {
      document.getElementById("app").innerHTML = `
        <h2>GPT가 생성한 코드</h2>
        <textarea style="width:100%;height:400px;">${data.code}</textarea>
        <button onclick="location.href='/board/write'">게시판에 올리기</button>
      `;
    });
};

window.onload = renderQuestion;
