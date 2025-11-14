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

    const passwordRuleMsg = document.getElementById("passwordRuleMsg");
    const passwordMatchMsg = document.getElementById("passwordMatchMsg");

    const togglePassword = document.getElementById("togglePassword");

    const birthInput = document.getElementById("birthDate");
    const genderSelect = document.getElementById("gender");
    const phoneInput = document.getElementById("phone");
	
    // 주소 필드들 (hidden address 대신 실제 입력 필드들 체크)
    const baseAddressInput = document.getElementById("baseAddress");
    const roadAddrInput = document.getElementById("roadAddr");
	
	// 요소 확인
    console.log("=== 요소 확인 ===");
    console.log("nameInput:", nameInput);
    console.log("emailInput:", emailInput);
    console.log("passwordInput:", passwordInput);
    console.log("birthInput:", birthInput);
    console.log("genderSelect:", genderSelect);
    console.log("phoneInput:", phoneInput);
    console.log("baseAddressInput:", baseAddressInput);
    console.log("roadAddrInput:", roadAddrInput);
    console.log("signupBtn:", signupBtn);

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
    // 비밀번호 유효성 검사 함수
    // ------------------------------
    function validatePasswordStrength(password) {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,20}$/;
        return regex.test(password);
    }

    // ------------------------------
    // 비밀번호 유효성 + 일치 검사
    // ------------------------------
    function checkPassword() {
        const pw = passwordInput.value;
        const pwC = passwordConfirmInput.value;

        // 1) 비밀번호 유효성 검사
        if (pw.length === 0) {
            passwordRuleMsg.textContent = "";
            passwordValid = false;
        } else if (!validatePasswordStrength(pw)) {
            passwordRuleMsg.textContent = "비밀번호는 8~20자, 영문 + 숫자 포함해야 합니다.";
            passwordRuleMsg.style.color = "red";
            passwordValid = false;
        } else {
            passwordRuleMsg.textContent = "사용 가능한 비밀번호입니다.";
            passwordRuleMsg.style.color = "green";
            passwordValid = true;
        }

        // 2) 비밀번호 일치 검사
        if (pwC.length === 0) {
            passwordMatchMsg.textContent = "";
            passwordMatched = false;
        } else if (pw === pwC) {
            passwordMatchMsg.textContent = "비밀번호가 일치합니다.";
            passwordMatchMsg.style.color = "green";
            passwordMatched = true;
        } else {
            passwordMatchMsg.textContent = "비밀번호가 일치하지 않습니다.";
            passwordMatchMsg.style.color = "red";
            passwordMatched = false;
        }

        updateSubmitButtonState();
    }

    passwordInput.addEventListener("input", checkPassword);
    passwordConfirmInput.addEventListener("input", checkPassword);

    // ------------------------------
    // 비밀번호 동시에 보기/숨기기
    // ------------------------------
    togglePassword.addEventListener("click", function () {
        const newType = passwordInput.getAttribute("type") === "password" ? "text" : "password";

        passwordInput.setAttribute("type", newType);
        passwordConfirmInput.setAttribute("type", newType);

        togglePassword.textContent = newType === "password" ? "보기" : "숨기기";
    });

    // ------------------------------
    // 전화번호 자동 하이픈
    // ------------------------------
    phoneInput.addEventListener("input", function () {
        let value = phoneInput.value.replace(/[^0-9]/g, "");

        if (value.length > 3 && value.length <= 7) {
            value = value.replace(/(\d{3})(\d+)/, "$1-$2");
        } else if (value.length > 7) {
            value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
        }

        phoneInput.value = value;
        validateBasicFields();
    });

    // ------------------------------
    // 기본 필드 유효성 검사
    // ------------------------------
    function validateBasicFields() {
        const nameValid = nameInput && nameInput.value.trim().length > 0;
        const birthValid = birthInput.value.trim().length > 0;
        const genderValid = genderSelect.value !== "";
        const phoneValid = /^010-\d{4}-\d{4}$/.test(phoneInput.value.trim());
        // 주소는 baseAddress와 roadAddr 둘 다 체크
        const addressValid = baseAddressInput.value.trim().length > 0 && 
                           roadAddrInput.value.trim().length > 0;
						   
	   console.log("=== 기본 필드 검증 ===");
	   console.log("이름:", nameValid, nameInput ? nameInput.value : "null");
	   console.log("생년월일:", birthValid, birthInput.value);
	   console.log("성별:", genderValid, genderSelect.value);
	   console.log("전화번호:", phoneValid, phoneInput.value);
	   console.log("우편번호:", baseAddressInput.value);
	   console.log("도로명:", roadAddrInput.value);
	   console.log("주소:", addressValid);

        basicFieldsValid = nameValid && birthValid && genderValid && phoneValid && addressValid;
        updateSubmitButtonState();
    }

    // 주소 검색 버튼 클릭 후에도 검증 실행
    if (baseAddressInput) {
        baseAddressInput.addEventListener("change", validateBasicFields);
    }
    if (roadAddrInput) {
        roadAddrInput.addEventListener("change", validateBasicFields);
    }

    [nameInput, birthInput, genderSelect].forEach(input => {
        if (input) {
            input.addEventListener("input", validateBasicFields);
            input.addEventListener("change", validateBasicFields);
        }
    });

    // ------------------------------
    // 제출 검증
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
            passwordRuleMsg.textContent = "비밀번호 형식이 올바르지 않습니다.";
            passwordRuleMsg.style.color = "red";
            return false;
        }

        if (!passwordMatched) {
            e.preventDefault();
            passwordMatchMsg.textContent = "비밀번호가 일치하지 않습니다.";
            passwordMatchMsg.style.color = "red";
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
		
		console.log("=== 버튼 활성화 체크 ===");
		       console.log("emailVerified:", emailVerified);
		       console.log("passwordValid:", passwordValid);
		       console.log("passwordMatched:", passwordMatched);
		       console.log("basicFieldsValid:", basicFieldsValid);
		       console.log("최종 결과:", emailVerified && passwordValid && passwordMatched && basicFieldsValid);
		
        signupBtn.disabled = !(emailVerified && passwordValid && passwordMatched && basicFieldsValid);
    }

    // ------------------------------
    // 초기 검증 실행
    // ------------------------------
    validateBasicFields();
    checkPassword();
    updateSubmitButtonState();

});