document.addEventListener("DOMContentLoaded", function () {

    const updateForm = document.getElementById("signupForm");
    const phoneInput = document.getElementById("phone");
    
    // 비밀번호 관련 요소
    const currentPasswordInput = document.getElementById("currentPassword");  // ✨ 추가
    const passwordInput = document.getElementById("password");
    const passwordConfirmInput = document.getElementById("passwordConfirm");
    const togglePasswordBtn = document.getElementById("togglePassword");
    const passwordRuleMsg = document.getElementById("passwordRuleMsg");
    const passwordMatchMsg = document.getElementById("passwordMatchMsg");

    // ============================================
    // 전화번호 자동 하이픈
    // ============================================
    if (phoneInput) {
        phoneInput.addEventListener("input", function() {
            let value = phoneInput.value.replace(/[^0-9]/g, "");
            
            if (value.length > 3 && value.length <= 7) {
                value = value.replace(/(\d{3})(\d+)/, "$1-$2");
            } else if (value.length > 7) {
                value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
            }
            
            phoneInput.value = value;
        });
    }

    // ============================================
    // 비밀번호 보기/숨기기 토글
    // ============================================
    if (togglePasswordBtn && passwordInput && passwordConfirmInput) {
        togglePasswordBtn.addEventListener("click", function() {
            const newType = passwordInput.getAttribute("type") === "password" ? "text" : "password";
            
            passwordInput.setAttribute("type", newType);
            passwordConfirmInput.setAttribute("type", newType);
            
            togglePasswordBtn.textContent = newType === "password" ? "보기" : "숨기기";
        });
    }

    // ============================================
    // 비밀번호 유효성 검사 함수
    // ============================================
    function validatePasswordStrength(password) {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,20}$/;
        return regex.test(password);
    }

    // ============================================
    // 비밀번호 검증 (새 비밀번호 입력 시에만)
    // ============================================
    function checkPassword() {
        // 새 비밀번호가 없으면 검증 안 함 (선택사항)
        if (!passwordInput || !passwordConfirmInput) return;

        const currentPw = currentPasswordInput ? currentPasswordInput.value : "";  // ✨ 추가
        const pw = passwordInput.value;
        const pwC = passwordConfirmInput.value;

        // 1) 비밀번호 유효성 검사 (입력된 경우에만)
        if (pw.length === 0) {
            if (passwordRuleMsg) passwordRuleMsg.textContent = "";
        } else if (currentPw && pw === currentPw) {  // ✨ 추가: 현재 비밀번호와 같은지 체크
            if (passwordRuleMsg) {
                passwordRuleMsg.textContent = "새 비밀번호는 현재 비밀번호와 달라야 합니다.";
                passwordRuleMsg.style.color = "red";
            }
        } else if (!validatePasswordStrength(pw)) {
            if (passwordRuleMsg) {
                passwordRuleMsg.textContent = "비밀번호는 8~20자, 영문 + 숫자 포함해야 합니다.";
                passwordRuleMsg.style.color = "red";
            }
        } else {
            if (passwordRuleMsg) {
                passwordRuleMsg.textContent = "사용 가능한 비밀번호입니다.";
                passwordRuleMsg.style.color = "green";
            }
        }

        // 2) 비밀번호 일치 검사 (확인란에 입력된 경우에만)
        if (pwC.length === 0) {
            if (passwordMatchMsg) passwordMatchMsg.textContent = "";
        } else if (pw === pwC) {
            if (passwordMatchMsg) {
                passwordMatchMsg.textContent = "비밀번호가 일치합니다.";
                passwordMatchMsg.style.color = "green";
            }
        } else {
            if (passwordMatchMsg) {
                passwordMatchMsg.textContent = "비밀번호가 일치하지 않습니다.";
                passwordMatchMsg.style.color = "red";
            }
        }
    }

    // 비밀번호 입력 이벤트 리스너
    if (passwordInput) {
        passwordInput.addEventListener("input", checkPassword);
    }
    if (passwordConfirmInput) {
        passwordConfirmInput.addEventListener("input", checkPassword);
    }
    // ✨ 추가: 현재 비밀번호 입력 시에도 체크
    if (currentPasswordInput) {
        currentPasswordInput.addEventListener("input", checkPassword);
    }

    // ============================================
    // 폼 제출 검증
    // ============================================
    if (updateForm) {
        updateForm.addEventListener("submit", function(e) {
            // 일반 로그인 사용자: 현재 비밀번호 필수
            if (currentPasswordInput && currentPasswordInput.offsetParent !== null) {
                const currentPw = currentPasswordInput.value.trim();
                if (!currentPw) {
                    e.preventDefault();
                    alert("현재 비밀번호를 입력해야 수정할 수 있습니다.");
                    return false;
                }
            }

            // 새 비밀번호 입력한 경우: 확인 일치 체크
            if (passwordInput && passwordConfirmInput) {
                const currentPw = currentPasswordInput ? currentPasswordInput.value : "";
                const pw = passwordInput.value;
                const pwC = passwordConfirmInput.value;

                if (pw.length > 0) {
                    // ✨ 추가: 현재 비밀번호와 같은지 체크
                    if (currentPw && pw === currentPw) {
                        e.preventDefault();
                        alert("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
                        return false;
                    }

                    if (!validatePasswordStrength(pw)) {
                        e.preventDefault();
                        alert("비밀번호는 8~20자, 영문 + 숫자를 포함해야 합니다.");
                        return false;
                    }

                    if (pw !== pwC) {
                        e.preventDefault();
                        alert("비밀번호가 일치하지 않습니다.");
                        return false;
                    }
                }
            }

            // 전화번호 형식 검증
            if (phoneInput) {
                const phoneValue = phoneInput.value.trim();
                if (phoneValue && !/^010-\d{4}-\d{4}$/.test(phoneValue)) {
                    e.preventDefault();
                    alert("전화번호를 올바른 형식으로 입력해주세요. (010-1234-5678)");
                    return false;
                }
            }
        });
    }

});