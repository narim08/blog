async function analyzeCode() {
    const code = document.getElementById("codeInput").value.trim();
    const output = document.getElementById("codeOutput");
    const loading = document.getElementById("loading");

    if (!code) {
        alert("코드를 입력해주세요.");
        return;
    }

    output.value = "";
    loading.style.display = "block";

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
    } catch (err) {
        output.value = "분석 중 오류가 발생했습니다. 다시 시도해주세요.";
        console.error(err);
    } finally {
        loading.style.display = "none";
    }
}

function goHome() {
  window.location.href = '/home';
}


