document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");
    const signupBtn = document.getElementById("signupBtn");

    // 입력 요소
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const checkEmailBtn = document.getElementById("checkEmailBtn");
    const emailCheckResult = document.getElementById("emailCheckResult");

    const passwordInput = document.getElementById("password");
    const passwordConfirmInput = document.getElementById("passwordConfirm");
    const passwordMatchResult = document.getElementById("passwordMatchResult");
    const togglePassword = document.getElementById("togglePassword");

    const birthInput = document.getElementById("birthDate");
    const genderSelect = document.getElementById("gender");
    const phoneInput = document.getElementById("phone");
    const addressInput = document.getElementById("address");

    // 상태 변수
    let emailVerified = false;
    let passwordValid = false;
    let passwordMatched = false;
    let basicFieldsValid = false;

    // ------------------------------
    // 이메일 중복 확인
    // ------------------------------
    checkEmailBtn.addEventListener("click", async function () {
        const email = emailInput.value.trim();

        if (email === "") {
            emailCheckResult.textContent = "이메일을 입력해주세요.";
            emailCheckResult.style.color = "red";
            emailVerified = false;
            updateSubmitButtonState();
            return;
        }

        try {
            const response = await fetch(`/user/check-email?email=${encodeURIComponent(email)}`);
            const data = await response.json();

            if (data.exists) {
                emailCheckResult.textContent = "이미 사용 중인 이메일입니다.";
                emailCheckResult.style.color = "red";
                emailVerified = false;
            } else {
                emailCheckResult.textContent = "사용 가능한 이메일입니다.";
                emailCheckResult.style.color = "green";
                emailVerified = true;
            }
        } catch (error) {
            console.error("이메일 중복 확인 오류:", error);
            emailCheckResult.textContent = "서버 오류가 발생했습니다.";
            emailCheckResult.style.color = "red";
            emailVerified = false;
        }

        updateSubmitButtonState();
    });

    // 이메일 수정 시 다시 확인 필요
    emailInput.addEventListener("input", function () {
        emailVerified = false;
        emailCheckResult.textContent = "이메일 변경 시 다시 중복확인을 해주세요.";
        emailCheckResult.style.color = "gray";
        updateSubmitButtonState();
    });

    // ------------------------------
    // 비밀번호 유효성 및 일치 검사
    // ------------------------------
    function validatePasswordStrength(password) {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,20}$/;
        return regex.test(password);
    }

    function checkPasswordValidity() {
        const password = passwordInput.value;
        const confirm = passwordConfirmInput.value;

        if (password === "") {
            passwordMatchResult.textContent = "";
            passwordValid = false;
            passwordMatched = false;
            updateSubmitButtonState();
            return;
        }

        if (!validatePasswordStrength(password)) {
            passwordMatchResult.textContent = "비밀번호는 8~20자, 영문과 숫자를 포함해야 합니다.";
            passwordMatchResult.style.color = "red";
            passwordValid = false;
        } else {
            passwordValid = true;
        }

        if (confirm.length > 0) {
            if (password === confirm) {
                passwordMatchResult.textContent = "비밀번호가 일치합니다.";
                passwordMatchResult.style.color = "green";
                passwordMatched = true;
            } else {
                passwordMatchResult.textContent = "비밀번호가 일치하지 않습니다.";
                passwordMatchResult.style.color = "red";
                passwordMatched = false;
            }
        }

        updateSubmitButtonState();
    }

    passwordInput.addEventListener("input", checkPasswordValidity);
    passwordConfirmInput.addEventListener("input", checkPasswordValidity);

    // ------------------------------
    // 비밀번호 보기 토글
    // ------------------------------
    togglePassword.addEventListener("click", function () {
        const type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
        passwordInput.setAttribute("type", type);
        togglePassword.textContent = type === "password" ? "보기" : "숨기기";
    });

    // ------------------------------
    // 전화번호 자동 하이픈 입력
    // ------------------------------
    phoneInput.addEventListener("input", function () {
        let value = phoneInput.value.replace(/[^0-9]/g, ""); // 숫자만 남기기

        if (value.length > 3 && value.length <= 7) {
            value = value.replace(/(\d{3})(\d+)/, "$1-$2");
        } else if (value.length > 7) {
            value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
        }

        phoneInput.value = value;
        validateBasicFields();
    });

    // ------------------------------
    // 기타 입력 필드 유효성 검사
    // ------------------------------
    function validateBasicFields() {
        const nameValid = nameInput && nameInput.value.trim().length > 0;
        const birthValid = birthInput && birthInput.value.trim().length > 0;
        const genderValid = genderSelect && genderSelect.value.trim().length > 0;
        const phoneValid = /^010-\d{4}-\d{4}$/.test(phoneInput.value.trim());
        const addressValid = addressInput && addressInput.value.trim().length > 0;

        basicFieldsValid = nameValid && birthValid && genderValid && phoneValid && addressValid;
        updateSubmitButtonState();
    }

    [nameInput, birthInput, genderSelect, addressInput].forEach(input => {
        input.addEventListener("input", validateBasicFields);
        input.addEventListener("change", validateBasicFields);
    });

    // ------------------------------
    // 최종 제출 검증
    // ------------------------------
    signupForm.addEventListener("submit", function (e) {
        if (!emailVerified) {
            e.preventDefault();
            emailCheckResult.textContent = "이메일 중복확인을 완료해주세요.";
            emailCheckResult.style.color = "red";
            return false;
        }

        if (!passwordValid) {
            e.preventDefault();
            passwordMatchResult.textContent = "비밀번호 형식이 올바르지 않습니다.";
            passwordMatchResult.style.color = "red";
            return false;
        }

        if (!passwordMatched) {
            e.preventDefault();
            passwordMatchResult.textContent = "비밀번호가 일치하지 않습니다.";
            passwordMatchResult.style.color = "red";
            return false;
        }

        if (!basicFieldsValid) {
            e.preventDefault();
            alert("모든 필드를 올바르게 입력해주세요.");
            return false;
        }
    });

    // ------------------------------
    // 버튼 활성화 조건
    // ------------------------------
    function updateSubmitButtonState() {
        if (emailVerified && passwordValid && passwordMatched && basicFieldsValid) {
            signupBtn.disabled = false;
        } else {
            signupBtn.disabled = true;
        }
    }
});
