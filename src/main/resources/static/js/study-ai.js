let currentQuestion = null;
let selectedChoice = null;

window.onload = function () {
  loadQuestion();
};

function loadQuestion() {
  document.getElementById('resultScreen').style.display = 'none';
  document.getElementById('questionBox').innerText = '문제를 불러오는 중입니다...';
  document.getElementById('choices').innerHTML = '';
  selectedChoice = null;

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
      document.getElementById('resultText').innerText =
        data.correct ? "✅ 정답입니다!" : "❌ 오답입니다!";
      document.getElementById('explanationText').innerText = data.explanation;
      document.getElementById('resultScreen').style.display = 'block';
    });
}

function goHome() {
  window.location.href = '/home';
}

function writePost() {
  const text = `문제: ${currentQuestion.question}\n선택한 답: ${selectedChoice}\n정답: ${currentQuestion.correctAnswer}\n해설: ${currentQuestion.explanation}`;
  location.href = `/board/write?content=${encodeURIComponent(text)}`;
}
