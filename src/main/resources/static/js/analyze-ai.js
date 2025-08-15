let step = 0;

function handleNext() {
    if (step === 0) {
        const code = document.getElementById("codeInput").value.trim();
        if (!code) {
            alert("코드를 입력해주세요.");
            return;
        }
        document.getElementById("questionBox").textContent = "AI가 코드를 분석중입니다...";
        document.getElementById("nextButton").style.display = "none";
        analyzeCode();
        // step은 analyzeCode 완료 후 변경
    } else if (step === 1) {
        // 결과 보기 → 개선된 코드 textarea로 이동
        document.getElementById("codeOutput").scrollIntoView({ behavior: "smooth" });
        step = 0;
        document.getElementById("nextButton").textContent = "다음";

        document.getElementById("nextButton").style.display = "block";
        document.querySelector(".btn-group").style.display = "none";
        // 다시 입력 화면으로 전환
        document.getElementById("inputSection").style.display = "block";
        document.getElementById("outputSection").style.display = "none";
    }
}

async function analyzeCode() {
    const code = document.getElementById("codeInput").value.trim();
    const output = document.getElementById("codeOutput");
    output.value = "";

    try {
        const response = await fetch("/api/analyze-code", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ code })
        });

        const result = await response.json();
        output.value = result.correctedCode || "분석 결과가 없습니다.";

        // 분석 완료 후 화면 전환
        document.getElementById("inputSection").style.display = "none";
        document.getElementById("outputSection").style.display = "block";

        // 버튼 그룹 보이기
        document.querySelector(".btn-group").style.display = "block";
        document.getElementById("nextButton").style.display = "none";

        // 말풍선 문구 변경 → 분석 완료 알림
        document.getElementById("questionBox").innerHTML = "오류를 수정하고, 더 개선한 코드 결과입니다.<br>내용을 바탕으로 글 작성을 도와드릴까요?.";

    } catch (err) {
        output.value = "분석 중 오류가 발생했습니다. 다시 시도해주세요.";
        console.error(err);
    }
}

function goHome() {
  window.location.href = '/home';
}

function prepareWritePost(text = "") {
    document.body.style.background = "black url('../images/background.jpg') no-repeat center center";
    document.body.style.backgroundSize = "cover";

        // 기존 컨텐츠 비우고 새 화면 구성
        document.body.innerHTML = `
          <div class="result-container">
            <h2>코드 분석</h2>
            <textarea id="post-content" style="width: 100%; height: 400px; margin-top: 5px;">${text}</textarea>
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
              <div style="color: #777; font-style: italic;">
                ! TIP: 직접 수정하거나, 자신의 생각을 덧붙여 게시글을 작성하세요.
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

  const title = "코드 분석";
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