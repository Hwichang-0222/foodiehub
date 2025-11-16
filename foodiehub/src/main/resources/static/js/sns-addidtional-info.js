/**
 * 
 */document.addEventListener("DOMContentLoaded", function () {

    const additionalInfoForm = document.getElementById("additionalInfoForm");
    const phoneInput = document.getElementById("phone");
    const birthDateInput = document.getElementById("birthDate");
    const genderSelect = document.getElementById("gender");
    const baseAddressInput = document.getElementById("baseAddress");
    const roadAddrInput = document.getElementById("roadAddr");

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
    // 폼 제출 검증
    // ============================================
    if (additionalInfoForm) {
        additionalInfoForm.addEventListener("submit", function(e) {
            
            // 전화번호 검증
            if (phoneInput) {
                const phoneValue = phoneInput.value.trim();
                if (!phoneValue) {
                    e.preventDefault();
                    alert("전화번호를 입력해주세요.");
                    phoneInput.focus();
                    return false;
                }
                
                if (!/^010-\d{4}-\d{4}$/.test(phoneValue)) {
                    e.preventDefault();
                    alert("전화번호를 올바른 형식으로 입력해주세요. (010-1234-5678)");
                    phoneInput.focus();
                    return false;
                }
            }

            // 생년월일 검증
            if (birthDateInput && !birthDateInput.value) {
                e.preventDefault();
                alert("생년월일을 입력해주세요.");
                birthDateInput.focus();
                return false;
            }

            // 성별 검증
            if (genderSelect && !genderSelect.value) {
                e.preventDefault();
                alert("성별을 선택해주세요.");
                genderSelect.focus();
                return false;
            }

            // 주소 검증
            if (baseAddressInput && !baseAddressInput.value) {
                e.preventDefault();
                alert("주소를 검색해주세요.");
                return false;
            }

            if (roadAddrInput && !roadAddrInput.value) {
                e.preventDefault();
                alert("주소를 검색해주세요.");
                return false;
            }
        });
    }

});