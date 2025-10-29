document.addEventListener("DOMContentLoaded", () => {
  const password = document.getElementById("password");
  const passwordConfirm = document.getElementById("passwordConfirm");
  const toggleBtn = document.getElementById("togglePassword");
  const resultText = document.getElementById("passwordMatchResult");
  const signupBtn = document.getElementById("signupBtn");

  // 안내문 엘리먼트 생성
  const passwordRuleText = document.createElement("small");
  passwordRuleText.textContent = "비밀번호는 최소 8자 이상, 영문과 숫자를 포함해야 합니다.";
  passwordRuleText.classList.add("password-rule");

  // 비밀번호 박스(div.password-box) 바로 뒤에 삽입
  const passwordBox = document.querySelector(".password-box");
  passwordBox.insertAdjacentElement("afterend", passwordRuleText);

  // 비밀번호 규칙 검사 함수
  function validatePasswordRule() {
    const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
    if (!regex.test(password.value)) {
      passwordRuleText.style.color = "red";
      signupBtn.disabled = true;
      return false;
    } else {
      passwordRuleText.style.color = "#36d1b6";
      return true;
    }
  }

  // 비밀번호 일치 검사 함수
  function validatePasswordMatch() {
    if (password.value === "" || passwordConfirm.value === "") {
      resultText.textContent = "";
      signupBtn.disabled = true;
      return;
    }

    if (password.value === passwordConfirm.value) {
      resultText.textContent = "비밀번호가 일치합니다.";
      resultText.style.color = "green";
      if (validatePasswordRule()) signupBtn.disabled = false;
    } else {
      resultText.textContent = "비밀번호가 일치하지 않습니다.";
      resultText.style.color = "red";
      signupBtn.disabled = true;
    }
  }

  // 이벤트 등록
  password.addEventListener("keyup", () => {
    validatePasswordRule();
    validatePasswordMatch();
  });

  passwordConfirm.addEventListener("keyup", validatePasswordMatch);

  // 보기 버튼 (비밀번호 + 비밀번호 확인 동시)
  toggleBtn.addEventListener("click", () => {
    const type =
      password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);
    passwordConfirm.setAttribute("type", type);
    toggleBtn.textContent = type === "password" ? "보기" : "숨기기";
  });
});
