let currentQuestion = null;
let selectedChoice = null;

window.onload = function () {
  loadQuestion();
};

function loadQuestion() {
  document.getElementById('nextButton').style.display = 'none';
  document.getElementById('resultScreen').style.display = 'none';
  document.getElementById('questionBox').style.display = 'block';
  document.getElementById('questionBox').innerText = '문제를 불러오는 중입니다...';
  document.getElementById('choices').innerHTML = '';
  selectedChoice = null;
  document.getElementById('balloon-box').classList.remove('result-mode');

  fetch('/api/question')  // SpringBoot에서 AI 문제 생성 API 호출
    .then(res => res.json())
    .then(data => {
      currentQuestion = data;
      document.getElementById('questionBox').innerText = data.question;

      const choices = shuffle([data.correctAnswer, ...data.wrongAnswers]);
      const choiceHTML = choices.map(choice =>
        `<button onclick="selectChoice(this, '${choice}')">${choice}</button>`
      ).join('');

      document.getElementById('choices').innerHTML = choiceHTML;

      document.getElementById('nextButton').style.display = 'block';
    });
}

function shuffle(array) {
  return array.sort(() => Math.random() - 0.5);
}

function selectChoice(button, choice) {
  selectedChoice = choice;
  document.querySelectorAll('#choices button').forEach(btn => {
    btn.classList.remove('selected');
  });
  button.classList.add('selected');
}

function submitAnswer() {
  if (!selectedChoice) {
    alert("하나의 답안을 선택하세요!");
    return;
  }

  fetch('/api/answer', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      selected: selectedChoice,
      correctAnswer: currentQuestion.correctAnswer,
      explanation: currentQuestion.explanation
    })
  })
    .then(res => res.json())
    .then(data => {
      document.getElementById('questionBox').style.display = 'none';
      document.getElementById('resultText').innerText =
        data.correct ? "✅ 정답입니다!" : "❌ 오답입니다!";
      document.getElementById('explanationText').innerText = data.explanation;
      document.getElementById('resultScreen').style.display = 'block';

      document.getElementById('balloon-box').classList.add('result-mode');

      document.getElementById('nextButton').style.display = 'none';
    });
}

function goHome() {
  window.location.href = '/home';
}

function writePost() {
  const text = `문제: ${currentQuestion.question}\n선택한 답: ${selectedChoice}\n정답: ${currentQuestion.correctAnswer}\n해설: ${currentQuestion.explanation}`;
  document.body.style.background = "black url('../images/background.jpg') no-repeat center center";
    document.body.style.backgroundSize = "cover";

    // 기존 컨텐츠 비우고 새 화면 구성
    document.body.innerHTML = `
      <div class="result-container">
        <h2>개념 공부</h2>
        <textarea id="post-content" style="width: 100%; height: 400px; margin-top: 5px;">${text}</textarea>
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
          <div style="color: #777; font-style: italic;">
            ✏️ TIP: 설명을 추가하거나 문제를 스스로 다시 정리해 보세요!
          </div>
          <div>
            <button onclick="submitPost()" class="custom-button button-complete">작성 완료</button>
            <button onclick="location.href='/home'" class="custom-button button-home">처음으로</button>
          </div>
        </div>
      </div>
    `;
}

function submitPost() {
  const username = localStorage.getItem("username");
  if (!username) {
    alert("로그인이 필요합니다.");
    window.location.href = "/login.html";
    return;
  }

  const title = "개념 공부 한 문제 풀이";
  const content = document.getElementById("post-content").value;
  const tag = "학습";

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
      alert("게시글이 작성되었습니다.");
      window.location.href = "/home";
    } else {
      return res.json().then(data => {
        alert(data.message || "게시글 작성에 실패했습니다.");
      });
    }
  })
  .catch(err => {
    console.error("게시글 작성 중 오류:", err);
    alert("오류가 발생했습니다.");
  });
}